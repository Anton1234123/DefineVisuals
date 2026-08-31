package moscow.definevisuals.mixin.minecraft.client.gui.screen;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.ui.QuitConfirmScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

@Mixin(targets = "net.minecraft.client.gui.screen.GameMenuScreen", priority = 1000)
public class PauseScreenMixin extends Screen {

   protected PauseScreenMixin(Text title) {
      super(title);
   }

   @Inject(method = "render", at = @At("HEAD"))
   private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
   }

   @Inject(method = "init", at = @At("RETURN"))
   private void onInit(CallbackInfo ci) {
      if (shouldShowWarning()) {
         // Replace the save and quit button action using reflection
         for (var widget : this.children()) {
            if (widget instanceof ButtonWidget button) {
               String buttonText = button.getMessage().getString();
               if (buttonText.contains("Save") || buttonText.contains("Сохранить") || buttonText.contains("Quit") || buttonText.contains("Выйти")) {
                  try {
                     Field onPressField = ButtonWidget.class.getDeclaredField("onPress");
                     onPressField.setAccessible(true);
                     onPressField.set(button, (Runnable) () -> {
                        MinecraftClient.getInstance().setScreen(new QuitConfirmScreen(this));
                     });
                  } catch (Exception e) {
                     e.printStackTrace();
                  }
               }
            }
         }
      }
   }
   
   private boolean shouldShowWarning() {
      var shulkerSafe = DefineVisuals.getInstance().getModuleManager().getModule("ShulkerSafe");
      if (shulkerSafe == null || !shulkerSafe.isEnabled()) {
         return false;
      }
      
      // Check if player has 2 or more shulkers
      if (MinecraftClient.getInstance().player == null) {
         return false;
      }
      
      int shulkerCount = 0;
      for (var stack : MinecraftClient.getInstance().player.getInventory().main) {
         if (isShulkerBox(stack)) {
            shulkerCount += stack.getCount();
         }
      }
      
      return shulkerCount >= 2;
   }
   
   private boolean isShulkerBox(net.minecraft.item.ItemStack stack) {
      if (stack == null || stack.isEmpty()) return false;
      if (stack.getItem() instanceof net.minecraft.item.BlockItem blockItem) {
         return blockItem.getBlock() instanceof net.minecraft.block.ShulkerBoxBlock;
      }
      return false;
   }
}
