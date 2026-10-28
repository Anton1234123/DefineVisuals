package moscow.definevisuals.systems.modules.modules.visuals;

import lombok.Getter;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.SliderSetting;

@Getter
@ModuleInfo(
    name = "Item Physics",
    category = ModuleCategory.VISUALS,
    desc = "Реалистичная физика лежащих и вращающихся предметов на земле"
)
public class ItemPhysics extends BaseModule {
    private final SliderSetting scale = new SliderSetting(this, "Масштаб", "Масштаб отображения предметов")
            .min(0.1F).max(2.0F).step(0.05F).currentValue(1.0F);

    private final SliderSetting spinSpeed = new SliderSetting(this, "Скорость вращения", "Скорость вращения предметов в воздухе")
            .min(0.1F).max(3.0F).step(0.1F).currentValue(1.0F);
}
