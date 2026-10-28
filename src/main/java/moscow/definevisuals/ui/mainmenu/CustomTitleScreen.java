package moscow.definevisuals.ui.mainmenu;

import java.util.ArrayList;
import java.util.List;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.CustomScreen;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.msdf.Font;
import moscow.definevisuals.framework.msdf.Fonts;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.framework.objects.MouseButton;
import moscow.definevisuals.framework.objects.gradient.impl.VerticalGradient;
import moscow.definevisuals.systems.background.BackgroundManager;
import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.systems.modules.modules.other.Sounds;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.game.TextUtility;
import moscow.definevisuals.utility.game.cursor.CursorType;
import moscow.definevisuals.utility.game.cursor.CursorUtility;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import moscow.definevisuals.utility.math.MathUtility;
import moscow.definevisuals.utility.render.DrawUtility;
import moscow.definevisuals.utility.render.RenderUtility;
import moscow.definevisuals.utility.sounds.ClientSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;

public class CustomTitleScreen extends CustomScreen implements IMinecraft {
    private static boolean once;
    private static final List<CustomButton> buttons = new ArrayList<>();
    private static CustomButton prevBackgroundButton;
    private static CustomButton nextBackgroundButton;
    private boolean active;
    private final Animation activeAnimation = new Animation(800L, 0.0f, Easing.FIGMA_EASE_IN_OUT);
    private final Animation blurAnimation = new Animation(600L, 0.0f, Easing.FIGMA_EASE_IN_OUT);

    private float currentParallaxX = 0.0f;
    private float currentParallaxY = 0.0f;

    @Override
    protected void init() {
        String basePath = "image/mainmenu/icons/";
        if (!once) {
            if (DefineVisuals.getInstance().getModuleManager().getModule(Sounds.class).isEnabled()) {
                ClientSounds.WELCOME.play(DefineVisuals.getInstance().getModuleManager().getModule(Sounds.class).getVolume().getCurrentValue());
            }
            buttons.clear();
            buttons.add(new CustomButton(basePath + "single.png", 14.0f, () -> mc.setScreen(new SelectWorldScreen(this))));
            buttons.add(new CustomButton(basePath + "multi.png", 14.0f, () -> mc.setScreen(new MultiplayerScreen(this))));
            buttons.add(new CustomButton(basePath + "settings.png", 14.0f, () -> mc.setScreen(new OptionsScreen(this, CustomTitleScreen.mc.options))));
            buttons.add(new CustomButton(basePath + "quit.png", 14.0f, () -> ((MinecraftClient)mc).stop()));

            prevBackgroundButton = new CustomButton("icons/arrow.png", 12.0f, 180.0f, () -> DefineVisuals.getInstance().getBackgroundManager().previousBackground());
            nextBackgroundButton = new CustomButton("icons/arrow.png", 12.0f, 0.0f, () -> DefineVisuals.getInstance().getBackgroundManager().nextBackground());
            once = true;
        }
        super.init();
    }

