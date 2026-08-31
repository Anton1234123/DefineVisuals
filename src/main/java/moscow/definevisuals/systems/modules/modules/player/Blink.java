package moscow.definevisuals.systems.modules.modules.player;

import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.BooleanSetting;
import moscow.definevisuals.systems.setting.settings.SliderSetting;
import moscow.definevisuals.utility.time.Timer;

@ModuleInfo(name = "Blink", category = ModuleCategory.PLAYER, enabledByDefault = false, desc = "modules.descriptions.blink")
public class Blink extends BaseModule {
    private final Timer timer = new Timer();
    private final SliderSetting time = new SliderSetting(this, "time")
            .min(1.0f)
            .max(20.0f)
            .step(0.5f)
            .currentValue(5.0f);
    private final BooleanSetting pulse = new BooleanSetting(this, "pulse").enabled(false);

    public Timer getTimer() {
        return this.timer;
    }

    public SliderSetting getTime() {
        return this.time;
    }

    public BooleanSetting getPulse() {
        return this.pulse;
    }

    @Override
    public void onEnable() {
        this.timer.reset();
        this.pulse.setEnabled(false);
    }

    @Override
    public void onDisable() {
        this.pulse.setEnabled(false);
    }
}
