package moscow.definevisuals.cosmetics.geckolib;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.cosmetics.geo.GeoModel;
import moscow.definevisuals.cosmetics.geo.GeoModelParser;
import moscow.definevisuals.cosmetics.model.CosmeticModel;

public class GeckolibModelParser {
   public GeoModel parseModel(CosmeticModel var1) {
      try {
         String var2 = var1.getRawModelJson();
         if (var2 == null) {
            DefineVisuals.getLOGGER().error("No raw model JSON available for cosmetic: " + var1.getName());
            return null;
         } else {
            GeoModel var3 = GeoModelParser.parse(var2);
            if (var3 == null) {
               DefineVisuals.getLOGGER().error("Failed to parse GeoModel for: " + var1.getName());
               return null;
            } else {
               DefineVisuals.getLOGGER().info("Successfully parsed model for: " + var1.getName() + " (bones: " + var3.topLevelBones.size() + ")");
               return var3;
            }
         }
      } catch (Exception var4) {
         DefineVisuals.getLOGGER().error("Error parsing model for: " + var1.getName(), var4);
         return null;
      }
   }
}
