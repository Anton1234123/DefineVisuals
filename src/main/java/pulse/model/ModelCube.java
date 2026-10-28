package pulse;

public class ModelCube {
   public ModelVertex[] a;
   public ModelVector b;
   public ModelVector c;
   public ModelVector d;
   public float e;
   public boolean f;

   public ModelCube(float width, float height, float depth) {
      this.a = new ModelVertex[6];
      this.b = new ModelVector(width, height, depth);
      this.c = new ModelVector(0.0F, 0.0F, 0.0F);
      this.d = new ModelVector(0.0F, 0.0F, 0.0F);
      this.e = 0.0F;
      this.f = false;
   }
}
