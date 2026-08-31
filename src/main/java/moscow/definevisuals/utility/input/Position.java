package moscow.definevisuals.utility.input;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public class Position {
   public static float mouseX() {
      return (float)MinecraftClient.getInstance().mouse.getX();
   }

   public static float mouseY() {
      return (float)MinecraftClient.getInstance().mouse.getY();
   }

   public static float screenWidth() {
      Window window = MinecraftClient.getInstance().getWindow();
      return window.getScaledWidth();
   }

   public static float screenHeight() {
      Window window = MinecraftClient.getInstance().getWindow();
      return window.getScaledHeight();
   }
}
