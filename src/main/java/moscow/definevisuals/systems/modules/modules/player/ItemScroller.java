package moscow.definevisuals.systems.modules.modules.player;

import lombok.Generated;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.SliderSetting;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

@ModuleInfo(name = "ItemScroller", category = ModuleCategory.PLAYER, desc = "Позволяет быстро перекладывать вещи в инвенторе")
public class ItemScroller extends BaseModule {
   private final SliderSetting delay = new SliderSetting(this, "Задержка")
      .min(0.0f)
      .max(100.0f)
      .step(1.0f)
      .currentValue(12.0f)
      .suffix(" ms");
   private int lastSlotId = -1;
   private long lastMoveTime;

   public void beginDrag(ScreenHandler handler, Slot slot, int button) {
      this.resetDrag();
      if (!this.canScroll(handler, button)) {
         return;
      }

      this.lastSlotId = slot == null ? -1 : slot.id;
      this.lastMoveTime = System.nanoTime();
   }

   public boolean handleDrag(ScreenHandler handler, Slot slot, int button) {
      if (!this.canScroll(handler, button)) {
         this.resetDrag();
         return false;
      }

      if (slot == null) {
         this.lastSlotId = -1;
         return true;
      }
      if (slot.id == this.lastSlotId) {
         return true;
      }

      long now = System.nanoTime();
      long delayNanos = (long)this.delay.getCurrentValue() * 1_000_000L;
      if (now - this.lastMoveTime < delayNanos) {
         return true;
      }

      this.lastSlotId = slot.id;
      this.lastMoveTime = now;
      if (!slot.hasStack() || !slot.canTakeItems(mc.player)) {
         return true;
      }

      mc.interactionManager.clickSlot(handler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, mc.player);
      return true;
   }

   private boolean canScroll(ScreenHandler handler, int button) {
      return this.isEnabled()
         && button == 0
         && Screen.hasShiftDown()
         && mc.player != null
         && mc.interactionManager != null
         && handler != null
         && handler.getCursorStack().isEmpty();
   }

   public void resetDrag() {
      this.lastSlotId = -1;
      this.lastMoveTime = 0L;
   }

   public ItemScroller() {
   }

   @Override
   public void onDisable() {
      this.resetDrag();
      super.onDisable();
   }

   @Generated
   public SliderSetting getDelay() {
      return this.delay;
   }
}
