package moscow.definevisuals.systems.modules.constructions.swinganim.presets;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.Generated;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.file.FileManager;
import moscow.definevisuals.utility.game.MessageUtility;
import net.minecraft.text.Text;

public class SwingPresetManager {
   private final List<SwingPresetFile> swingPresetFiles = new ArrayList<>();
   private SwingPresetFile current;
   private boolean initialized = false;
   public void handle() {
      if (this.getAutoSavePreset() == null) {
         this.createPreset("autosave");
      }

      if (!this.initialized) {
         this.scanPresetDirectory();
         this.initialized = true;
      }
   }

   public void directionPreset() {
      try {
         File presetDir = new File(FileManager.DIRECTORY + "/presets", "swing");
         if (!presetDir.exists() && !presetDir.mkdirs()) {
            throw new IOException("Не удалось создать папку пресетов");
         }
         if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            throw new IOException("Открытие папок не поддерживается системой");
         }
         Desktop.getDesktop().open(presetDir);
      } catch (Exception var3) {
         DefineVisuals.LOGGER.error("Не удалось открыть папку пресетов: {}", var3.getMessage());
      }
   }

   public void createPreset(String name) {
      if (name != null) {
         if (this.getPreset(name, false) != null) {
            DefineVisuals.LOGGER.warn("Preset {} already exists", name);
         } else {
            SwingPresetFile preset = new SwingPresetFile(name);
            if (name.equals("autosave")) {
               preset.load();
            }

            preset.save();
            this.swingPresetFiles.add(preset);
         }
      }
   }

   public void listPresets() {
      MessageUtility.info(Text.of("Список конфигов:"));

      for (SwingPresetFile swingPresetFile : this.swingPresetFiles) {
         int idx = this.swingPresetFiles.indexOf(swingPresetFile) + 1;
         MessageUtility.info(Text.of("[" + idx + "] " + swingPresetFile.getFileName()));
      }
   }

   private void scanPresetDirectory() {
      this.swingPresetFiles.clear();
      Path presetPath = Paths.get(FileManager.DIRECTORY + "/presets", "swing");
      if (!Files.exists(presetPath)) {
         try {
            Files.createDirectories(presetPath);
         } catch (IOException var5) {
            DefineVisuals.LOGGER.error("Не удалось создать директорию пресетов: {}", var5.getMessage());
         }
      } else {
         try (Stream<Path> stream = Files.list(presetPath)) {
            stream.filter(x$0 -> Files.isRegularFile(x$0)).filter(path -> path.toString().endsWith(".def") || path.toString().endsWith(".rock")).forEach(path -> {
               String fileName = path.getFileName().toString();
               String name = fileName.substring(0, fileName.lastIndexOf(46));
               SwingPresetFile swingPresetFile = new SwingPresetFile(name);
               this.swingPresetFiles.add(swingPresetFile);
            });
         } catch (IOException var8) {
            DefineVisuals.LOGGER.error("Ошибка при сканировании директории конфигов: {}", var8.getMessage());
         }
      }
   }

   public SwingPresetFile getPreset(String name, boolean rescan) {
      if (rescan) {
         this.scanPresetDirectory();
      }

      return this.swingPresetFiles.stream().filter(swingPresetFile -> swingPresetFile.getFileName().equalsIgnoreCase(name)).findFirst().orElse(null);
   }

   public SwingPresetFile getPreset(String name) {
      return this.getPreset(name, false);
   }

   public SwingPresetFile getAutoSavePreset() {
      return this.getPreset("autosave", true);
   }

   public void refresh() {
      this.scanPresetDirectory();
   }

   @Generated
   public List<SwingPresetFile> getSwingPresetFiles() {
      return this.swingPresetFiles;
   }

   @Generated
   public SwingPresetFile getCurrent() {
      return this.current;
   }

   @Generated
   public boolean isInitialized() {
      return this.initialized;
   }

   @Generated
   public void setCurrent(SwingPresetFile current) {
      this.current = current;
   }
}
