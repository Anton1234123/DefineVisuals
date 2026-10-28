package moscow.definevisuals.systems.modules.modules.player;

import lombok.Generated;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.player.ClientPlayerTickEvent;
import moscow.definevisuals.systems.event.impl.window.KeyPressEvent;
import moscow.definevisuals.systems.event.impl.window.MouseEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.BindSetting;
import moscow.definevisuals.systems.setting.settings.BooleanSetting;
import moscow.definevisuals.systems.setting.settings.ModeSetting;
import moscow.definevisuals.systems.setting.settings.SliderSetting;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

@ModuleInfo(
   name = "FreeLook",
   category = ModuleCategory.PLAYER,
   desc = "Свободный круговой обзор камерой во время движения",
   enabledByDefault = true
)
public class FreeLook extends BaseModule {
   private final BindSetting key = new BindSetting(this, "Клавиша").key(GLFW.GLFW_KEY_LEFT_ALT);
   private final ModeSetting mode = new ModeSetting(this, "Режим");
   private final ModeSetting.Value holdMode = new ModeSetting.Value(this.mode, "Hold");
   private final ModeSetting.Value toggleMode = new ModeSetting.Value(this.mode, "Toggle");

   private final ModeSetting perspective = new ModeSetting(this, "Перспектива");
   private final ModeSetting.Value thirdPersonMode = new ModeSetting.Value(this.perspective, "3-е лицо");
   private final ModeSetting.Value firstPersonMode = new ModeSetting.Value(this.perspective, "1-е лицо");

   private final SliderSetting distance = new SliderSetting(this, "Дистанция")
      .min(2.0f)
      .max(10.0f)
      .step(0.5f)
      .currentValue(4.0f);

   private final BooleanSetting invertPitch = new BooleanSetting(this, "Инверсия Y").enabled(false);

   private boolean active = false;
   private Perspective previousPerspective;
   private float cameraYaw = 0.0f;
   private float cameraPitch = 0.0f;

   private final EventListener<KeyPressEvent> onKeyPress = event -> {
      if (mc.currentScreen != null) return;
      if (this.isMatchingKey(event.getKey())) {
         if (this.holdMode.isSelected()) {
            if (event.getAction() == 1) {
               this.activate();
            } else if (event.getAction() == 0) {
               this.deactivate();
            }
         } else if (this.toggleMode.isSelected()) {
            if (event.getAction() == 1) {
               this.toggleActive();
            }
         }
      }
   };

   private final EventListener<MouseEvent> onMouseEvent = event -> {
      if (mc.currentScreen != null) return;
      if (this.isMatchingKey(event.getButton())) {
         if (this.holdMode.isSelected()) {
            if (event.getAction() == 1) {
               this.activate();
            } else if (event.getAction() == 0) {
               this.deactivate();
            }
         } else if (this.toggleMode.isSelected()) {
            if (event.getAction() == 1) {
               this.toggleActive();
            }
         }
      }
   };

   private final EventListener<ClientPlayerTickEvent> onTick = event -> {
      if (mc.player == null) {
         if (this.active) {
            this.deactivate();
         }
         return;
      }

      if (this.active && (mc.currentScreen != null || (this.holdMode.isSelected() && !this.isKeyDown(this.getBoundKey())))) {
         this.deactivate();
      }
   };

   public void activate() {
      if (this.active || mc.player == null || mc.options == null) return;
      this.previousPerspective = mc.options.getPerspective();
      this.cameraYaw = mc.player.getYaw();
      this.cameraPitch = mc.player.getPitch();

      if (this.thirdPersonMode.isSelected()) {
         mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
      }
      this.active = true;
   }

   public void deactivate() {
      if (!this.active) return;
      this.active = false;
      if (mc.options != null && this.previousPerspective != null) {
         mc.options.setPerspective(this.previousPerspective);
      }
      this.previousPerspective = null;
   }

   public void toggleActive() {
      if (this.active) {
         this.deactivate();
      } else {
         this.activate();
      }
   }

   public void updateCameraAngles(double cursorDeltaX, double cursorDeltaY) {
      float deltaYaw = (float)(cursorDeltaX * 0.15);
      float deltaPitch = (float)(cursorDeltaY * 0.15);
      if (this.invertPitch.isEnabled()) {
         deltaPitch = -deltaPitch;
      }
      this.cameraYaw += deltaYaw;
      this.cameraPitch += deltaPitch;
      this.cameraPitch = MathHelper.clamp(this.cameraPitch, -90.0f, 90.0f);
   }

   private int getBoundKey() {
      int k = this.key.getKey();
      if (k != -1) return k;
      return this.getKey();
   }

   private boolean isMatchingKey(int pressedKey) {
      int bound = this.getBoundKey();
      if (bound == -1) return false;
      if ((bound == GLFW.GLFW_KEY_LEFT_ALT || bound == GLFW.GLFW_KEY_RIGHT_ALT)
          && (pressedKey == GLFW.GLFW_KEY_LEFT_ALT || pressedKey == GLFW.GLFW_KEY_RIGHT_ALT)) {
         return true;
      }
      return bound == pressedKey;
   }

   private boolean isKeyDown(int boundKey) {
      if (mc.getWindow() == null) return false;
      long handle = mc.getWindow().getHandle();
      if (boundKey >= 0 && boundKey <= 8) {
         return GLFW.glfwGetMouseButton(handle, boundKey) == 1;
      }
      if (boundKey == GLFW.GLFW_KEY_LEFT_ALT || boundKey == GLFW.GLFW_KEY_RIGHT_ALT) {
         return InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_ALT)
             || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_ALT);
      }
      return InputUtil.isKeyPressed(handle, boundKey);
   }

   @Override
   public void onDisable() {
      this.deactivate();
      super.onDisable();
   }

   @Generated
   public boolean isActive() {
      return this.active;
   }

   @Generated
   public float getCameraYaw() {
      return this.cameraYaw;
   }

   @Generated
   public float getCameraPitch() {
      return this.cameraPitch;
   }

   public float getCameraDistance() {
      return this.distance.getCurrentValue();
   }
}
