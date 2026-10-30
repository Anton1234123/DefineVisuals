/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  lombok.Generated
 */
package moscow.definevisuals.ui.menu.modern.components;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.CustomComponent;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.msdf.Font;
import moscow.definevisuals.framework.msdf.Fonts;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.framework.objects.MouseButton;
import moscow.definevisuals.framework.style.InterfaceStyleRenderer;
import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.systems.modules.Module;
import moscow.definevisuals.ui.components.IosSwitch;
import moscow.definevisuals.ui.menu.MenuScreen;
import moscow.definevisuals.ui.menu.dropdown.DropDownScreen;
import moscow.definevisuals.ui.menu.modern.ModernCategory;
import moscow.definevisuals.ui.menu.modern.ModernScreen;
import moscow.definevisuals.ui.menu.modern.components.ModernSettings;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.animation.types.ColorAnimation;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.game.TextUtility;
import moscow.definevisuals.utility.interfaces.IScaledResolution;
import moscow.definevisuals.utility.render.obj.Rect;
import moscow.definevisuals.utility.sounds.UiSound;

public class ModernModule
extends CustomComponent {
    private final Animation visible = new Animation(300L, Easing.FIGMA_EASE_IN_OUT);
    private final Animation offset = new Animation(300L, Easing.FIGMA_EASE_IN_OUT);
    private final Animation hoverAnimation = new Animation(300L, Easing.FIGMA_EASE_IN_OUT);
    private final Animation enableAnimation = new Animation(300L, 0.0f, Easing.FIGMA_EASE_IN_OUT);
    private final Module module;
    private final ModernCategory category;
    private boolean bindingMode;
    private final Animation shakeAnimation = new Animation(100L, Easing.FIGMA_EASE_IN_OUT);
    private final Animation blockingAnimation = new Animation(500L, Easing.FIGMA_EASE_IN_OUT);
    private final ColorAnimation blockingColorAnimation = new ColorAnimation(500L, ColorRGBA.WHITE, Easing.FIGMA_EASE_IN_OUT);
    private boolean blocking;
    private boolean shakeValue;
    private float cardHeight = 28.0f;

    public float getCardHeight(float cardWidth) {
        Font descFont = Fonts.REGULAR.getFont(5.5f);
        String desc = this.module.getDescription();
        if (desc == null || desc.isEmpty()) {
            this.cardHeight = 28.0f;
            return this.cardHeight;
        }
        float maxTextWidth = cardWidth - 14.0f;
        List<String> lines = this.wrapText(desc, descFont, maxTextWidth);
        float lineHeight = descFont.height() + 1.5f;
        float nameHeight = 14.0f;
        float descHeight = lines.size() * lineHeight;
        this.cardHeight = nameHeight + descHeight + 8.0f;
        if (this.cardHeight < 28.0f) this.cardHeight = 28.0f;
        return this.cardHeight;
    }

    private List<String> wrapText(String text, Font font, float maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) return lines;
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            String test = currentLine.isEmpty() ? word : currentLine + " " + word;
            if (font.width(test) <= maxWidth) {
                currentLine = new StringBuilder(test);
            } else {
                if (!currentLine.isEmpty()) {
                    lines.add(currentLine.toString());
                }
                currentLine = new StringBuilder(word);
            }
        }
        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }
        return lines;
    }

    @Override
    protected void renderComponent(UIContext context) {
        this.enableAnimation.setEasing(Easing.QUARTIC_OUT);
        this.enableAnimation.update(this.module.isEnabled());
        this.hoverAnimation.update(this.isHovered(context.getMouseX(), context.getMouseY()));
        this.blockingAnimation.update(this.blocking);
        this.blockingColorAnimation.update(this.blocking ? new ColorRGBA(255.0f, 150.0f, 150.0f) : DefineVisuals.getInstance().getThemeManager().getCurrentTheme().getTextColor());
        this.shakeAnimation.update(this.blocking ? (this.shakeValue ? 1.0f : -1.0f) : 0.0f);
        if (this.blockingAnimation.getValue() == 1.0f) {
            this.blocking = false;
        }
        if (this.shakeAnimation.getValue() == 1.0f) {
            this.shakeValue = false;
        }
        if (this.shakeAnimation.getValue() == -1.0f) {
            this.shakeValue = true;
        }
        BorderRadius cardRadius = BorderRadius.all(4.0f);
        InterfaceStyleRenderer.drawCard(
            context,
            this.x,
            this.y,
            this.width,
            this.height,
            3.0f,
            cardRadius,
            this.visible.getValue(),
            this.enableAnimation.getValue(),
            this.hoverAnimation.getValue()
        );

        // iOS switch from dropdown menu on top right
        float switchX = this.x + this.width - 13.0f - 8.0f;
        float switchY = this.y + 6.5f;
        IosSwitch.render(context, switchX, switchY, this.enableAnimation.getValue(), this.visible.getValue());
    }

    public void renderRounds(UIContext context) {
        // toggle not shown in new grid layout
    }

    public void renderInto(UIContext context) {
        // toggle not shown in new grid layout
    }

    public void renderMedium(UIContext context) {
        int key = this.module.getKey();
        Object bindingText = key == -1 ? Localizator.translate("menu.binding") : Localizator.translate("key") + ": " + TextUtility.getKeyName(key);
        context.drawFadeoutText(Fonts.MEDIUM.getFont(7.0f), (String)(this.bindingMode ? bindingText : this.module.getName()), this.x + 7.0f + this.shakeAnimation.getValue(), this.y + 7.0f, this.blockingColorAnimation.getColor().mulAlpha(RenderSystem.getShaderColor()[3] * 0.75f + 0.25f * this.enableAnimation.getValue() + 0.25f * this.hoverAnimation.getValue()).mulAlpha(this.visible.getValue()), 0.9f, 1.0f, this.width - 26.0f);
    }

    public void renderRegular(UIContext context) {
        Font descFont = Fonts.REGULAR.getFont(5.5f);
        String desc = this.module.getDescription();
        if (desc == null || desc.isEmpty()) return;
        float maxTextWidth = this.width - 14.0f;
        List<String> lines = this.wrapText(desc, descFont, maxTextWidth);
        float lineHeight = descFont.height() + 1.5f;
        float startY = this.y + 16.0f;
        ColorRGBA descColor = Colors.getTextColor().mulAlpha(0.5f * this.visible.getValue());
        for (int i = 0; i < lines.size(); i++) {
            context.drawText(descFont, lines.get(i), this.x + 7.0f, startY + i * lineHeight, descColor);
        }
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        if (this.bindingMode && button != MouseButton.LEFT && button != MouseButton.RIGHT) {
            this.module.setKey(button.getButtonIndex());
            this.bindingMode = false;
            UiSound.BIND_SET.play();
            return;
        }
        switch (button) {
            case LEFT: {
                this.module.toggle();
                break;
            }
            case MIDDLE: {
                for (ModernModule comp : this.category.getModules()) {
                    comp.setBindingMode(false);
                }
                this.bindingMode = true;
                UiSound.BIND.play();
                break;
            }
            case RIGHT: {
                this.open();
            }
        }
    }

    @Override
    public void onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.bindingMode) {
            if (keyCode == 256 || keyCode == 261) {
                this.module.setKey(-1);
            } else {
                this.module.setKey(keyCode);
            }
            this.bindingMode = false;
            UiSound.BIND_SET.play();
            MenuScreen menuScreen = DefineVisuals.getInstance().getMenuScreen();
            if (menuScreen instanceof DropDownScreen dropDownScreen) {
                dropDownScreen.getSearchField().setFocused(false);
            } else if (menuScreen instanceof ModernScreen modernScreen) {
                modernScreen.getSearchField().setFocused(false);
            }
        }
        super.onKeyPressed(keyCode, scanCode, modifiers);
    }

    public void open() {
        if (this.module.getSettings().isEmpty()) {
            if (!this.blocking) {
                UiSound.ERROR.play();
            }
            this.blocking = true;
            this.shakeValue = true;
        } else {
            MenuScreen menuScreen = DefineVisuals.getInstance().getMenuScreen();
            if (!(menuScreen instanceof ModernScreen modernScreen)) {
                return;
            }
            Rect win = modernScreen.getMenuWindow();
            List<ModernSettings> windows = modernScreen.getWindows();
            float x = win.getX() + win.getWidth() + 10.0f;
            float y = win.getY();
            float width = 152.0f;
            if (!windows.isEmpty()) {
                float h = windows.getLast().getY() + windows.getLast().getHeight();
                if (h < win.getY() + win.getHeight()) {
                    y = h + 10.0f;
                    x = windows.getLast().getX();
                } else {
                    x = windows.getLast().getX() + windows.getLast().getWidth() + 10.0f;
                }
            }
            for (ModernSettings window : windows) {
                if (window.getModule() != this) continue;
                return;
            }
            if (!windows.isEmpty() && x + width > IScaledResolution.sr.getScaledWidth() || windows.size() > 4) {
                x = windows.getFirst().getX();
                y = windows.getFirst().getY();
                windows.getFirst().setShowing(false);
                ModernSettings newWindow = new ModernSettings(this, x, y, width);
                windows.addFirst(newWindow);
                float offset = y + newWindow.getHeight() + 10.0f;
                for (ModernSettings window : windows) {
                    if (window.getX() != x || window.getModule() == this) continue;
                    window.setY(offset);
                    offset += window.getHeight() + 10.0f;
                }
            } else {
                windows.add(new ModernSettings(this, x, y, width));
            }
            UiSound.WINDOW_OPEN.play();
            this.visible.setValue(0.0f);
        }
    }

    public boolean isBinding() {
        return false;
    }

    @Generated
    public ModernModule(Module module, ModernCategory category) {
        this.module = module;
        this.category = category;
    }

    @Generated
    public Animation getVisible() {
        return this.visible;
    }

    @Generated
    public Animation getOffset() {
        return this.offset;
    }

    @Generated
    public Module getModule() {
        return this.module;
    }

    @Generated
    public void setBindingMode(boolean bindingMode) {
        this.bindingMode = bindingMode;
    }
}
