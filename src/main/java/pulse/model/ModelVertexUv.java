package pulse;

public class ModelVertexUv {
   public ModelVector position;
   public float u;
   public float v;

   public ModelVertexUv(float x, float y, float z, float u, float v) {
      this(new ModelVector(x, y, z), u, v);
   }

   public ModelVertexUv(ModelVector position, float u, float v) {
      this.position = position;
      this.u = u;
      this.v = v;
   }
}
