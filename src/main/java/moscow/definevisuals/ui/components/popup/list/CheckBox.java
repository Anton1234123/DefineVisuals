/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 */
package moscow.definevisuals.ui.components.popup.list;

import com.mojang.blaze3d.systems.RenderSystem;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.msdf.Font;
import moscow.definevisuals.framework.msdf.Fonts;
import moscow.definevisuals.framework.objects.MouseButton;
import moscow.definevisuals.ui.components.popup.CheckBoxAction;
import moscow.definevisuals.ui.components.popup.PopupComponent;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.game.cursor.CursorType;
import moscow.definevisuals.utility.game.cursor.CursorUtility;
import moscow.definevisuals.utility.gui.GuiUtility;

public class CheckBox
extends PopupComponent {
    private boolean enabled;
    private final String text;
    private final Animation hoverAnimation = new Animation(300L, Easing.FIGMA_EASE_IN_OUT);
    private final Animation enableAnimation = new Animation(300L, Easing.FIGMA_EASE_IN_OUT);
    private CheckBoxAction action;

    public CheckBox(String text) {
        this.text = text;
    }

    @Override
    protected void renderComponent(UIContext context) {
        Font nameFont = Fonts.REGULAR.getFont(8.0f);
        float nameLeftPadding = 8.0f;
        float nameHeight = nameFont.height();
        this.hoverAnimation.update(this.isHovered(context.getMouseX(), context.getMouseY()));
        this.enableAnimation.update(this.enabled ? 1.0f : 0.0f);
        if (this.isHovered(context.getMouseX(), context.getMouseY())) {
            CursorUtility.set(CursorType.HAND);
        }
        context.drawFadeoutText(nameFont, this.text, this.x + nameLeftPadding, this.y + GuiUtility.getMiddleOfBox(nameHeight, this.height), Colors.getTextColor().withAlpha(RenderSystem.getShaderColor()[3] * 255.0f * (0.75f + 0.25f * this.enableAnimation.getValue() + 0.25f * this.hoverAnimation.getValue())), 0.8f, 1.0f, this.width - 12.0f - 12.0f * this.enableAnimation.getValue());
        float alpha = this.enableAnimation.getValue() * (RenderSystem.getShaderColor()[3] * 255.0f);
        if (this.enableAnimation.getValue() >= 0.0f) {
            context.drawTexture(DefineVisuals.id("icons/check.png"), this.x + this.width - 13.0f - this.enableAnimation.getValue() * 2.0f, this.y + 7.0f, 6.0f, 6.0f, Colors.getTextColor().withAlpha(alpha));
        }
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        if (this.isHovered(mouseX, mouseY) && button == MouseButton.LEFT) {
            boolean bl = this.enabled = !this.enabled;
            if (this.action != null) {
                this.action.handleAction(this.enabled);
            }
        }
        super.onMouseClicked(mouseX, mouseY, button);
    }

    @Override
    public float getHeight() {
        this.height = 19.0f;
        return 19.0f;
    }

    public CheckBox enabled(boolean value) {
        this.enabled = value;
        return this;
    }

    public CheckBox action(CheckBoxAction action) {
        this.action = action;
        return this;
    }
}

