public class Main {

    public static void main(String[] args) {
        try {
            Configuration configuration = new Configuration("config.json");

            Cluster cluster = new Cluster(configuration);

            cluster.run();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
