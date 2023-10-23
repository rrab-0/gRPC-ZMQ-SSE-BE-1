import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DotEnvLoader {
    public static Map<String, String> loadEnvVariables() {
        String currentDir = System.getProperty("user.dir");
        String filename = currentDir + "/src/main/assets/.env";

        Map<String, String> envVariables = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    envVariables.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return envVariables;
    }
}