import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Configuration {

    private String file;
    private JSONObject json;
    private JSONParser parser = new JSONParser();

    public Configuration(String file) {
        this.file = file;
    }

    public Map<String, User> getUser() {
        Map<String, User> users_map = new HashMap<>();

        JSONArray users = (JSONArray) json.get("users");

        for (Object entry : users) {
            Map<String, Object> map = (Map<String, Object>) entry;

            User user = new User(map);
            users_map.put(user.name, user);
        }

        return users_map;
    }

    public String getEMailFrom() {
        return json.get("email_from").toString();
    }

    public void reload() throws IOException, ParseException {
        json = (JSONObject) parser.parse(new FileReader(file));
    }

}
