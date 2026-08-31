/*
 * Decompiled with CFR 0.152.
 */
package moscow.definevisuals.ui.menu.dropdown.components.settings.impl;

import moscow.definevisuals.framework.base.CustomComponent;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.msdf.Font;
import moscow.definevisuals.framework.msdf.Fonts;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.framework.objects.MouseButton;
import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.systems.setting.settings.ButtonSetting;
import moscow.definevisuals.ui.menu.dropdown.components.settings.MenuSettingComponent;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.game.cursor.CursorType;
import moscow.definevisuals.utility.game.cursor.CursorUtility;
import moscow.definevisuals.utility.gui.GuiUtility;

public class ButtonSettingComponent
extends MenuSettingComponent<ButtonSetting> {
    public ButtonSettingComponent(ButtonSetting setting, CustomComponent parent) {
        super(setting, parent);
    }

    @Override
    public void onInit() {
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
        this.hoverAnimation.update(this.isHovered(context.getMouseX(), context.getMouseY()));
        if (this.isHovered(context.getMouseX(), context.getMouseY())) {
            CursorUtility.set(CursorType.HAND);
        }
        Font nameFont = Fonts.REGULAR.getFont(8.0f);
        context.drawRoundedRect(this.x + 7.0f, this.y + 4.0f, this.width - 14.0f, this.height - 7.0f, BorderRadius.all(6.0f), Colors.getBackgroundColor().withAlpha(255.0f * (0.3f + 0.2f * this.hoverAnimation.getValue())));
        context.drawCenteredText(nameFont, Localizator.translate(((ButtonSetting)this.setting).getName()), this.x + this.width / 2.0f, this.y + GuiUtility.getMiddleOfBox(nameFont.height(), this.height) - 0.5f, Colors.getTextColor().withAlpha(255.0f * (0.75f + 0.25f * this.hoverAnimation.getValue())));
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
            ((ButtonSetting)this.setting).getAction().run();
        }
        super.onMouseReleased(mouseX, mouseY, button);
    }

    @Override
    public float getHeight() {
        this.height = 24.0f;
        return 24.0f;
    }
}

