package moscow.definevisuals.ui.mainmenu;

import lombok.Generated;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.game.cursor.CursorType;
import moscow.definevisuals.utility.game.cursor.CursorUtility;
import moscow.definevisuals.utility.render.RenderUtility;
import moscow.definevisuals.utility.render.obj.Rect;

public class CustomButton extends Rect {
    private final String icon;
    private final float iconSize;
    private final Runnable onClick;
    private float rotation = 0.0f;
    private final ColorRGBA backgroundColor = new ColorRGBA(40.0f, 40.0f, 45.0f);
    private final Animation activeAnim = new Animation(400L, 0.0f, Easing.BAKEK);
    private final Animation hoverAnim = new Animation(250L, 0.0f, Easing.FIGMA_EASE_IN_OUT);

    public CustomButton(String icon, float iconSize, Runnable onClick) {
        this(icon, iconSize, 0.0f, onClick);
    }

    public CustomButton(String icon, float iconSize, float rotation, Runnable onClick) {
        this.icon = icon;
        this.iconSize = iconSize;
        this.rotation = rotation;
        this.onClick = onClick;
    }

    public void draw(UIContext context) {
        float alpha = this.activeAnim.getValue();
        if (alpha <= 0.01f) return;

        boolean hovered = this.hovered(context.getMouseX(), context.getMouseY());
        if (hovered && alpha > 0.6f) {
            CursorUtility.set(CursorType.HAND);
        }
        this.hoverAnim.update(hovered && alpha > 0.6f);

        float hover = this.hoverAnim.getValue();
        float radius = Math.min(this.width, this.height) / 2.0f;

        // Glass background with smooth hover brightness
        context.drawRoundedRect(
                this.x, this.y, this.width, this.height,
                BorderRadius.all(radius),
                this.backgroundColor.withAlpha(255.0f * (0.45f * alpha + 0.35f * hover))
        );

        // Icon rendering
        float iconX = this.x + (this.width - this.iconSize) / 2.0f;
        float iconY = this.y + (this.height - this.iconSize) / 2.0f;

        if (this.rotation != 0.0f) {
            RenderUtility.rotate(context.getMatrices(), this.x + this.width / 2.0f, this.y + this.height / 2.0f, this.rotation);
            context.drawTexture(DefineVisuals.id(this.icon), iconX, iconY, this.iconSize, this.iconSize, ColorRGBA.WHITE.withAlpha(255.0f * alpha));
            RenderUtility.end(context.getMatrices());
        } else {
            context.drawTexture(DefineVisuals.id(this.icon), iconX, iconY, this.iconSize, this.iconSize, ColorRGBA.WHITE.withAlpha(255.0f * alpha));
        }
    }

    public void click(double mouseX, double mouseY, int button) {
        if (this.hovered(mouseX, mouseY) && button == 0 && this.activeAnim.getValue() > 0.5f) {
            this.onClick.run();
        }
    }

    @Generated
    public Animation getActiveAnim() {
        return this.activeAnim;
    }

    @Generated
    public Animation getHoverAnim() {
        return this.hoverAnim;
    }
}

