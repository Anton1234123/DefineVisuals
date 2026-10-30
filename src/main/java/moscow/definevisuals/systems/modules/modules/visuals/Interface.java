package moscow.definevisuals.systems.modules.modules.visuals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Generated;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.render.HudRenderEvent;
import moscow.definevisuals.systems.localization.Language;
import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.SettingsContainer;
import moscow.definevisuals.systems.setting.settings.ModeSetting;
import moscow.definevisuals.systems.theme.Theme;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;

@ModuleInfo(name = "Interface", category = ModuleCategory.VISUALS, enabledByDefault = true)
public class Interface extends BaseModule {
    private final ModeSetting mode = new ModeSetting(this, "modules.settings.interface.mode");
    private final ModeSetting.Value liquidGlass = new ModeSetting.Value(this.mode, "modules.settings.interface.liquidGlass");
    private final ModeSetting.Value minimalism = new ModeSetting.Value(this.mode, "modules.settings.interface.minimalism");
    private final ModeSetting.Value modernMinimalism = new ModeSetting.Value(this.mode, "modules.settings.interface.modernMinimalism");
    private final ModeSetting.Value blurGlass = new ModeSetting.Value(this.mode, "modules.settings.interface.glass");
    private final ModeSetting themeMode = new ModeSetting(
        (SettingsContainer) this,
        "modules.settings.interface.theme",
        () -> this.liquidGlass.isSelected() || this.minimalism.isSelected()
    );
    public final ModeSetting.Value dark = new ModeSetting.Value(this.themeMode, "modules.settings.interface.dark");
    public final ModeSetting.Value light = new ModeSetting.Value(this.themeMode, "modules.settings.interface.light");
    private final ModeSetting language = new ModeSetting(this, "modules.settings.interface.language");
    private final Animation liquidGlassAnim = new Animation(500L, 1.0f, Easing.BOTH_CUBIC);
    private final Animation minimalismAnim = new Animation(500L, Easing.BOTH_CUBIC);
    private final Animation modernMinimalismAnim = new Animation(500L, Easing.BOTH_CUBIC);
    private final Animation blurGlassAnim = new Animation(500L, Easing.BOTH_CUBIC);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean languageAutoDetected;
    private int lastLang = 0;

    private final EventListener<HudRenderEvent> onHudRenderEvent = event -> {
        this.updateStyleAnimations();
        int lang = this.language.getValues().indexOf(this.language.getValue());
        if (lang != this.lastLang) {
            Localizator.setLanguage(lang == 0 ? Language.RU_RU : (lang == 1 ? Language.EN_US : (lang == 2 ? Language.UK_UA : Language.PL_PL)));
            this.languageAutoDetected = false;
        }
        this.lastLang = lang;
        DefineVisuals.getInstance().getThemeManager().setCurrentTheme(
            this.minimalism.isSelected() || this.minimalismAnim.getValue() > 0.01f || this.dark.isSelected() ? Theme.DARK : Theme.LIGHT
        );
    };

    public Interface() {
        new ModeSetting.Value(this.language, "Русский");
        new ModeSetting.Value(this.language, "English");
        new ModeSetting.Value(this.language, "Українська");
        new ModeSetting.Value(this.language, "polski");
    }

    public static boolean glassSelected() {
        return DefineVisuals.getInstance().getModuleManager().getModule(Interface.class).liquidGlass.isSelected();
    }

    public static float glass() {
        return liquidGlass();
    }

    public static float liquidGlass() {
        Interface module = instance();
        module.updateStyleAnimations();
        return module.liquidGlassAnim.getValue();
    }

    public static float minimalizm() {
        return minimalism();
    }

    public static float minimalism() {
        Interface module = instance();
        module.updateStyleAnimations();
        return module.minimalismAnim.getValue();
    }

    public static float modernMinimalism() {
        Interface module = instance();
        module.updateStyleAnimations();
        return module.modernMinimalismAnim.getValue();
    }

