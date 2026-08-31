package moscow.definevisuals.systems.modules.modules.other;

import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.BooleanSetting;

@ModuleInfo(name = "CompassTooltip", category = ModuleCategory.OTHER, desc = "Подсветка компасов без кулдауна")
public class CompassTooltip extends BaseModule {
    public final BooleanSetting highlightNoCooldown = new BooleanSetting(this, "Подсвечивать без КД").enabled(true);

    public CompassTooltip() {
    }

    public boolean shouldHighlight() {
        return highlightNoCooldown.isEnabled();
    }
}
