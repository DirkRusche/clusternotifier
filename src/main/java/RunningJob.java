public class RunningJob extends Job {

    public final String t_used;
    public final String t_remain;
    public final String started;
    public final String hosts;

    public RunningJob(String id, String user, String name, String nodes, String ppn, String t_req, String queue, String t_used, String t_remain, String started, String hosts) {
        super(id, user, name, nodes, ppn, t_req, queue);
        this.t_used = t_used;
        this.t_remain = t_remain;
        this.started = started;
        this.hosts = hosts;
    }

    public boolean equals(Object o) {
        if (o == null) { return false; }
        if (o == this) { return true; }
        if (!o.getClass().equals(getClass())) { return false; }

        RunningJob job = (RunningJob) o;

        return job.id == this.id;
    }

}
