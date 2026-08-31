package moscow.definevisuals.mixin.minecraft.item;

import moscow.definevisuals.utility.mixins.ArmorItemAddition;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorItem.class)
public abstract class ArmorItemMixin implements ArmorItemAddition {
   @Unique
   private EquipmentType DefineVisuals$type;
   @Unique
   private ArmorMaterial DefineVisuals$material;

   @Inject(method = "<init>", at = @At("TAIL"))
   public void saveArgs(ArmorMaterial material, EquipmentType type, Settings settings, CallbackInfo ci) {
      this.DefineVisuals$type = type;
      this.DefineVisuals$material = material;
   }

   @Override
   public ArmorMaterial DefineVisuals$getMaterial() {
      return this.DefineVisuals$material;
   }

   @Override
   public EquipmentType DefineVisuals$getType() {
      return this.DefineVisuals$type;
   }
}
