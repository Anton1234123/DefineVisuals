package pulse;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public class MatrixTransformStack {
   private MatrixStack matrices;

   public void a(MatrixStack matrices) {
      this.matrices = matrices;
   }

   public void a() {
      this.matrices.push();
   }

   public void b() {
      this.matrices.pop();
   }

   public MatrixStack c() {
      return this.matrices;
   }

   public void a(float x, float y, float z) {
      this.matrices.translate(x, y, z);
   }

   public void b(float x, float y, float z) {
      this.matrices.scale(x, y, z);
   }

   public void a(float degrees) {
      this.matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(degrees));
   }

   public void b(float degrees) {
      this.matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(degrees));
   }

   public void c(float degrees) {
      this.matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(degrees));
   }

   public void c(float xDegrees, float yDegrees, float zDegrees) {
      if (zDegrees != 0.0F) {
         this.c(zDegrees);
      }

      if (yDegrees != 0.0F) {
         this.b(yDegrees);
      }

      if (xDegrees != 0.0F) {
         this.a(xDegrees);
      }
   }

   public void d(float xDegrees, float yDegrees, float zDegrees) {
      this.c(xDegrees, yDegrees, zDegrees);
   }

   public void d(float degrees) {
      this.a(degrees);
   }

   public void e(float degrees) {
      this.b(degrees);
   }

   public void f(float degrees) {
      this.c(degrees);
   }
}
