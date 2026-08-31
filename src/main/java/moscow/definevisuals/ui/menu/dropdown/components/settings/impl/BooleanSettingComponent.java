/*
 * Decompiled with CFR 0.152.
 */
package moscow.definevisuals.ui.menu.dropdown.components.settings.impl;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.CustomComponent;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.msdf.Font;
import moscow.definevisuals.framework.msdf.Fonts;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.framework.objects.MouseButton;
import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.systems.modules.modules.visuals.Interface;
import moscow.definevisuals.systems.setting.settings.BooleanSetting;
import moscow.definevisuals.ui.menu.dropdown.components.settings.MenuSettingComponent;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.animation.types.ColorAnimation;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.game.cursor.CursorType;
import moscow.definevisuals.utility.game.cursor.CursorUtility;
import moscow.definevisuals.utility.gui.GuiUtility;

public class BooleanSettingComponent
extends MenuSettingComponent<BooleanSetting> {
    private Animation circleOpacityAnimation;
    private Animation enableAnimation;
    private ColorAnimation backgroundColorAnimation;

    public BooleanSettingComponent(BooleanSetting setting, CustomComponent parent) {
        super(setting, parent);
    }

    @Override
    public void onInit() {
        this.circleOpacityAnimation = new Animation(300L, 0.0f, Easing.FIGMA_EASE_IN_OUT);
        this.enableAnimation = new Animation(300L, Easing.BAKEK);
        this.backgroundColorAnimation = new ColorAnimation(300L, new ColorRGBA(24.0f, 24.0f, 27.0f), Easing.FIGMA_EASE_IN_OUT);
        this.width = 13.0f;
        this.height = 8.0f;
        super.onInit();
    }

    @Override
    public void update(UIContext context) {
        super.update(context);
    }

    @Override
    protected void renderComponent(UIContext context) {
        this.circleOpacityAnimation.update(((BooleanSetting)this.setting).isEnabled() ? 1.0f : 0.75f);
        this.enableAnimation.update(((BooleanSetting)this.setting).isEnabled() ? 1.0f : 0.0f);
        this.backgroundColorAnimation.update(((BooleanSetting)this.setting).isEnabled() ? new ColorRGBA(151.0f, 71.0f, 255.0f) : DefineVisuals.getInstance().getThemeManager().getCurrentTheme().getAdditionalColor());
        this.hoverAnimation.update(this.isHovered(context.getMouseX(), context.getMouseY()));
        if (this.isHovered(context.getMouseX(), context.getMouseY())) {
            CursorUtility.set(CursorType.HAND);
        }
        float checkWidth = 13.0f;
        float checkHeight = 8.0f;
        Font nameFont = Fonts.REGULAR.getFont(8.0f);
        float leftPadding = 10.0f;
        float nameHeight = nameFont.height();
        float headerHeight = 19.0f;
        context.drawFadeoutText(nameFont, Localizator.translate(((BooleanSetting)this.setting).getName()), this.x + leftPadding, this.y + GuiUtility.getMiddleOfBox(nameFont.height(), headerHeight) - 0.5f, Colors.getTextColor().withAlpha(255.0f * (0.75f + 0.25f * this.enableAnimation.getValue() + 0.25f * this.hoverAnimation.getValue())), 0.7f, 0.99f, this.width - checkWidth - 20.0f);
        context.drawRoundedRect(this.x + this.width - checkWidth - 9.0f, this.y + 5.0f, checkWidth, checkHeight, BorderRadius.all(3.0f), this.backgroundColorAnimation.getColor().withAlpha(!((BooleanSetting)this.setting).isEnabled() ? 255.0f - 100.0f * Interface.glass() : 255.0f));
        context.drawRoundedRect(this.x + this.width - checkWidth - 8.0f + 5.0f * this.enableAnimation.getValue(), this.y + 6.0f, 6.0f, 6.0f, BorderRadius.all(4.0f), new ColorRGBA(255.0f, 255.0f, 255.0f).withAlpha(this.circleOpacityAnimation.getValue() * 255.0f));
    }

    @Override
    public void drawRegular8(UIContext context) {
    }

    @Override
    public void drawSplit(UIContext context) {
        float separatorHeight = 0.5f;
        context.drawRect(this.x, this.y + this.height, this.width, separatorHeight, Colors.getTextColor().withAlpha(5.1f));
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        if (this.isHovered(mouseX, mouseY) && button == MouseButton.LEFT) {
            ((BooleanSetting)this.setting).toggle();
        }
        super.onMouseReleased(mouseX, mouseY, button);
    }

    @Override
    public float getHeight() {
        this.height = 18.0f;
        return 18.0f;
    }
}

