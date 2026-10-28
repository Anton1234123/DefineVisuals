package pulse;

public class ModelVertex {
   public ModelVertexUv[] a;
   public ModelVector b;

   public ModelVertex(ModelVertexUv[] vertices, ModelVector normal) {
      this.a = vertices;
      this.b = normal;
   }

   public ModelVertex(ModelVertexUv[] vertices, float normalX, float normalY, float normalZ) {
      this(vertices, new ModelVector(normalX, normalY, normalZ));
   }
}
