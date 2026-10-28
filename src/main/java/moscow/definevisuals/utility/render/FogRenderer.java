package moscow.definevisuals.utility.render;

import com.mojang.blaze3d.systems.RenderSystem;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.shader.GlProgram;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.TriState;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Random;

public final class FogRenderer implements IMinecraft {
   private static final int MAX_FOG_PARTICLES = 3000;
   private static final float FOG_PARTICLE_SIZE = 2.0F;
   private static final float FOG_DRIFT_SPEED = 0.02F;
   
   private static GlProgram fogProgram;
   private static VertexBuffer fogBuffer;
   private static RenderLayer fogLayer;
   private static final Random random = new Random();
   private static final FogParticle[] particles = new FogParticle[MAX_FOG_PARTICLES];
   
   static {
      for (int i = 0; i < MAX_FOG_PARTICLES; i++) {
         particles[i] = new FogParticle();
      }
   }
   
   public static void init() {
      fogProgram = new GlProgram(DefineVisuals.id("fog/data"), VertexFormats.POSITION_COLOR);
   }
   
   public static void render(Matrix4f positionMatrix, Matrix4f projectionMatrix,
                             ColorRGBA fogColor, float density, float coverage,
                             float level, float thickness, boolean highQuality) {
      if (mc.world == null || mc.player == null || fogProgram == null) {
         return;
      }
      
      ensureBuffer();
      updateParticles(density, coverage, level, thickness, highQuality);
      
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.depthMask(false);
      
      fogProgram.use();
      fogProgram.findUniform("PositionMatrix").set(positionMatrix);
      fogProgram.findUniform("ProjectionMatrix").set(projectionMatrix);
      fogProgram.findUniform("FogColor").set(
         fogColor.getRed() / 255.0F,
         fogColor.getGreen() / 255.0F,
         fogColor.getBlue() / 255.0F
      );
      fogProgram.findUniform("Density").set(density);
      fogProgram.findUniform("Thickness").set(thickness);
      fogProgram.findUniform("Time").set((float)(System.currentTimeMillis() % 1000000L) / 1000.0F);
      
      Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();
      fogProgram.findUniform("CameraPos").set((float)cameraPos.x, (float)cameraPos.y, (float)cameraPos.z);
      
      fogBuffer.draw(fogLayer);
      
      RenderSystem.depthMask(true);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }
   
   private static void ensureBuffer() {
      if (fogBuffer == null || fogBuffer.isClosed()) {
         fogBuffer = VertexBuffer.createAndUpload(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR, FogRenderer::tessellate);
         fogLayer = createFogLayer();
      }
   }
   
   private static void updateParticles(float density, float coverage, float level, 
                                      float thickness, boolean highQuality) {
      int activeParticles = (int)(MAX_FOG_PARTICLES * density * coverage);
      Vec3d playerPos = mc.player.getPos();
      
      for (int i = 0; i < activeParticles; i++) {
         FogParticle particle = particles[i];
         if (!particle.active) {
            particle.reset(playerPos, level, thickness, highQuality);
         }
         particle.update(thickness);
      }
   }
   
   private static void tessellate(VertexConsumer consumer) {
      Matrix4f matrix = new Matrix4f();
      
      for (FogParticle particle : particles) {
         if (!particle.active) continue;
         
         float x = particle.x;
         float y = particle.y;
         float z = particle.z;
         float size = particle.size;
         float alpha = particle.alpha;
         
         // Billboard quad facing camera
         float halfSize = size * 0.5F;
         
         consumer.vertex(matrix, x - halfSize, y - halfSize, z).color(1.0F, 1.0F, 1.0F, alpha);
         consumer.vertex(matrix, x + halfSize, y - halfSize, z).color(1.0F, 1.0F, 1.0F, alpha);
         consumer.vertex(matrix, x + halfSize, y + halfSize, z).color(1.0F, 1.0F, 1.0F, alpha);
         consumer.vertex(matrix, x - halfSize, y + halfSize, z).color(1.0F, 1.0F, 1.0F, alpha);
      }
   }
   
   private static RenderLayer createFogLayer() {
      return RenderLayer.of(
         "DefineVisuals_fog",
         VertexFormats.POSITION_COLOR,
         VertexFormat.DrawMode.QUADS,
         MAX_FOG_PARTICLES * 4,
         false,
         false,
         RenderLayer.MultiPhaseParameters.builder()
            .program(fogProgram.renderPhaseProgram())
            .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
            .cull(RenderPhase.DISABLE_CULLING)
            .depthTest(RenderPhase.LEQUAL_DEPTH_TEST)
            .writeMaskState(RenderPhase.COLOR_MASK)
            .build(false)
      );
   }
   
   private static class FogParticle {
      float x, y, z;
      float size;
      float alpha;
      float driftX, driftZ;
      float pulsePhase;
      boolean active;
      
      void reset(Vec3d playerPos, float level, float thickness, boolean highQuality) {
         float spread = highQuality ? 60.0F : 40.0F;
         this.x = (float)(playerPos.x + (random.nextDouble() - 0.5) * spread);
         this.y = (float)(playerPos.y + level + (random.nextDouble() - 0.5) * thickness);
         this.z = (float)(playerPos.z + (random.nextDouble() - 0.5) * spread);
         this.size = FOG_PARTICLE_SIZE + random.nextFloat() * (highQuality ? 2.0F : 1.0F);
         this.alpha = 0.1F + random.nextFloat() * 0.2F;
         this.driftX = (random.nextFloat() - 0.5F) * FOG_DRIFT_SPEED;
         this.driftZ = (random.nextFloat() - 0.5F) * FOG_DRIFT_SPEED;
         this.pulsePhase = random.nextFloat() * 6.28F;
         this.active = true;
      }
      
      void update(float thickness) {
         this.x += this.driftX;
         this.z += this.driftZ;
         this.pulsePhase += 0.02F;
         this.alpha = 0.15F + (float)Math.sin(this.pulsePhase) * 0.05F;
         
         Vec3d playerPos = mc.player.getPos();
         float dist = (float)Math.sqrt(
            (this.x - playerPos.x) * (this.x - playerPos.x) +
            (this.z - playerPos.z) * (this.z - playerPos.z)
         );
         
         if (dist > 50.0F || Math.abs(this.y - playerPos.y) > thickness) {
            this.active = false;
         }
      }
   }
   
   public FogRenderer() {}
}
