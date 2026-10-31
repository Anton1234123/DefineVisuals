package moscow.definevisuals.systems.modules.modules.player;

import lombok.Generated;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.SliderSetting;

@ModuleInfo(
   name = "Input Guard",
   category = ModuleCategory.PLAYER,
   desc = "modules.descriptions.input_guard"
)
public class InputGuard extends BaseModule {
   private static final int VANILLA_USE_DELAY = 4;
   private final SliderSetting useDelay = new SliderSetting(this, "modules.settings.input_guard.delay")
      .min(VANILLA_USE_DELAY)
      .max(20.0f)
      .step(1.0f)
      .currentValue(6.0f);

   public int getMinimumUseDelay() {
      return Math.max(VANILLA_USE_DELAY, (int)this.useDelay.getCurrentValue());
   }

   @Generated
   public SliderSetting getUseDelay() {
      return this.useDelay;
   }
}
