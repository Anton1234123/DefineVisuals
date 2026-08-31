package moscow.definevisuals.systems.modules.modules.visuals;

import lombok.Generated;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.render.HandRenderEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.SliderSetting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;

@ModuleInfo(name = "ViewModel", category = ModuleCategory.VISUALS, desc = "Позволяет изменить положение рук")
public class ViewModel extends BaseModule {
   private final SliderSetting rightHandX = new SliderSetting(this, "Правая рука X").min(-2.0f).max(2.0f).step(0.1f).currentValue(0.0f);
   private final SliderSetting rightHandY = new SliderSetting(this, "Правая рука Y").min(-2.0f).max(2.0f).step(0.1f).currentValue(0.0f);
   private final SliderSetting rightHandZ = new SliderSetting(this, "Правая рука Z").min(-2.0f).max(2.0f).step(0.1f).currentValue(0.0f);
   private final SliderSetting leftHandX = new SliderSetting(this, "Левая рука X").min(-2.0f).max(2.0f).step(0.1f).currentValue(0.0f);
   private final SliderSetting leftHandY = new SliderSetting(this, "Левая рука Y").min(-2.0f).max(2.0f).step(0.1f).currentValue(0.0f);
   private final SliderSetting leftHandZ = new SliderSetting(this, "Левая рука Z").min(-2.0f).max(2.0f).step(0.1f).currentValue(0.0f);

   private final EventListener<HandRenderEvent> onHandRender = event -> {
      if (!this.isEnabled()) return;

      MatrixStack matrices = event.getMatrices();
      if (event.getArm() == Arm.RIGHT) {
         matrices.translate(this.rightHandX.getCurrentValue(), this.rightHandY.getCurrentValue(), this.rightHandZ.getCurrentValue());
      } else {
         matrices.translate(this.leftHandX.getCurrentValue(), this.leftHandY.getCurrentValue(), this.leftHandZ.getCurrentValue());
      }
   };

   public ViewModel() {
   }

   @Generated
   public EventListener<HandRenderEvent> getOnHandRender() {
      return this.onHandRender;
   }
}
