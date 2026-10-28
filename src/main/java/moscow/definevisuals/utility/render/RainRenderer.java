package moscow.definevisuals.utility.render;

import com.mojang.blaze3d.systems.RenderSystem;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.shader.GlProgram;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Random;

public final class RainRenderer implements IMinecraft {
   private static final int MAX_DROPLETS = 5000;
   private static final float DROP_LENGTH = 0.5F;
   private static final float DROP_SPEED = 0.3F;
   private static final float SPLASH_SIZE = 0.1F;
   
   private static GlProgram rainProgram;
   private static VertexBuffer rainBuffer;
   private static RenderLayer rainLayer;
   private static final Random random = new Random();
   private static final Droplet[] droplets = new Droplet[MAX_DROPLETS];
   private static final Splash[] splashes = new Splash[200];
   
   static {
      for (int i = 0; i < MAX_DROPLETS; i++) {
         droplets[i] = new Droplet();
      }
      for (int i = 0; i < 200; i++) {
         splashes[i] = new Splash();
      }
   }
   
   public static void init() {
      rainProgram = new GlProgram(DefineVisuals.id("rain/data"), VertexFormats.POSITION_COLOR);
   }
   
   public static void render(Matrix4f positionMatrix, Matrix4f projectionMatrix, 
                             float density, float opacity, float drops, float splashes) {
      if (mc.world == null || mc.player == null || rainProgram == null) {
         return;
      }
      
      ensureBuffer();
      updateDroplets(density, drops);
      updateSplashes(splashes);
      
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      
      rainProgram.use();
      rainProgram.findUniform("PositionMatrix").set(positionMatrix);
      rainProgram.findUniform("ProjectionMatrix").set(projectionMatrix);
      rainProgram.findUniform("Opacity").set(opacity);
      rainProgram.findUniform("Time").set((float)(System.currentTimeMillis() % 1000000L) / 1000.0F);
      
      Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();
      rainProgram.findUniform("CameraPos").set((float)cameraPos.x, (float)cameraPos.y, (float)cameraPos.z);
      
      rainBuffer.draw(rainLayer);
      
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }
   
   private static void ensureBuffer() {
      if (rainBuffer == null || rainBuffer.isClosed()) {
         rainBuffer = VertexBuffer.createAndUpload(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR, RainRenderer::tessellate);
         rainLayer = createRainLayer();
      }
   }
   
   private static void updateDroplets(float density, float dropIntensity) {
      int activeDroplets = (int)(MAX_DROPLETS * density);
      Vec3d playerPos = mc.player.getPos();
      
      for (int i = 0; i < activeDroplets; i++) {
         Droplet droplet = droplets[i];
         if (!droplet.active) {
            droplet.reset(playerPos);
         }
         droplet.update(dropIntensity);
      }
   }
   
   private static void updateSplashes(float splashIntensity) {
      int activeSplashes = (int)(splashes.length * splashIntensity);
      Vec3d playerPos = mc.player.getPos();
      
      for (int i = 0; i < activeSplashes; i++) {
         Splash splash = splashes[i];
         if (!splash.active) {
            if (random.nextFloat() < 0.02F) {
               splash.reset(playerPos);
            }
         }
         splash.update();
      }
   }
   
   private static void tessellate(VertexConsumer consumer) {
      Matrix4f matrix = new Matrix4f();
      
      // Render droplets
      for (Droplet droplet : droplets) {
         if (!droplet.active) continue;
         
         float x = droplet.x;
         float y = droplet.y;
         float z = droplet.z;
         float alpha = droplet.alpha;
         
         // Rain drop line
         consumer.vertex(matrix, x, y, z).color(0.7F, 0.8F, 1.0F, alpha);
         consumer.vertex(matrix, x, y - DROP_LENGTH, z).color(0.7F, 0.8F, 1.0F, alpha);
      }
      
      // Render splashes
      for (Splash splash : splashes) {
         if (!splash.active) continue;
         
         float x = splash.x;
         float y = splash.y;
         float z = splash.z;
         float size = splash.size * splash.life;
         float alpha = splash.life * 0.5F;
         
         // Splash particles (small circles)
         consumer.vertex(matrix, x - size, y, z - size).color(0.8F, 0.9F, 1.0F, alpha);
         consumer.vertex(matrix, x + size, y, z - size).color(0.8F, 0.9F, 1.0F, alpha);
         consumer.vertex(matrix, x + size, y, z + size).color(0.8F, 0.9F, 1.0F, alpha);
         consumer.vertex(matrix, x - size, y, z + size).color(0.8F, 0.9F, 1.0F, alpha);
      }
   }
   
   private static RenderLayer createRainLayer() {
      return RenderLayer.of(
         "DefineVisuals_rain",
         VertexFormats.POSITION_COLOR,
         VertexFormat.DrawMode.LINES,
         MAX_DROPLETS * 2 + 200 * 4,
         false,
         false,
         RenderLayer.MultiPhaseParameters.builder()
            .program(rainProgram.renderPhaseProgram())
            .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
            .cull(RenderPhase.DISABLE_CULLING)
            .writeMaskState(RenderPhase.COLOR_MASK)
            .build(false)
      );
   }
   
   private static class Droplet {
      float x, y, z;
      float speed;
      float alpha;
      boolean active;
      
      void reset(Vec3d playerPos) {
         this.x = (float)(playerPos.x + (random.nextDouble() - 0.5) * 50.0);
         this.y = (float)(playerPos.y + 20.0 + random.nextDouble() * 10.0);
         this.z = (float)(playerPos.z + (random.nextDouble() - 0.5) * 50.0);
         this.speed = DROP_SPEED + random.nextFloat() * 0.1F;
         this.alpha = 0.3F + random.nextFloat() * 0.4F;
         this.active = true;
      }
      
      void update(float intensity) {
         this.y -= this.speed * intensity;
         
         if (this.y < mc.player.getPos().y - 10.0) {
            this.active = false;
         }
      }
   }
   
   private static class Splash {
      float x, y, z;
      float size;
      float life;
      boolean active;
      
      void reset(Vec3d playerPos) {
         this.x = (float)(playerPos.x + (random.nextDouble() - 0.5) * 30.0);
         this.y = (float)(playerPos.getY() + random.nextDouble() * 0.5);
         this.z = (float)(playerPos.z + (random.nextDouble() - 0.5) * 30.0);
         this.size = SPLASH_SIZE + random.nextFloat() * 0.05F;
         this.life = 1.0F;
         this.active = true;
      }
      
      void update() {
         this.life -= 0.05F;
         if (this.life <= 0.0F) {
            this.active = false;
         }
      }
   }
   
   public RainRenderer() {}
}
