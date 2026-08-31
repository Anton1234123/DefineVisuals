package moscow.definevisuals.systems.event.impl.render;

import moscow.definevisuals.systems.event.Event;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class ItemTooltipEvent extends Event {
   private final ItemStack itemStack;
   private final List<Text> tooltip;

   public ItemTooltipEvent(ItemStack itemStack, List<Text> tooltip) {
      this.itemStack = itemStack;
      this.tooltip = tooltip;
   }

   public ItemStack getItemStack() {
      return itemStack;
   }

   public List<Text> getTooltip() {
      return tooltip;
   }
}
