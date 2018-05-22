public class Job {
    public final String id;
    public final String user;
    public final String name;
    public final String nodes;
    public final String ppn;
    public final String t_req;
    public final String queue;


    public Job(String id, String user, String name, String nodes, String ppn, String t_req,
        String queue) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.nodes = nodes;
        this.ppn = ppn;
        this.t_req = t_req;
        this.queue = queue;
    }

    public boolean equals(Object o) {
        if (o == null) { return false; }
        if (o == this) { return true; }
        if (!o.getClass().equals(getClass())) { return false; }

        Job job = (Job) o;

        return job.id == this.id;
    }

    public String toString() {
        return String.format("#%s, %s %s", id, user, name);
    }
}
