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
import moscow.definevisuals.systems.modules.modules.visuals.Interface;
import moscow.definevisuals.systems.setting.settings.SliderSetting;
import moscow.definevisuals.ui.menu.dropdown.components.settings.MenuSettingComponent;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.game.TextUtility;
import moscow.definevisuals.utility.game.cursor.CursorType;
import moscow.definevisuals.utility.game.cursor.CursorUtility;
import moscow.definevisuals.utility.gui.GuiUtility;
import moscow.definevisuals.utility.render.DrawUtility;
import moscow.definevisuals.utility.time.Timer;

public class SliderSettingComponent
extends MenuSettingComponent<SliderSetting> {
    private final Animation animation = new Animation(500L, Easing.BAKEK_PAGES);
    private final Animation moving = new Animation(500L, Easing.FIGMA_EASE_IN_OUT);
    private final Timer timer = new Timer();
    private boolean drag;
    private static SliderSettingComponent current;

    public SliderSettingComponent(SliderSetting setting, CustomComponent parent) {
        super(setting, parent);
    }

    @Override
    protected void renderComponent(UIContext context) {
        float x = this.x + 9.0f;
        float y = this.y + 2.0f;
        float width = this.width - 18.0f;
        Font nameFont = Fonts.REGULAR.getFont(8.0f);
        float leftPadding = 10.0f;
        float nameHeight = Fonts.REGULAR.getFont(7.0f).height();
        float headerHeight = 19.0f;
        this.animation.update(((SliderSetting)this.setting).getCurrentValue());
        this.hoverAnimation.update(this.isHovered(context.getMouseX(), context.getMouseY()));
        context.drawRoundedRect(x, y + this.height - 12.0f, width, 2.0f, BorderRadius.all(0.25f), Colors.getAdditionalColor().withAlpha((255.0f - 100.0f * Interface.glass()) * 0.7f));
        context.drawRoundedRect(x, y + this.height - 12.0f, width * GuiUtility.getPercent(this.animation.getValue(), ((SliderSetting)this.setting).getMin(), ((SliderSetting)this.setting).getMax()), 2.0f, BorderRadius.all(0.25f), Colors.ACCENT);
        if (this.timer.finished(1000L)) {
            DrawUtility.updateBuffer();
            this.timer.reset();
        }
        if (Interface.showGlass()) {
            context.drawShadow(x + width * GuiUtility.getPercent(this.animation.getValue(), ((SliderSetting)this.setting).getMin(), ((SliderSetting)this.setting).getMax()) - 4.5f - 3.0f * this.moving.getValue(), y + this.height - 11.0f - 3.0f - 2.0f * this.moving.getValue(), 9.0f + 6.0f * this.moving.getValue(), 6.0f + 4.0f * this.moving.getValue(), 10.0f, BorderRadius.all(3.0f + this.moving.getValue() * 2.0f), ColorRGBA.BLACK.withAlpha(255.0f * (0.25f + 0.2f * this.moving.getValue()) * Interface.glass()));
            context.drawSquircle(x + width * GuiUtility.getPercent(this.animation.getValue(), ((SliderSetting)this.setting).getMin(), ((SliderSetting)this.setting).getMax()) - 4.5f - 3.0f * this.moving.getValue(), y + this.height - 11.0f - 3.0f - 2.0f * this.moving.getValue(), 9.0f + 6.0f * this.moving.getValue(), 6.0f + 4.0f * this.moving.getValue(), 7.0f, BorderRadius.all(3.0f + this.moving.getValue()), ColorRGBA.WHITE.withAlpha(255.0f * (1.0f - this.moving.getValue()) * Interface.glass()));
            context.drawLiquidGlass(x + width * GuiUtility.getPercent(this.animation.getValue(), ((SliderSetting)this.setting).getMin(), ((SliderSetting)this.setting).getMax()) - 4.5f - 3.0f * this.moving.getValue(), y + this.height - 11.0f - 3.0f - 2.0f * this.moving.getValue(), 9.0f + 6.0f * this.moving.getValue(), 6.0f + 4.0f * this.moving.getValue(), 7.0f, BorderRadius.all(3.0f + this.moving.getValue()), ColorRGBA.WHITE.withAlpha(255.0f * this.moving.getValue() * Interface.glass()), true);
        }
        if (Interface.showMinimalizm()) {
            context.drawShadow(x + width * GuiUtility.getPercent(this.animation.getValue(), ((SliderSetting)this.setting).getMin(), ((SliderSetting)this.setting).getMax()) - 3.0f, y + this.height - 14.0f + this.moving.getValue(), 6.0f, 6.0f - this.moving.getValue() * 2.0f, 10.0f, BorderRadius.all(3.0f - this.moving.getValue() * 2.0f), ColorRGBA.BLACK.withAlpha(63.75f * Interface.minimalizm()));
            context.drawRoundedRect(x + width * GuiUtility.getPercent(this.animation.getValue(), ((SliderSetting)this.setting).getMin(), ((SliderSetting)this.setting).getMax()) - 3.0f, y + this.height - 14.0f + this.moving.getValue(), 6.0f, 6.0f - this.moving.getValue() * 2.0f, BorderRadius.all(3.0f - this.moving.getValue() * 2.0f), ColorRGBA.WHITE.withAlpha(255.0f * Interface.minimalizm()));
        }
        String value = TextUtility.formatNumberClean(this.animation.getValue()) + ((SliderSetting)this.setting).getSuffix();
        context.drawFadeoutText(nameFont, Localizator.translate(((SliderSetting)this.setting).getName()), this.x + leftPadding, y + 11.0f - nameFont.height(), Colors.getTextColor().withAlpha(255.0f * (0.75f + 0.25f * this.hoverAnimation.getValue())), 0.8f, 1.0f, this.getParent().getWidth() - leftPadding - Fonts.REGULAR.getFont(7.0f).width(value) - 10.0f);
        context.drawRightText(Fonts.REGULAR.getFont(7.0f), value, x + width, y + 11.0f - nameHeight, Colors.getTextColor().withAlpha(255.0f * (0.75f + 0.25f * this.hoverAnimation.getValue())));
        if (this.isHovered(context.getMouseX(), context.getMouseY())) {
            CursorUtility.set(CursorType.HAND);
        }
        this.moving.setDuration(200L);
        this.moving.update(this.drag ? 1.0f : 0.0f);
        if (this.drag) {
            float xValue = GuiUtility.getSliderValue(((SliderSetting)this.setting).getMin(), ((SliderSetting)this.setting).getMax(), x, width, context.getMouseX());
            ((SliderSetting)this.setting).setCurrentValue(xValue);
            CursorUtility.set(CursorType.ARROW_HORIZONTAL);
            current = this;
        }
    }

    @Override
    public void drawSplit(UIContext context) {
        float separatorHeight = 0.5f;
        context.drawRect(this.x, this.y + this.height, this.width, separatorHeight, Colors.getTextColor().withAlpha(5.1f));
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        if (this.isHovered(mouseX, mouseY)) {
            this.drag = true;
        }
        super.onMouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, MouseButton button) {
        this.drag = false;
        super.onMouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if ((keyCode == 262 || keyCode == 263) && current == this) {
            ((SliderSetting)current.getSetting()).setCurrentValue(((SliderSetting)current.getSetting()).getCurrentValue() + ((SliderSetting)current.getSetting()).getStep() * 0.7f * (float)(keyCode == 262 ? 1 : -1));
        }
    }

    @Override
    public float getHeight() {
        this.height = 29.0f;
        return 29.0f;
    }
}

