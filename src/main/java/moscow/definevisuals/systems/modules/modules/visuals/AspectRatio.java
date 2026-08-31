package moscow.definevisuals.systems.modules.modules.visuals;

import lombok.Generated;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.render.AspectRatioEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.ModeSetting;
import moscow.definevisuals.systems.setting.settings.SliderSetting;

@ModuleInfo(name = "AspectRatio", category = ModuleCategory.VISUALS, desc = "Изменяет соотношение сторон экрана")
public class AspectRatio extends BaseModule {
   private final ModeSetting ratioMode = new ModeSetting(this, "Соотношение");
   private final ModeSetting.Value ratio4_3 = new ModeSetting.Value(this.ratioMode, "4:3");
   private final ModeSetting.Value ratio16_9 = new ModeSetting.Value(this.ratioMode, "16:9");
   private final ModeSetting.Value ratio1_1 = new ModeSetting.Value(this.ratioMode, "1:1");
   private final ModeSetting.Value ratio16_10 = new ModeSetting.Value(this.ratioMode, "16:10");
   private final ModeSetting.Value ratioCustom = new ModeSetting.Value(this.ratioMode, "Кастомное");
   
   private final SliderSetting customValue = new SliderSetting(this, "Значение", () -> !this.ratioCustom.isSelected())
      .min(0.1f)
      .max(5.0f)
      .step(0.1f)
      .currentValue(1.0f);

   private final EventListener<AspectRatioEvent> onAspectRatio = event -> {
      float aspectRatio;
      
      if (this.ratio4_3.isSelected()) {
         aspectRatio = 1.3333334f;
      } else if (this.ratio16_9.isSelected()) {
         aspectRatio = 1.7777778f;
      } else if (this.ratio1_1.isSelected()) {
         aspectRatio = 1.0f;
      } else if (this.ratio16_10.isSelected()) {
         aspectRatio = 1.6f;
      } else {
         aspectRatio = this.customValue.getCurrentValue();
      }
      
      event.setAspectRatio(aspectRatio);
   };

   public AspectRatio() {
   }

   @Generated
   public EventListener<AspectRatioEvent> getOnAspectRatio() {
      return this.onAspectRatio;
   }

   @Generated
   public ModeSetting getRatioMode() {
      return this.ratioMode;
   }

   @Generated
   public ModeSetting.Value getRatio4_3() {
      return this.ratio4_3;
   }

   @Generated
   public ModeSetting.Value getRatio16_9() {
      return this.ratio16_9;
   }

   @Generated
   public ModeSetting.Value getRatio1_1() {
      return this.ratio1_1;
   }

   @Generated
   public ModeSetting.Value getRatio16_10() {
      return this.ratio16_10;
   }

   @Generated
   public ModeSetting.Value getRatioCustom() {
      return this.ratioCustom;
   }

   @Generated
   public SliderSetting getCustomValue() {
      return this.customValue;
   }
}