    @Override
    public void render(UIContext context) {
        Font timeFont = Fonts.ROUND_BOLD.getFont(65.0f);
        Font dateFont = Fonts.MEDIUM.getFont(16.0f);
        Font unlockFont = Fonts.REGULAR.getFont(10.0f);

        this.activeAnimation.update(this.active);
        this.blurAnimation.update(!this.active);
        float activeVal = this.activeAnimation.getValue();
        float blurVal = this.blurAnimation.getValue();
        float textAlpha = 255.0f * (0.55f + 0.45f * activeVal);
        float timeOffset = MathUtility.interpolate((float)this.height / 2.0f - 20.0f, 75.0f, activeVal);

        // Smooth Parallax
        float targetParallaxX = ((float)context.getMouseX() - (float)this.width / 2.0f) / (float)this.width * 16.0f;
        float targetParallaxY = ((float)context.getMouseY() - (float)this.height / 2.0f) / (float)this.height * 16.0f;
        this.currentParallaxX = MathUtility.interpolate(this.currentParallaxX, targetParallaxX, 0.08f);
        this.currentParallaxY = MathUtility.interpolate(this.currentParallaxY, targetParallaxY, 0.08f);

        BackgroundManager bgManager = DefineVisuals.getInstance().getBackgroundManager();
        bgManager.getTransitionAnim().update(true);
        float transition = bgManager.getTransitionAnim().getValue();

        // Background bounds with parallax bleed
        float padding = 35.0f;
        float bgX = -padding + this.currentParallaxX;
        float bgY = -padding + this.currentParallaxY;
        float bgWidth = (float)this.width + padding * 2.0f;
        float bgHeight = (float)this.height + padding * 2.0f;

        // Capture blur buffer BEFORE rendering background so it can be overlaid on top
        DrawUtility.blurProgram.draw();

        // Base gradient
        context.drawRoundedRect(0.0f, 0.0f, (float)this.width, (float)this.height, BorderRadius.ZERO, new VerticalGradient(new ColorRGBA(22.0f, 28.0f, 42.0f), new ColorRGBA(5.0f, 3.0f, 10.0f)));

        // Scale zoom effect when unlocking
        RenderUtility.scale(context.getMatrices(), (float)this.width / 2.0f, (float)this.height / 2.0f, 1.04f - 0.04f * activeVal);

        // Smooth crossfade rendering
        if (transition < 1.0f) {
            context.drawTexture(DefineVisuals.id(bgManager.getPreviousBackground()), bgX, bgY, bgWidth, bgHeight, ColorRGBA.WHITE.withAlpha(255.0f * (1.0f - transition)));
        }
        context.drawTexture(DefineVisuals.id(bgManager.getCurrentBackground()), bgX, bgY, bgWidth, bgHeight, ColorRGBA.WHITE.withAlpha(255.0f * transition));

        RenderUtility.end(context.getMatrices());

        // Blur overlay for the clock-only stage (blurVal=1 when !active, fades out as menu opens)
        // Re-draw blur from the buffer we captured above
        if (blurVal > 0.01f) {
            float blurStrength = 40.0f + 20.0f * blurVal;
            context.drawBlurredRect(0.0f, 0.0f, (float)this.width, (float)this.height, blurStrength, 0.0f, BorderRadius.ZERO, ColorRGBA.WHITE.withAlpha(255.0f * blurVal * 0.85f));
            // Dark scrim to deepen the frosted-glass effect - disabled
            // context.drawRoundedRect(0.0f, 0.0f, (float)this.width, (float)this.height, BorderRadius.ZERO,
            //     new VerticalGradient(new ColorRGBA(0.0f, 0.0f, 0.0f, 60.0f * blurVal), new ColorRGBA(0.0f, 0.0f, 0.0f, 140.0f * blurVal)));
        }

        // Subtle dark vignette overlay for enhanced legibility
        context.drawRoundedRect(0.0f, 0.0f, (float)this.width, (float)this.height, BorderRadius.ZERO, new VerticalGradient(new ColorRGBA(0.0f, 0.0f, 0.0f, 30.0f), new ColorRGBA(0.0f, 0.0f, 0.0f, 120.0f)));

        // Date and Time
        context.drawCenteredText(dateFont, TextUtility.getFormattedDate(), (float)this.width / 2.0f, timeOffset - 24.0f, ColorRGBA.WHITE.withAlpha(textAlpha * 0.9f));
        context.drawCenteredText(timeFont, TextUtility.getCurrentTime(), (float)this.width / 2.0f, timeOffset, ColorRGBA.WHITE.withAlpha(textAlpha));

        // Unlock swipe / prompt indicator
        context.drawRoundedRect((float)this.width / 2.0f - 36.0f, (float)(this.height - 7) - 3.0f * activeVal, 72.0f, 3.0f, BorderRadius.all(1.5f), ColorRGBA.WHITE.withAlpha(220.0f * activeVal));
        context.drawCenteredText(unlockFont, Localizator.translate("mainmenu.next"), (float)this.width / 2.0f, (float)(this.height - 18) + 3.0f * activeVal, ColorRGBA.WHITE.withAlpha(160.0f * (1.0f - activeVal)));

        // Update blur buffer again for glass elements below (buttons etc.)
        DrawUtility.blurProgram.draw();

        // Main Navigation Buttons
        float buttonSpacing = 8.0f;
        float buttonWidth = 34.0f;
        float totalButtonsWidth = buttons.size() * buttonWidth + (buttons.size() - 1) * buttonSpacing;
        float startButtonsX = (float)this.width / 2.0f - totalButtonsWidth / 2.0f;
        float buttonsY = (this.height > 500 ? (float)this.height / 2.0f : (float)this.height / 1.25f) + 15.0f;

        float offset = 0.0f;
        for (int i = 0; i < buttons.size(); i++) {
            CustomButton button = buttons.get(i);
            button.getActiveAnim().update((float)(buttons.size() - i) > (1.0f - activeVal) * (float)buttons.size() + 0.3f);
            button.set(startButtonsX + offset, buttonsY - 10.0f * button.getActiveAnim().getValue(), buttonWidth, buttonWidth);
            offset += buttonWidth + buttonSpacing;
            button.draw(context);
        }

        // Side Navigation Arrow Buttons
        if (prevBackgroundButton != null && nextBackgroundButton != null) {
            prevBackgroundButton.getActiveAnim().update(this.active);
            nextBackgroundButton.getActiveAnim().update(this.active);

            float arrowY = (float)this.height / 2.0f - 17.0f;
            prevBackgroundButton.set(24.0f, arrowY, 34.0f, 34.0f);
            prevBackgroundButton.draw(context);

            nextBackgroundButton.set(this.width - 58.0f, arrowY, 34.0f, 34.0f);
            nextBackgroundButton.draw(context);
        }

        // Frosted Page Indicator Dots
        if (this.active) {
            this.renderPageIndicators(context);
        }

        // Dynamic Island
        if (this.shouldShowIsland()) {
            DefineVisuals.getInstance().getHud().getIsland().render(context);
        }
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        if (this.shouldShowIsland() && DefineVisuals.getInstance().getHud().getIsland().handleClick((float)mouseX, (float)mouseY, button.getButtonIndex())) {
            return;
        }

        if (this.active) {
            if (this.handlePageIndicatorClick(mouseX, mouseY)) {
                return;
            }

            if (prevBackgroundButton != null && prevBackgroundButton.hovered(mouseX, mouseY)) {
                prevBackgroundButton.click(mouseX, mouseY, button.getButtonIndex());
                return;
            }

            if (nextBackgroundButton != null && nextBackgroundButton.hovered(mouseX, mouseY)) {
                nextBackgroundButton.click(mouseX, mouseY, button.getButtonIndex());
                return;
            }

            for (CustomButton customButton : buttons) {
                if (customButton.hovered(mouseX, mouseY) && customButton.getActiveAnim().getValue() > 0.5f) {
                    customButton.click(mouseX, mouseY, button.getButtonIndex());
                    return;
                }
            }
        }

        this.active = !this.active;
        super.onMouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Esc: if active, collapse back to idle clock view
        if (keyCode == 256 && this.active) {
            this.active = false;
            return true;
        }

        // Left / Right Arrow or A / D keys: switch background
        if (keyCode == 263 || keyCode == 65) { // Left arrow or 'A'
            DefineVisuals.getInstance().getBackgroundManager().previousBackground();
            return true;
        }
        if (keyCode == 262 || keyCode == 68) { // Right arrow or 'D'
            DefineVisuals.getInstance().getBackgroundManager().nextBackground();
            return true;
        }

        if (keyCode == 69) { // 'E' -> Switch Theme
            DefineVisuals.getInstance().getThemeManager().switchTheme();
        }
        if (Screen.hasControlDown() && keyCode == 82) { // Ctrl+R -> Multiplayer
            MinecraftClient.getInstance().setScreen(new MultiplayerScreen(this));
        }
        if (Screen.hasControlDown() && keyCode == 84) { // Ctrl+T -> Singleplayer
            MinecraftClient.getInstance().setScreen(new SelectWorldScreen(this));
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean shouldShowIsland() {
        return DefineVisuals.getInstance().getMusicTracker().haveActiveSession();
    }

    private void renderPageIndicators(UIContext context) {
        BackgroundManager bgManager = DefineVisuals.getInstance().getBackgroundManager();
        int currentIndex = bgManager.getCurrentIndex();
        int totalBackgrounds = bgManager.getBackgrounds().size();

        float dotSize = 5.0f;
        float dotSpacing = 14.0f;
        float totalWidth = (totalBackgrounds - 1) * dotSpacing;
        float startX = (float)this.width / 2.0f - totalWidth / 2.0f;
        float y = this.height - 30.0f;

        // Frosted capsule bar background
        float barPaddingX = 14.0f;
        float barHeight = 16.0f;
        context.drawRoundedRect(
                startX - barPaddingX,
                y - (barHeight - dotSize) / 2.0f,
                totalWidth + barPaddingX * 2.0f,
                barHeight,
                BorderRadius.all(barHeight / 2.0f),
                new ColorRGBA(25.0f, 25.0f, 30.0f, 160.0f * this.activeAnimation.getValue())
        );

        for (int i = 0; i < totalBackgrounds; i++) {
            float x = startX + i * dotSpacing;
            boolean isSelected = (i == currentIndex);
            boolean isHovered = Math.abs(context.getMouseX() - x) < 8.0 && Math.abs(context.getMouseY() - y) < 10.0;

            if (isHovered) {
                CursorUtility.set(CursorType.HAND);
            }

            if (isSelected) {
                float capsuleWidth = dotSize + 10.0f;
                context.drawRoundedRect(
                        x - (capsuleWidth - dotSize) / 2.0f,
                        y - 1.0f,
                        capsuleWidth,
                        dotSize + 2.0f,
                        BorderRadius.all((dotSize + 2.0f) / 2.0f),
                        new ColorRGBA(120.0f, 180.0f, 255.0f, 240.0f * this.activeAnimation.getValue())
                );
            } else {
                float dotAlpha = isHovered ? 240.0f : 130.0f;
                context.drawRoundedRect(
                        x,
                        y,
                        dotSize,
                        dotSize,
                        BorderRadius.all(dotSize / 2.0f),
                        new ColorRGBA(255.0f, 255.0f, 255.0f, dotAlpha * this.activeAnimation.getValue())
                );
            }
        }
    }

    private boolean handlePageIndicatorClick(double mouseX, double mouseY) {
        BackgroundManager bgManager = DefineVisuals.getInstance().getBackgroundManager();
        int totalBackgrounds = bgManager.getBackgrounds().size();

        float dotSpacing = 14.0f;
        float totalWidth = (totalBackgrounds - 1) * dotSpacing;
        float startX = (float)this.width / 2.0f - totalWidth / 2.0f;
        float y = this.height - 30.0f;

        for (int i = 0; i < totalBackgrounds; i++) {
            float x = startX + i * dotSpacing;
            if (Math.abs(mouseX - x) < 9.0 && Math.abs(mouseY - y) < 12.0) {
                bgManager.setBackground(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}

