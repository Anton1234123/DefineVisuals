package moscow.definevisuals.systems.modules.modules.player;

import lombok.Generated;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.player.ClientPlayerTickEvent;
import moscow.definevisuals.systems.event.impl.window.KeyPressEvent;
import moscow.definevisuals.systems.event.impl.window.MouseMoveEvent;
import moscow.definevisuals.systems.event.impl.window.MouseScrollEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.BindSetting;
import moscow.definevisuals.systems.setting.settings.ModeSetting;
import moscow.definevisuals.systems.setting.settings.SliderSetting;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import net.minecraft.client.util.InputUtil;

@ModuleInfo(name = "FreeLook", category = ModuleCategory.PLAYER, desc = "Позволяет смотреть вокруг без движения камеры")
public class FreeLook extends BaseModule {
    private final ModeSetting mode = new ModeSetting(this, "Режим");
    private final ModeSetting.Value modeToggle = new ModeSetting.Value(this.mode, "Toggle");
    private final ModeSetting.Value modeHold = new ModeSetting.Value(this.mode, "Hold");
    
    private final BindSetting keyBind = new BindSetting(this, "Клавиша").key(76); // L key by default
    
    private final SliderSetting zoomLevel = new SliderSetting(this, "Zoom")
        .min(0.5f)
        .max(3.0f)
        .step(0.1f)
        .currentValue(1.0f);
    
    private final SliderSetting smoothness = new SliderSetting(this, "Плавность")
        .min(0.1f)
        .max(1.0f)
        .step(0.05f)
        .currentValue(0.5f);

    private final Animation yawAnimation = new Animation(200L, Easing.QUAD_OUT);
    private final Animation pitchAnimation = new Animation(200L, Easing.QUAD_OUT);
    private final Animation zoomAnimation = new Animation(150L, Easing.QUAD_OUT);

    private float targetYaw = 0.0f;
    private float targetPitch = 0.0f;
    private float currentYaw = 0.0f;
    private float currentPitch = 0.0f;
    private float targetZoom = 1.0f;
    private float currentZoom = 1.0f;

    private boolean isActive = false;
    private boolean keyHeld = false;

    private final EventListener<ClientPlayerTickEvent> onTick = event -> {
        if (mc.player == null || mc.options == null) return;

        // Handle hold mode
        if (this.modeHold.isSelected()) {
            boolean isKeyPressed = isKeyHeld();
            if (isKeyPressed && !keyHeld) {
                keyHeld = true;
                isActive = true;
            } else if (!isKeyPressed && keyHeld) {
                keyHeld = false;
                isActive = false;
                resetCamera();
            }
        }

        // Update animations
        float smoothValue = smoothness.getCurrentValue();
        yawAnimation.setDuration((long)(200 * (1.1f - smoothValue)));
        pitchAnimation.setDuration((long)(200 * (1.1f - smoothValue)));
        zoomAnimation.setDuration((long)(150 * (1.1f - smoothValue)));

        if (isActive) {
            yawAnimation.update(1.0f);
            pitchAnimation.update(1.0f);
            zoomAnimation.update(1.0f);
        } else {
            yawAnimation.update(0.0f);
            pitchAnimation.update(0.0f);
            zoomAnimation.update(1.0f);
        }

        currentYaw = lerp(currentYaw, targetYaw, yawAnimation.getValue() * 0.15f);
        currentPitch = lerp(currentPitch, targetPitch, pitchAnimation.getValue() * 0.15f);
        currentZoom = lerp(currentZoom, targetZoom, zoomAnimation.getValue() * 0.2f);
    };

    private final EventListener<KeyPressEvent> onKeyPress = event -> {
        if (mc.player == null) return;
        
        if (event.getKey() == keyBind.getKey() && event.getAction() == 1) {
            if (this.modeToggle.isSelected()) {
                isActive = !isActive;
                if (!isActive) {
                    resetCamera();
                }
            }
        }
    };

    private final EventListener<MouseScrollEvent> onScroll = event -> {
        if (!isActive || mc.player == null) return;
        
        double scroll = event.getVerticalAmount();
        if (scroll > 0) {
            targetZoom = Math.max(zoomLevel.getMin(), targetZoom - 0.1f);
        } else if (scroll < 0) {
            targetZoom = Math.min(zoomLevel.getMax(), targetZoom + 0.1f);
        }
    };

    private final EventListener<MouseMoveEvent> onMouseMove = event -> {
        if (!isActive || mc.player == null) return;
        
        float sensitivity = 0.15f;
        float deltaX = (float)event.getDeltaX() * sensitivity;
        float deltaY = (float)event.getDeltaY() * sensitivity;
        
        targetYaw += deltaX;
        targetPitch -= deltaY;
        
        // Clamp pitch to prevent flipping
        targetPitch = Math.max(-90.0f, Math.min(90.0f, targetPitch));
    };

    @Override
    public void onEnable() {
        if (this.modeToggle.isSelected()) {
            isActive = true;
        }
    }

    @Override
    public void onDisable() {
        isActive = false;
        keyHeld = false;
        resetCamera();
    }

    private void resetCamera() {
        targetYaw = 0.0f;
        targetPitch = 0.0f;
        targetZoom = 1.0f;
    }

    private boolean isKeyHeld() {
        if (mc.getWindow() == null) return false;
        return InputUtil.isKeyPressed(mc.getWindow().getHandle(), keyBind.getKey());
    }

    private float lerp(float start, float end, float alpha) {
        return start + (end - start) * alpha;
    }

    public float getYawOffset() {
        return currentYaw;
    }

    public float getPitchOffset() {
        return currentPitch;
    }

    public float getCameraZoom() {
        return currentZoom;
    }

    public boolean isFreeLookActive() {
        return isActive;
    }

    @Generated
    public ModeSetting getMode() {
        return this.mode;
    }

    @Generated
    public ModeSetting.Value getModeToggle() {
        return this.modeToggle;
    }

    @Generated
    public ModeSetting.Value getModeHold() {
        return this.modeHold;
    }

    @Generated
    public BindSetting getKeyBind() {
        return this.keyBind;
    }

    @Generated
    public SliderSetting getZoomLevel() {
        return this.zoomLevel;
    }

    @Generated
    public SliderSetting getSmoothness() {
        return this.smoothness;
    }

    @Generated
    public EventListener<ClientPlayerTickEvent> getOnTick() {
        return this.onTick;
    }

    @Generated
    public EventListener<KeyPressEvent> getOnKeyPress() {
        return this.onKeyPress;
    }

    @Generated
    public EventListener<MouseScrollEvent> getOnScroll() {
        return this.onScroll;
    }

    @Generated
    public EventListener<MouseMoveEvent> getOnMouseMove() {
        return this.onMouseMove;
    }
}
