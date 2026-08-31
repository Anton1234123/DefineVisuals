package moscow.definevisuals.systems.modules.modules.visuals;

import lombok.Generated;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.player.ClientPlayerTickEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.SelectSetting;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import net.minecraft.client.option.Perspective;

@ModuleInfo(name = "Beautifully", category = ModuleCategory.VISUALS, desc = "Улучшение визуальных эффектов (плавный F5, анимации)")
public class Beautifully extends BaseModule {
   private final SelectSetting select = new SelectSetting(this, "Функции");
   private final SelectSetting.Value smoothF5 = new SelectSetting.Value(this.select, "Плавный F5").select();
   private final Animation smoothAnimation = new Animation(300L, Easing.FIGMA_EASE_IN_OUT);
   private final Animation targetAnimation = new Animation(300L, Easing.FIGMA_EASE_IN_OUT);
   private final EventListener<ClientPlayerTickEvent> onTick = event -> {
      if (!this.isEnabled() || !this.smoothF5.isSelected() || mc.options == null) {
         return;
      }

      boolean thirdPerson = mc.options.getPerspective() != Perspective.FIRST_PERSON;
      this.targetAnimation.update(thirdPerson);
      this.smoothAnimation.update(thirdPerson);
   };

   public boolean isSmoothF5Enabled() {
      return this.isEnabled() && this.smoothF5.isSelected();
   }

   @Generated
   public Animation getSmoothAnimation() {
      return this.smoothAnimation;
   }

   @Generated
   public Animation getTargetAnimation() {
      return this.targetAnimation;
   }

   @Generated
   public SelectSetting.Value getSmoothF5() {
      return this.smoothF5;
   }
}
