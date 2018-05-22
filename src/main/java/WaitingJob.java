public class WaitingJob extends Job {

    public final String prio;
    public final String enqueued;
    public final String waiting;
    public final String est_hosts;

    public WaitingJob(String id, String user, String name, String nodes, String ppn,
        String t_req, String queue, String prio, String enqueued, String waiting,
        String est_hosts) {
        super(id, user, name, nodes, ppn, t_req, queue);
        this.prio = prio;
        this.enqueued = enqueued;
        this.waiting = waiting;
        this.est_hosts = est_hosts;
    }

    public boolean equals(Object o) {
        if (o == null) { return false; }
        if (o == this) { return true; }
        if (!o.getClass().equals(getClass())) { return false; }

        WaitingJob job = (WaitingJob) o;

        return job.id == this.id;
    }
}
