package moscow.definevisuals.systems.modules.modules.player;

import lombok.Generated;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.player.ClientPlayerTickEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@ModuleInfo(name = "Auto Sprint", category = ModuleCategory.PLAYER, enabledByDefault = true, desc = "Удерживает спринт при движении вперёд")
public class AutoSprint extends BaseModule {
   private boolean heldByUs;

   private final EventListener<ClientPlayerTickEvent> onUpdateEvent = event -> {
      if (mc.player == null || mc.options == null || mc.getWindow() == null) {
         this.releaseSprintKey();
         return;
      }
      boolean isForwardPressed = this.isPressed(mc.options.forwardKey);
      if (isForwardPressed) {
         mc.options.sprintKey.setPressed(true);
         this.heldByUs = true;
      } else {
         this.releaseSprintKey();
      }
   };

   private boolean isPressed(KeyBinding keyBinding) {
      InputUtil.Key key = InputUtil.fromTranslationKey(keyBinding.getBoundKeyTranslationKey());
      long windowHandle = mc.getWindow().getHandle();
      return switch (key.getCategory()) {
         case KEYSYM, SCANCODE -> InputUtil.isKeyPressed(windowHandle, key.getCode());
         case MOUSE -> GLFW.glfwGetMouseButton(windowHandle, key.getCode()) == 1;
         default -> false;
      };
   }

   private void releaseSprintKey() {
      if (this.heldByUs) {
         this.heldByUs = false;
         if (mc.options != null) {
            mc.options.sprintKey.setPressed(this.isPressed(mc.options.sprintKey));
         }
      }
   }

   @Override
   public void onDisable() {
      this.releaseSprintKey();
      super.onDisable();
   }

   @Generated
   public EventListener<ClientPlayerTickEvent> getOnUpdateEvent() {
      return this.onUpdateEvent;
   }
}
