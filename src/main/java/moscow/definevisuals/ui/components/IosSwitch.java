package moscow.definevisuals.ui.components;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;

public class IosSwitch {
    private final Animation animation;

    public IosSwitch() {
        this(300L);
    }

    public IosSwitch(long duration) {
        this.animation = new Animation(duration, 0.0f, Easing.BAKEK);
    }

    public void update(boolean state) {
        this.animation.update(state ? 1.0f : 0.0f);
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public float getValue() {
        return this.animation.getValue();
    }

    public static void render(UIContext context, float x, float y, float progress, float alpha) {
        render(context, x, y, progress, alpha, Colors.getAccent());
    }

    public static void render(UIContext context, float x, float y, float progress, float alpha, ColorRGBA activeColor) {
        if (alpha <= 0.001f) return;
        progress = Math.max(0.0f, Math.min(1.0f, progress));

        float checkWidth = 13.0f;
        float checkHeight = 8.0f;

        ColorRGBA offColor = DefineVisuals.getInstance().getThemeManager().getCurrentTheme().getAdditionalColor();
        ColorRGBA trackColor = offColor.mix(activeColor, progress).mulAlpha(alpha);

        context.drawRoundedRect(x, y, checkWidth, checkHeight, BorderRadius.all(3.0f), trackColor);
        context.drawRoundedRect(x + 1.0f + 5.0f * progress, y + 1.0f, 6.0f, 6.0f, BorderRadius.all(4.0f), Colors.WHITE.mulAlpha(alpha));
    }
}
