package moscow.definevisuals.mixin.cosmetics;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import moscow.definevisuals.cosmetics.LocalCosmetics;

@Mixin(Keyboard.class)
public abstract class CosmeticKeybindMixin {
   @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
   private void define_onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
      if (action != GLFW.GLFW_PRESS || key != GLFW.GLFW_KEY_F9) {
         return;
      }

      MinecraftClient client = MinecraftClient.getInstance();
      if (client == null || client.currentScreen != null || client.player == null) {
         return;
      }

      int index = LocalCosmetics.cycleNext();
      if (index >= 0 && index < LocalCosmetics.size()) {
         client.player.sendMessage(Text.literal("Косметика: " + LocalCosmetics.name(index)), true);
      }

      ci.cancel();
   }
}
