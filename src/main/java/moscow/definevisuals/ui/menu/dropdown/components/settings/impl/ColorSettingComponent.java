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
import moscow.definevisuals.systems.setting.settings.ColorSetting;
import moscow.definevisuals.ui.components.ColorPicker;
import moscow.definevisuals.ui.menu.MenuScreen;
import moscow.definevisuals.ui.menu.dropdown.DropDownScreen;
import moscow.definevisuals.ui.menu.dropdown.components.settings.MenuSettingComponent;
import moscow.definevisuals.ui.menu.modern.ModernScreen;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.game.cursor.CursorType;
import moscow.definevisuals.utility.game.cursor.CursorUtility;
import moscow.definevisuals.utility.gui.GuiUtility;
import moscow.definevisuals.utility.sounds.UiSound;

public class ColorSettingComponent
extends MenuSettingComponent<ColorSetting> {
    private ColorPicker picker;

    public ColorSettingComponent(ColorSetting setting, CustomComponent parent) {
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
        float checkWidth = 13.0f;
        Font nameFont = Fonts.REGULAR.getFont(8.0f);
        float leftPadding = 10.0f;
        float headerHeight = 19.0f;
        context.drawFadeoutText(nameFont, Localizator.translate(((ColorSetting)this.setting).getName()), this.x + leftPadding, this.y + GuiUtility.getMiddleOfBox(nameFont.height(), headerHeight) - 0.5f, Colors.getTextColor().withAlpha(255.0f * (0.75f + 0.25f * this.hoverAnimation.getValue())), 0.7f, 0.99f, this.width - checkWidth - 20.0f);
        context.drawRoundedRect(this.x + this.width - leftPadding - 8.0f, this.y + 5.0f, 8.0f, 8.0f, BorderRadius.all(4.5f), Colors.getOutlineColor());
        context.drawRoundedRect(this.x + this.width - leftPadding - 7.0f, this.y + 6.0f, 6.0f, 6.0f, BorderRadius.all(4.5f), ((ColorSetting)this.setting).getColor());
        if (this.picker != null) {
            ((ColorSetting)this.setting).color(this.picker.built());
        }
    }

    @Override
    public void drawSplit(UIContext context) {
        float separatorHeight = 0.5f;
        context.drawRect(this.x, this.y + this.height, this.width, separatorHeight, Colors.getTextColor().withAlpha(5.1f));
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        if (this.isHovered(mouseX, mouseY) && button == MouseButton.LEFT) {
            UiSound.WINDOW_OPEN.play();
            MenuScreen menuScreen = DefineVisuals.getInstance().getMenuScreen();
            if (menuScreen instanceof DropDownScreen) {
                DropDownScreen dropDownScreen = (DropDownScreen)menuScreen;
                this.picker = new ColorPicker((float)mouseX, (float)mouseY, 6.0f, ((ColorSetting)this.setting).isAlpha(), ((ColorSetting)this.setting).getColor(), Localizator.translate(((ColorSetting)this.setting).getName()));
                dropDownScreen.getColorPickers().add(this.picker);
            } else {
                menuScreen = DefineVisuals.getInstance().getMenuScreen();
                if (menuScreen instanceof ModernScreen) {
                    ModernScreen modernScreen = (ModernScreen)menuScreen;
                    this.picker = new ColorPicker((float)mouseX, (float)mouseY, 6.0f, ((ColorSetting)this.setting).isAlpha(), ((ColorSetting)this.setting).getColor(), Localizator.translate(((ColorSetting)this.setting).getName()));
                    modernScreen.getColorPickers().add(this.picker);
                }
            }
        }
        super.onMouseClicked(mouseX, mouseY, button);
    }

    @Override
    public float getHeight() {
        this.height = 18.0f;
        return 18.0f;
    }
}
