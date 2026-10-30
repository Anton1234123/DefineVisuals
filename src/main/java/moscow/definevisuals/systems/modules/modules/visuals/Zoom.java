package moscow.definevisuals.systems.modules.modules.visuals;

import lombok.Generated;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.player.ClientPlayerTickEvent;
import moscow.definevisuals.systems.event.impl.window.KeyPressEvent;
import moscow.definevisuals.systems.event.impl.window.MouseEvent;
import moscow.definevisuals.systems.event.impl.window.MouseScrollEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.BindSetting;
import moscow.definevisuals.systems.setting.settings.BooleanSetting;
import moscow.definevisuals.systems.setting.settings.SliderSetting;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

@ModuleInfo(
   name = "Zoom",
   category = ModuleCategory.VISUALS,
   desc = "Плавное приближение камеры как в OptiFine и Zoomify",
   enabledByDefault = true
)
public class Zoom extends BaseModule {
   private final BindSetting activationKey = new BindSetting(this, "modules.settings.zoom.key").key(GLFW.GLFW_KEY_C);
   private final SliderSetting zoomAmount = new SliderSetting(this, "modules.settings.zoom.amount")
      .min(2.0f)
      .max(20.0f)
      .step(0.5f)
      .currentValue(4.0f)
      .suffix("x");
   private final BooleanSetting smooth = new BooleanSetting(this, "modules.settings.zoom.smooth").enabled(true);
   private final SliderSetting transitionDuration = new SliderSetting(this, "modules.settings.zoom.duration", () -> !this.smooth.isEnabled())
      .min(50.0f)
      .max(400.0f)
      .step(25.0f)
      .currentValue(175.0f)
      .suffix(" ms");
   private final BooleanSetting scrollAdjust = new BooleanSetting(this, "modules.settings.zoom.scroll").enabled(true);
   private final Animation zoomAnimation = new Animation(175L, Easing.FIGMA_EASE_IN_OUT);
   private boolean active;

   private final EventListener<KeyPressEvent> onKeyPress = event -> {
      if (mc.currentScreen != null || !this.isMatchingKey(event.getKey())) {
         return;
      }
      if (event.getAction() == GLFW.GLFW_PRESS) {
         this.active = true;
      } else if (event.getAction() == GLFW.GLFW_RELEASE) {
         this.active = false;
      }
   };

   private final EventListener<MouseEvent> onMouseButton = event -> {
      if (mc.currentScreen != null || !this.isMatchingKey(event.getButton())) {
         return;
      }
      if (event.getAction() == GLFW.GLFW_PRESS) {
         this.active = true;
      } else if (event.getAction() == GLFW.GLFW_RELEASE) {
         this.active = false;
      }
   };

   private final EventListener<ClientPlayerTickEvent> onTick = event -> {
      boolean shouldZoom = mc.player != null
         && mc.world != null
         && mc.currentScreen == null
         && this.isActivationKeyDown();
      this.active = shouldZoom;
   };

   private final EventListener<MouseScrollEvent> onMouseScroll = event -> {
      if (!this.active || mc.currentScreen != null || !this.scrollAdjust.isEnabled()) {
         return;
      }

      float step = event.getVerticalAmount() > 0.0 ? 0.5f : -0.5f;
      this.zoomAmount.setCurrentValue(MathHelper.clamp(
         this.zoomAmount.getCurrentValue() + step,
         this.zoomAmount.getMin(),
         this.zoomAmount.getMax()
      ));
      event.cancel();
   };

   public float applyFov(float fov) {
      boolean target = this.isEnabled() && this.active;
      float progress;
      if (this.smooth.isEnabled()) {
         this.zoomAnimation.setDuration((long)this.transitionDuration.getCurrentValue());
         this.zoomAnimation.setEasing(Easing.FIGMA_EASE_IN_OUT);
         this.zoomAnimation.update(target);
         progress = this.zoomAnimation.getValue();
      } else {
         progress = target ? 1.0f : 0.0f;
         this.zoomAnimation.setValue(progress);
      }

      if (progress <= 0.001f) {
         return fov;
      }
      float factor = 1.0f + (this.zoomAmount.getCurrentValue() - 1.0f) * progress;
      return Math.max(1.0f, fov / factor);
   }

   private boolean isMatchingKey(int key) {
      return this.activationKey.getKey() != -1 && this.activationKey.getKey() == key;
   }

   private boolean isActivationKeyDown() {
      int key = this.activationKey.getKey();
      if (key == -1 || mc.getWindow() == null) {
         return false;
      }
      long handle = mc.getWindow().getHandle();
      if (key >= GLFW.GLFW_MOUSE_BUTTON_1 && key <= GLFW.GLFW_MOUSE_BUTTON_LAST) {
         return GLFW.glfwGetMouseButton(handle, key) == GLFW.GLFW_PRESS;
      }
      return InputUtil.isKeyPressed(handle, key);
   }

   @Override
   public void onDisable() {
      this.active = false;
      super.onDisable();
   }

   @Generated
   public boolean isActive() {
      return this.active;
   }

   @Generated
   public BindSetting getActivationKey() {
      return this.activationKey;
   }

   @Generated
   public SliderSetting getZoomAmount() {
      return this.zoomAmount;
   }
}
