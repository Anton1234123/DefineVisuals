package pulse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModelDefinition {
   public List<ModelPart> a = new ArrayList<>();
   public int b = 64;
   public int c = 64;

   public Optional<ModelPart> a(String name) {
      for (ModelPart part : this.a) {
         ModelPart found = findPart(name, part);
         if (found != null) {
            return Optional.of(found);
         }
      }

      return Optional.empty();
   }

   private ModelPart findPart(String name, ModelPart part) {
      if (part.d.equals(name)) {
         return part;
      }

      for (ModelPart child : part.b) {
         ModelPart found = findPart(name, child);
         if (found != null) {
            return found;
         }
      }

      return null;
   }
}
