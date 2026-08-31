package moscow.definevisuals.utility.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonStorage {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final Path CONFIG_DIR = Path.of("config/definevisuals");

   public static JsonObject read(String fileName) {
      try {
         Path filePath = CONFIG_DIR.resolve(fileName + ".json");
         if (!Files.exists(filePath)) {
            return null;
         }
         String content = Files.readString(filePath);
         return JsonParser.parseString(content).getAsJsonObject();
      } catch (IOException e) {
         return null;
      }
   }

   public static void write(String fileName, JsonObject jsonObject) {
      try {
         if (!Files.exists(CONFIG_DIR)) {
            Files.createDirectories(CONFIG_DIR);
         }
         Path filePath = CONFIG_DIR.resolve(fileName + ".json");
         String content = GSON.toJson(jsonObject);
         Files.writeString(filePath, content);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
