package moscow.definevisuals.mixin.minecraft.item;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.commands.commands.NbtCommand;
import moscow.definevisuals.systems.event.impl.render.ItemTooltipEvent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackTooltipMixin {
   @Inject(method = "getTooltip", at = @At("RETURN"))
   private void onGetTooltip(CallbackInfoReturnable<List<Text>> cir) {
      List<Text> tooltip = cir.getReturnValue();
      ItemStack stack = (ItemStack)(Object)this;
      
      // Добавляем NBT если включено
      if (NbtCommand.isEnabled()) {
         addNbtToTooltip(stack, tooltip);
      }
      
      DefineVisuals.getInstance().getEventManager().triggerEvent(new ItemTooltipEvent(stack, tooltip));
   }
   
   private void addNbtToTooltip(ItemStack stack, List<Text> tooltip) {
      NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
      if (customData == null) {
         return;
      }
      
      NbtCompound nbt = customData.getNbt();
      if (nbt == null || nbt.isEmpty()) {
         return;
      }
      
      tooltip.add(Text.of(""));
      tooltip.add(Text.of("§a§lNBT Data:"));
      formatNbtToTooltip(nbt, tooltip, 0);
   }
   
   private void formatNbtToTooltip(NbtElement nbt, List<Text> tooltip, int indent) {
      String prefix = "  ".repeat(indent);
      
      if (nbt instanceof NbtCompound compound) {
         for (String key : compound.getKeys()) {
            NbtElement element = compound.get(key);
            if (element instanceof NbtCompound nestedCompound) {
               tooltip.add(Text.of(prefix + "§e" + key + ":"));
               formatNbtToTooltip(nestedCompound, tooltip, indent + 1);
            } else {
               String value = element.asString();
               tooltip.add(Text.of(prefix + "§7" + key + ": §f" + value));
            }
         }
      } else {
         tooltip.add(Text.of(prefix + "§f" + nbt.asString()));
      }
   }
}
