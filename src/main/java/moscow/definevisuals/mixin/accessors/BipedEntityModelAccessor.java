package moscow.definevisuals.mixin.accessors;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BipedEntityModel.class)
public interface BipedEntityModelAccessor {
   @Accessor("head")
   ModelPart DefineVisuals$getHead();

   @Accessor("hat")
   ModelPart DefineVisuals$getHat();

   @Accessor("body")
   ModelPart DefineVisuals$getBody();

   @Accessor("rightArm")
   ModelPart DefineVisuals$getRightArm();

   @Accessor("leftArm")
   ModelPart DefineVisuals$getLeftArm();

   @Accessor("rightLeg")
   ModelPart DefineVisuals$getRightLeg();

   @Accessor("leftLeg")
   ModelPart DefineVisuals$getLeftLeg();
}
