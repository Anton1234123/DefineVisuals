package pulse;

public class ModelVector {
   public static final ModelVector ZERO = new ModelVector(0.0F, 0.0F, 0.0F);

   public float x;
   public float y;
   public float z;

   public ModelVector(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public float a() {
      return this.x;
   }

   public float b() {
      return this.y;
   }

   public float c() {
      return this.z;
   }

   public void a(float x) {
      this.x = x;
   }

   public void b(float y) {
      this.y = y;
   }

   public void c(float z) {
      this.z = z;
   }

   public void a(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public ModelVector a(ModelVector other) {
      return new ModelVector(
         this.y * other.z - this.z * other.y,
         this.z * other.x - this.x * other.z,
         this.x * other.y - this.y * other.x
      );
   }

   @Override
   public String toString() {
      return this.x + ", " + this.y + ", " + this.z;
   }
}
