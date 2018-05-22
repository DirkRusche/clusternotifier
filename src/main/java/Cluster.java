import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Cluster {

    private Configuration configuration;
    private String from;
    private Map<String, User> users;

    private Map<String, RunningJob> running_old = new HashMap<>();
    private Map<String, WaitingJob> waiting_old = new HashMap<>();
    private Map<String, RunningJob> running_tmp;
    private Map<String, WaitingJob> waiting_tmp;

    public Cluster(Configuration configuration) throws IOException, ParseException {
        this.configuration = configuration;

        configuration.reload();
        this.from = configuration.getEMailFrom();
        this.users = configuration.getUser();
    }

    public void run() throws InterruptedException {
        System.out.println("Starting");

        while (true) {
            running_tmp = new HashMap<>();
            waiting_tmp = new HashMap<>();

            Document doc = Jsoup.parse(readURL("http://www.wr.inf.h-brs.de/wr/stat/batch.xhtml"));

            Elements html_running_jobs = doc.select("table.jobtable tr");
            Elements html_waiting_jobs = doc.select("table.waittable tr");

            for (Element entry : html_running_jobs) {
                RunningJob runningJob = createRunningJob(entry);
                if (runningJob == null) { continue; }

                running_tmp.put(runningJob.id, runningJob);
            }

            for (Element entry : html_waiting_jobs) {
                WaitingJob waitingJob = createWaitingJob(entry);
                if (waitingJob == null) { continue; }

                waiting_tmp.put(waitingJob.id, waitingJob);
            }

            checkChanges();

            running_old = new HashMap<>(running_tmp);
            waiting_old = new HashMap<>(waiting_tmp);

            Thread.sleep(10 * 1000);

            if (Math.random() >= 0.8) { // lol
                try {
                    configuration.reload();
                    users = configuration.getUser();
                } catch (IOException | ParseException e) {
                    System.err.println("Something is wrong with the JSON");
                }
            }
        }
    }

    private RunningJob createRunningJob(Element entry) {
        String id = entry.child(1).html();
        id = id.substring(id.indexOf("</a>") + 4);

        String queue = entry.child(2).html();
        String user = entry.child(3).html();
        String proc = entry.child(4).html();
        String nodes = entry.child(5).html();
        String ppn = entry.child(6).html();
        String vmem = entry.child(7).html();
        String t_remain = entry.child(8).html();
        String t_req = entry.child(9).html();
        String t_used = entry.child(10).html();
        String started = entry.child(11).html();
        String name = entry.child(12).html();
        String hosts = entry.child(13).html();

        if  (!users.containsKey(user)) {
            return null;
        }
        if (id.isEmpty()) {
            return null;
        }

        return new RunningJob(id, user, name, nodes, ppn, t_req, queue, t_used, t_remain, started, hosts);
    }

    private WaitingJob createWaitingJob(Element entry) {
        String id = entry.child(0).html();
        id = id.substring(id.indexOf("</a>") + 4);

        String queue = entry.child(1).html();
        String user = entry.child(2).html();
        String state = entry.child(3).html();
        String proc = entry.child(4).html();
        String nodes = entry.child(5).html();
        String ppn = entry.child(6).html();
        String vmem = entry.child(7).html();
        String t_req = entry.child(8).html();
        String prio = entry.child(9).html();
        String enqueued = entry.child(10).html();
        String waiting = entry.child(11).html();
        String name = entry.child(12).html();
        String est_hosts = entry.child(13).html();

        if  (!users.containsKey(user)) {
            return null;
        }
        if (id.isEmpty()) {
            return null;
        }

        return new WaitingJob(id, user, name, nodes, ppn, t_req, queue, prio, enqueued, waiting, est_hosts);
    }

    private void checkChanges() {

        for (String id : running_tmp.keySet()) {
            Job j = running_tmp.get(id);
            User user = users.get(j.user);

            if (waiting_old.containsKey(id) && user.waitingJobStarted) {
                sendMail(user.email, "Job started " + id, j.toString());
            }
            if (!running_old.containsKey(id) && user.newRunningJob) {
                sendMail(user.email, "New running " + id, j.toString());
            }
        }
        for (String id : running_old.keySet()) {
            Job j = running_old.get(id);
            User user = users.get(j.user);

            if (!running_tmp.containsKey(id) && user.runningJobFinished) {
                sendMail(user.email, "Job ended " + id, j.toString());
            }
        }
        for (String id : waiting_tmp.keySet()) {
            Job j = waiting_tmp.get(id);
            User user = users.get(j.user);

            if (!waiting_old.containsKey(id) && user.jobWaiting) {
                sendMail(user.email, "Job waiting " + id, j.toString());
            }
        }
        for (String id : waiting_old.keySet()) {
            Job j = waiting_old.get(id);
            User user = users.get(j.user);

            if (!waiting_tmp.containsKey(id) && !running_tmp.containsKey(id) && user.waitingJobVanished) {
                sendMail(user.email, "Waiting job vanished " + id, j.toString());
            }
        }
    }


    public String readURL(String url) {
        // https://stackoverflow.com/questions/13140552/how-t-get-specific-value-from-html-in-java

        String fileContents = "";
        String currentLine = "";

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
            fileContents = reader.readLine();
            while (currentLine != null) {
                currentLine = reader.readLine();
                fileContents += "\n" + currentLine;
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileContents;
    }

    public void sendMail(String to, String subject, String text) {
        // https://www.tutorialspoint.com/java/java_sending_email.htm

        String from = this.from;

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", "localhost");
        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

}
