package moscow.definevisuals.systems.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import lombok.Generated;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.file.FileManager;
import moscow.definevisuals.systems.notifications.NotificationType;
import moscow.definevisuals.utility.game.MessageUtility;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.glfw.GLFWDropCallbackI;

public final class ConfigDropHandler implements IMinecraft {
   private static boolean initialized;
   public static void init() {
      if (!initialized) {
         initialized = true;
         long handle = mc.getWindow().getHandle();
         GLFWDropCallbackI[] previous = new GLFWDropCallbackI[1];
         GLFWDropCallbackI callback = (window, count, names) -> {
            if (previous[0] != null) {
               previous[0].invoke(window, count, names);
            }

            for (int i = 0; i < count; i++) {
               String path = GLFWDropCallback.getName(names, i);
               handleDrop(path);
            }
         };
         previous[0] = GLFW.glfwSetDropCallback(handle, callback);
      }
   }

   private static void handleDrop(String path) {
      try {
         File src = new File(path);
         if (!src.isFile()) {
            return;
         }

         if (!src.getName().endsWith(".def") && !src.getName().endsWith(".rock")) {
            return;
         }

         File destDir = new File(FileManager.DIRECTORY, "configs");
         if (!destDir.exists() && !destDir.mkdirs()) {
            DefineVisuals.LOGGER.error("Failed to create directory {}", destDir.getAbsolutePath());
            return;
         }

         File dest = new File(destDir, src.getName());
         Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
         String name = src.getName().substring(0, src.getName().lastIndexOf(46));
         ConfigManager manager = DefineVisuals.getInstance().getConfigManager();
         manager.refresh();
         ConfigFile cfg = manager.getConfig(name);
         if (cfg == null) {
            cfg = new ConfigFile(name);
            manager.getConfigFiles().add(cfg);
         }

         cfg.load();
         MessageUtility.info(Text.of("Конфиг " + name + " загружен"));
         DefineVisuals.getInstance().getNotificationManager().addNotification(NotificationType.SUCCESS, Text.translatable("configs.loaded").getString());
      } catch (Exception var7) {
         DefineVisuals.LOGGER.error("Failed to load dropped config {}", path, var7);
      }
   }

   @Generated
   private ConfigDropHandler() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
