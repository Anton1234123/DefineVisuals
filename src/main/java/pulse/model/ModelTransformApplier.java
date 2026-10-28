package pulse;

import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;

public final class ModelTransformApplier {
   private static final float MODEL_SCALE = 16.0F;

   private ModelTransformApplier() {
   }

   public static void translatePartOffset(ModelPart part, MatrixStack matrices) {
      matrices.translate(-part.d() / MODEL_SCALE, part.e() / MODEL_SCALE, part.f() / MODEL_SCALE);
   }

   public static void translatePartPivot(ModelPart part, MatrixStack matrices) {
      matrices.translate(part.j() / MODEL_SCALE, part.k() / MODEL_SCALE, part.l() / MODEL_SCALE);
   }

   public static void translatePartPivotBack(ModelPart part, MatrixStack matrices) {
      matrices.translate(-part.j() / MODEL_SCALE, -part.k() / MODEL_SCALE, -part.l() / MODEL_SCALE);
   }

   public static void rotatePart(ModelPart part, MatrixStack matrices) {
      if (part.c() != 0.0F) {
         matrices.multiply(new Quaternionf().rotationZ(part.c()));
      }

      if (part.b() != 0.0F) {
         matrices.multiply(new Quaternionf().rotationY(part.b()));
      }

      if (part.a() != 0.0F) {
         matrices.multiply(new Quaternionf().rotationX(part.a()));
      }
   }

   public static void scalePart(ModelPart part, MatrixStack matrices) {
      matrices.scale(part.g(), part.h(), part.i());
   }

   public static void translateCubePivot(ModelCube cube, MatrixStack matrices) {
      matrices.translate(cube.c.a() / MODEL_SCALE, cube.c.b() / MODEL_SCALE, cube.c.c() / MODEL_SCALE);
   }

   public static void translateCubePivotBack(ModelCube cube, MatrixStack matrices) {
      matrices.translate(-cube.c.a() / MODEL_SCALE, -cube.c.b() / MODEL_SCALE, -cube.c.c() / MODEL_SCALE);
   }

   public static void rotateCube(ModelCube cube, MatrixStack matrices) {
      if (cube.d.c() != 0.0F) {
         matrices.multiply(new Quaternionf().rotationZ(cube.d.c()));
      }

      if (cube.d.b() != 0.0F) {
         matrices.multiply(new Quaternionf().rotationY(cube.d.b()));
      }

      if (cube.d.a() != 0.0F) {
         matrices.multiply(new Quaternionf().rotationX(cube.d.a()));
      }
   }
}
