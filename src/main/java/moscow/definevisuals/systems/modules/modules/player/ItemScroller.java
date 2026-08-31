package moscow.definevisuals.systems.modules.modules.player;

import lombok.Generated;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.window.MouseDragEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.SliderSetting;

@ModuleInfo(name = "ItemScroller", category = ModuleCategory.PLAYER, desc = "Позволяет быстро перекладывать вещи в инвенторе")
public class ItemScroller extends BaseModule {
   private final SliderSetting delay = new SliderSetting(this, "Задержка")
      .min(0.0f)
      .max(10.0f)
      .step(1.0f)
      .currentValue(8.0f);

   private final EventListener<MouseDragEvent> onMouseDrag = event -> {
      event.setDelay(this.delay.getCurrentValue() > 0.0f ? (int) this.delay.getCurrentValue() : 0);
   };

   public ItemScroller() {
   }

   @Generated
   public EventListener<MouseDragEvent> getOnMouseDrag() {
      return this.onMouseDrag;
   }

   @Generated
   public SliderSetting getDelay() {
      return this.delay;
   }
}
