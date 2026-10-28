package ru.pulse.cosmetic.loader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import ru.pulse.Pulse;
import ru.pulse.cosmetic.model.CosmeticModel;
import ru.pulse.cosmetic.model.ModelPosition;

public class CosmeticLoader {
   private static CosmeticLoader instance;
   private final Map<Integer, CosmeticModel> loadedCosmetics = new ConcurrentHashMap<>();
   private final Map<String, Identifier> textureCache = new ConcurrentHashMap<>();

   public static CosmeticLoader getInstance() {
      if (instance == null) {
         instance = new CosmeticLoader();
      }

      return instance;
   }

   public CosmeticModel loadFromJson(String var1) {
      return this.loadFromJson(var1, null, -1);
   }

   public CosmeticModel loadFromJson(String var1, Identifier var2) {
      return this.loadFromJson(var1, var2, -1);
   }

   public CosmeticModel loadFromJson(String var1, Identifier var2, int var3) {
      try {
         JsonObject var4 = new JsonParser().parse(var1).getAsJsonObject();
         return this.loadFromJson(var4, var2, var3);
      } catch (Exception var5) {
         Pulse.getLOGGER().error("Error loading cosmetic from JSON string", var5);
         return null;
      }
   }

   public CosmeticModel loadFromJson(JsonObject var1) {
      return this.loadFromJson(var1, null, -1);
   }

   public CosmeticModel loadFromJson(JsonObject var1, Identifier var2, int var3) {
      try {
         if (var1.has("name") && var1.has("model")) {
            String var4 = var1.get("name").getAsString();
            int var5 = var3 > 0 ? var3 : (var1.has("id") ? var1.get("id").getAsInt() : var4.hashCode());
            int var6 = var1.has("category") ? var1.get("category").getAsInt() : 1;
            CosmeticModel var7 = new CosmeticModel(var4, var5, var6);
            JsonObject var8 = var1.getAsJsonObject("model");
            var7.setRawModelJson(var8.toString());
            if (var2 != null) {
               var7.setTextureId(var2);
            } else if (var1.has("texture")) {
               String var9 = var1.get("texture").getAsString();
               Identifier var10 = this.loadTextureFromBase64(var4, var5, var9);
               var7.setTextureId(var10);
            }

            this.parseModelPosition(var1, var7);
            if (var1.has("height")) {
               var7.setHeight(var1.get("height").getAsFloat());
            }

            if (var1.has("scale")) {
               var7.setScale(var1.get("scale").getAsFloat());
            }

            if (var1.has("previewScale")) {
               var7.setPreviewScale(var1.get("previewScale").getAsFloat());
            }

            if (var1.has("previewY")) {
               var7.setPreviewY(var1.get("previewY").getAsFloat());
            }

            if (var1.has("animation")) {
               var7.setAnimationJson(var1.getAsJsonObject("animation"));
            }

            this.loadedCosmetics.put(var5, var7);
            Pulse.getLOGGER().info("Loaded cosmetic: " + var4 + " (id=" + var5 + "), texture=" + var7.getTextureId());
            return var7;
         } else {
            Pulse.getLOGGER().error("Invalid cosmetic JSON: missing name or model");
            return null;
         }
      } catch (Exception var11) {
         Pulse.getLOGGER().error("Error loading cosmetic", var11);
         return null;
      }
   }

   private void parseModelPosition(JsonObject var1, CosmeticModel var2) {
      if (var1.has("pos")) {
         var2.setPosition(ModelPosition.getById(var1.get("pos").getAsInt()));
      }

      if (var1.has("scale")) {
         var2.setScale(var1.get("scale").getAsFloat());
      }

      if (var1.has("x")) {
         var2.setX(var1.get("x").getAsFloat());
      }

      if (var1.has("y")) {
         var2.setY(var1.get("y").getAsFloat());
      }

      if (var1.has("z")) {
         var2.setZ(var1.get("z").getAsFloat());
      }

      if (var1.has("yaw")) {
         var2.setYaw(var1.get("yaw").getAsFloat());
      }

      if (var1.has("pitch")) {
         var2.setPitch(var1.get("pitch").getAsFloat());
      }

      if (var1.has("roll")) {
         var2.setRoll(var1.get("roll").getAsFloat());
      }
   }

   private Identifier loadTextureFromBase64(String var1, int var2, String var3) {
      try {
         String var4 = var1.replace(" ", "").toLowerCase() + "_" + var2;
         String var5 = "cosmetic_" + var4;
         if (this.textureCache.containsKey(var5)) {
            return this.textureCache.get(var5);
         }

         byte[] var6 = Base64.getDecoder().decode(var3);
         ByteArrayInputStream var7 = new ByteArrayInputStream(var6);
         NativeImage var8 = NativeImage.read(var7);
         Identifier var9 = Identifier.of("pulse", "cosmetic/" + var4);
         Runnable var10 = () -> {
            NativeImageBackedTexture var4x = new NativeImageBackedTexture(var8);
            MinecraftClient.getInstance().getTextureManager().registerTexture(var9, var4x);
            this.textureCache.put(var5, var9);
            Pulse.getLOGGER().info("Registered texture: " + var9 + " (" + var8.getWidth() + "x" + var8.getHeight() + ")");
         };
         if (MinecraftClient.getInstance().isOnThread()) {
            var10.run();
         } else {
            MinecraftClient.getInstance().execute(var10);
         }

         this.textureCache.put(var5, var9);
         return var9;
      } catch (Exception var11) {
         Pulse.getLOGGER().error("Failed to load texture for cosmetic: " + var1, var11);
         return null;
      }
   }

   public CosmeticModel getCosmetic(int var1) {
      return this.loadedCosmetics.get(var1);
   }

   public Map<Integer, CosmeticModel> getAllCosmetics() {
      return this.loadedCosmetics;
   }

   public void loadFromInputStream(InputStream var1) {
      try {
         ByteArrayOutputStream var2 = new ByteArrayOutputStream();
         byte[] var3 = new byte[1024];

         int var4;
         while ((var4 = var1.read(var3)) != -1) {
            var2.write(var3, 0, var4);
         }

         String var5 = var2.toString(StandardCharsets.UTF_8.name());
         this.loadFromJson(var5);
      } catch (Exception var6) {
         Pulse.getLOGGER().error("Error loading cosmetic from input stream", var6);
      }
   }
}
