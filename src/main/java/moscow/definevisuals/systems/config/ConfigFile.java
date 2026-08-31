package moscow.definevisuals.systems.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import lombok.Generated;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.file.FileManager;
import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.systems.modules.Module;
import moscow.definevisuals.systems.modules.exception.UnknownModuleException;
import moscow.definevisuals.systems.modules.modules.other.Sounds;
import moscow.definevisuals.systems.modules.modules.visuals.MenuModule;
import moscow.definevisuals.systems.notifications.NotificationType;
import moscow.definevisuals.systems.setting.Setting;
import moscow.definevisuals.utility.game.MessageUtility;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import moscow.definevisuals.utility.sounds.ClientSounds;
import net.minecraft.text.Text;

public class ConfigFile implements IMinecraft {
   private List<Module> modules = DefineVisuals.getInstance().getModuleManager().getModules();
   private File file;
   private String fileName;

   public ConfigFile(String fileName) {
      this.fileName = fileName;
      File configsFolder = new File(FileManager.DIRECTORY, "configs");
      if (!configsFolder.exists()) {
         configsFolder.mkdir();
      }

      this.file = new File(configsFolder, fileName + ".%s".formatted("def"));
   }

   public void load() {
      if (!this.file.exists()) {
         DefineVisuals.LOGGER.warn("Config file not found: {}", this.file.getAbsolutePath());
      } else {
         try {
            try (BufferedReader reader = new BufferedReader(new FileReader(this.file))) {
               JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
               if (jsonObject.has("modules")) {
                  JsonArray modulesArray = jsonObject.getAsJsonArray("modules");
                  int loadedModules = 0;

                  for (JsonElement moduleElement : modulesArray) {
                     JsonObject moduleObject = moduleElement.getAsJsonObject();
                     if (moduleObject.has("name")) {
                        String moduleName = moduleObject.get("name").getAsString();
                        boolean enabled = moduleObject.has("enabled") && moduleObject.get("enabled").getAsBoolean();
                        int key = moduleObject.has("key") ? moduleObject.get("key").getAsInt() : 0;

                        try {
                           Module module = DefineVisuals.getInstance().getModuleManager().getModule(moduleName);
                           if (!(module instanceof MenuModule)) {
                              // Use silent=false to ensure event listeners are subscribed
                              module.setEnabled(enabled, false);
                              module.setKey(key);
                           }

                           if (moduleObject.has("settings")) {
                              JsonObject settingsObject = moduleObject.getAsJsonObject("settings");

                              for (Setting setting : module.getSettings()) {
                                 if (settingsObject.has(setting.getName())) {
                                    setting.load(settingsObject.get(setting.getName()));
                                 }
                              }
                           }

                           loadedModules++;
                        } catch (UnknownModuleException var16) {
                           DefineVisuals.LOGGER.warn("Module not found during config load: {}", moduleName);
                        }
                     }
                  }

                  ClientSounds.MODULE.play(DefineVisuals.getInstance().getModuleManager().getModule(Sounds.class).getVolume().getCurrentValue(), 1.0F);
                  DefineVisuals.getInstance().getNotificationManager().addNotification(NotificationType.SUCCESS, Localizator.translate("configs.loaded"));
                  DefineVisuals.LOGGER.info("Loaded {} modules from config {}", loadedModules, this.fileName);
                  if (!this.fileName.equals("autosave")) {
                     DefineVisuals.getInstance().getConfigManager().setCurrent(this);
                  }

                  return;
               }

               DefineVisuals.LOGGER.warn("Invalid config format: missing 'modules' array in {}", this.fileName);
            }
         } catch (Exception var18) {
            DefineVisuals.LOGGER.error("Failed to load config file {}: {}", this.fileName, var18.getMessage());
         }
      }
   }

   public void save() {
      try {
         if (!this.file.exists() && !this.file.createNewFile()) {
            throw new IOException("Failed to create config file: " + this.file.getAbsolutePath());
         }

         JsonObject json = new JsonObject();
         JsonArray modulesJsonArray = this.getModulesJsonArray();
         json.add("modules", modulesJsonArray);
         FileWriter fileWriter = new FileWriter(this.file);

         try {
            fileWriter.write(FileManager.GSON.toJson(json));
         } catch (Throwable var7) {
            try {
               fileWriter.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }

            throw var7;
         }

         fileWriter.close();
         if (!this.fileName.equals("autosave")) {
            DefineVisuals.getInstance().getConfigManager().setCurrent(this);
         }

         DefineVisuals.LOGGER.info("Successfully saved config " + this.fileName);
      } catch (IOException var8) {
         DefineVisuals.LOGGER.error("Failed to save config file", var8);
      }
   }

   public void delete() {
      if (this.file.exists() && this.file.delete()) {
         DefineVisuals.getInstance().getConfigManager().getConfigFiles().remove(this);
         MessageUtility.info(Text.of("Конфиг " + this.fileName + " успешно удален"));
         DefineVisuals.LOGGER.info("Config file deleted: {}", this.file.getAbsolutePath());
      } else {
         MessageUtility.error(Text.of("Произошла ошибка при удалении"));
         DefineVisuals.LOGGER.warn("Failed to delete config file: {}", this.file.getAbsolutePath());
      }
   }

   private JsonArray getModulesJsonArray() {
      JsonArray modulesJsonArray = new JsonArray();

      for (Module module : this.modules) {
         JsonObject moduleObject = new JsonObject();
         moduleObject.addProperty("name", module.getName());
         moduleObject.addProperty("enabled", module.isEnabled());
         moduleObject.addProperty("key", module.getKey());
         moduleObject.add("settings", this.getSettingsJsonObject(module));
         modulesJsonArray.add(moduleObject);
      }

      return modulesJsonArray;
   }

   private JsonObject getSettingsJsonObject(Module module) {
      JsonObject settingsObject = new JsonObject();

      for (Setting setting : module.getSettings()) {
         settingsObject.add(setting.getName(), setting.save());
      }

      return settingsObject;
   }

   @Generated
   public String getFileName() {
      return this.fileName;
   }
}
