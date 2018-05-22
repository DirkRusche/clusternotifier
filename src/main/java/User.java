import java.util.Map;

public class User {

    public final String name;
    public final String email;
    public final boolean waitingJobStarted;
    public final boolean newRunningJob;
    public final boolean runningJobFinished;
    public final boolean jobWaiting;
    public final boolean waitingJobVanished;

    public User(String name, String email, boolean waitingJobStarted, boolean newRunningJob, boolean runningJobFinished, boolean jobWaiting, boolean waitingJobVanished) {
        this.name = name;
        this.email = email;
        this.waitingJobStarted = waitingJobStarted;
        this.newRunningJob = newRunningJob;
        this.runningJobFinished = runningJobFinished;
        this.jobWaiting = jobWaiting;
        this.waitingJobVanished = waitingJobVanished;
    }
    public User(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.email = (String) map.get("email");
        this.waitingJobStarted = (Boolean) map.get("waitingJobStarted");
        this.newRunningJob = (Boolean) map.get("newRunningJob");
        this.runningJobFinished = (Boolean) map.get("runningJobFinished");
        this.jobWaiting = (Boolean) map.get("jobWaiting");
        this.waitingJobVanished = (Boolean) map.get("waitingJobVanished");
    }

    public String toString() {
        return String.format("Email %s waitingJobStarted=%b newRunningJob=%b runningJobFinished=%b jobWaiting=%b waitingJobVanished=%b", email, waitingJobStarted, newRunningJob, runningJobFinished, jobWaiting, waitingJobVanished);
    }
}