    public static float blurGlass() {
        Interface module = instance();
        module.updateStyleAnimations();
        return module.blurGlassAnim.getValue();
    }

    public static float nonLiquid() {
        StyleWeights weights = styleWeights();
        return Math.min(1.0f, weights.minimalism() + weights.modernMinimalism() + weights.blurGlass());
    }

    public static StyleWeights styleWeights() {
        Interface module = instance();
        module.updateStyleAnimations();
        return new StyleWeights(
            module.liquidGlassAnim.getValue(),
            module.minimalismAnim.getValue(),
            module.modernMinimalismAnim.getValue(),
            module.blurGlassAnim.getValue()
        );
    }

    public static boolean showGlass() {
        return liquidGlass() > 0.001f;
    }

    public static boolean showMinimalizm() {
        return minimalism() > 0.001f;
    }

    public static boolean showModernMinimalism() {
        return modernMinimalism() > 0.001f;
    }

    public static boolean showBlurGlass() {
        return blurGlass() > 0.001f;
    }

    public static boolean modernMinimalismSelected() {
        return instance().modernMinimalism.isSelected();
    }

    public static boolean blurGlassSelected() {
        return instance().blurGlass.isSelected();
    }

    private static Interface instance() {
        return DefineVisuals.getInstance().getModuleManager().getModule(Interface.class);
    }

    private void updateStyleAnimations() {
        this.liquidGlassAnim.setEasing(Easing.FIGMA_EASE_IN_OUT);
        this.minimalismAnim.setEasing(Easing.FIGMA_EASE_IN_OUT);
        this.modernMinimalismAnim.setEasing(Easing.FIGMA_EASE_IN_OUT);
        this.blurGlassAnim.setEasing(Easing.FIGMA_EASE_IN_OUT);

        this.liquidGlassAnim.update(this.liquidGlass.isSelected());
        this.minimalismAnim.update(this.minimalism.isSelected());
        this.modernMinimalismAnim.update(this.modernMinimalism.isSelected());
        this.blurGlassAnim.update(this.blurGlass.isSelected());
    }

    public record StyleWeights(float liquidGlass, float minimalism, float modernMinimalism, float blurGlass) {
    }

    @Generated
    public ModeSetting getMode() {
        return this.mode;
    }

    @Generated
    public ModeSetting.Value getLiquidGlass() {
        return this.liquidGlass;
    }

    @Generated
    public ModeSetting.Value getMinimalism() {
        return this.minimalism;
    }

    @Generated
    public ModeSetting.Value getModernMinimalism() {
        return this.modernMinimalism;
    }

    @Generated
    public ModeSetting.Value getBlurGlass() {
        return this.blurGlass;
    }

    @Generated
    public ModeSetting getThemeMode() {
        return this.themeMode;
    }

    @Generated
    public ModeSetting.Value getDark() {
        return this.dark;
    }

    @Generated
    public ModeSetting.Value getLight() {
        return this.light;
    }

    @Generated
    public ModeSetting getLanguage() {
        return this.language;
    }

    @Generated
    public Animation getLiquidGlassAnim() {
        return this.liquidGlassAnim;
    }

    @Generated
    public Animation getMinimalismAnim() {
        return this.minimalismAnim;
    }

    @Generated
    public Animation getModernMinimalismAnim() {
        return this.modernMinimalismAnim;
    }

    @Generated
    public Animation getBlurGlassAnim() {
        return this.blurGlassAnim;
    }

    @Generated
    public ExecutorService getExecutor() {
        return this.executor;
    }

    @Generated
    public boolean isLanguageAutoDetected() {
        return this.languageAutoDetected;
    }

    @Generated
    public int getLastLang() {
        return this.lastLang;
    }

    @Generated
    public EventListener<HudRenderEvent> getOnHudRenderEvent() {
        return this.onHudRenderEvent;
    }
}
