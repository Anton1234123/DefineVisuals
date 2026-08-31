package moscow.definevisuals.utility.mixins;

import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;

public interface ArmorItemAddition {
   EquipmentType DefineVisuals$getType();

   ArmorMaterial DefineVisuals$getMaterial();
}
