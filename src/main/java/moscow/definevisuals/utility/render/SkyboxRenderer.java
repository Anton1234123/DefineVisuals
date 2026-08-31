package moscow.definevisuals.utility.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import moscow.definevisuals.framework.shader.impl.SkyShaderProgram;
import moscow.definevisuals.utility.colors.ColorRGBA;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import org.joml.Matrix4f;

public final class SkyboxRenderer {
   private static final float SKYBOX_SIZE = 100.0F;
   private static final int EXPECTED_BUFFER_SIZE = 1536;
   private static final Map<Identifier, RenderLayer> TEXTURE_LAYERS = new HashMap<>();
   private static final Map<SkyShaderProgram, RenderLayer> SHADER_LAYERS = new IdentityHashMap<>();
   private static VertexBuffer skyboxBuffer;

   public static void render(Identifier texture, ColorRGBA color) {
      if (texture == null || color == null) {
         return;
      }

      ensureBuffer();
      // Статичный скайбокс на экране - сбрасываем матрицу в identity
      Matrix4f savedModelView = new Matrix4f(RenderSystem.getModelViewMatrix());
      RenderSystem.getModelViewMatrix().identity();

      RenderSystem.setShaderColor(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
      skyboxBuffer.draw(TEXTURE_LAYERS.computeIfAbsent(texture, SkyboxRenderer::createTextureLayer));
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

      RenderSystem.getModelViewMatrix().set(savedModelView);
   }

   public static void render(SkyShaderProgram shader, ColorRGBA color, float time, float opacity) {
      if (shader == null || color == null) {
         return;
      }

      ensureBuffer();
      // Статичный скайбокс на экране - сбрасываем матрицу в identity
      Matrix4f savedModelView = new Matrix4f(RenderSystem.getModelViewMatrix());
      RenderSystem.getModelViewMatrix().identity();

      shader.updateUniforms(time, color);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, Math.max(0.0F, Math.min(1.0F, opacity)));
      skyboxBuffer.draw(SHADER_LAYERS.computeIfAbsent(shader, SkyboxRenderer::createShaderLayer));
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

      RenderSystem.getModelViewMatrix().set(savedModelView);
   }

   public static void rebuildShaderLayer(SkyShaderProgram shader) {
      if (shader != null) {
         SHADER_LAYERS.put(shader, createShaderLayer(shader));
      }
   }

   public static void close() {
      if (skyboxBuffer != null && !skyboxBuffer.isClosed()) {
         skyboxBuffer.close();
      }

      skyboxBuffer = null;
      TEXTURE_LAYERS.clear();
      SHADER_LAYERS.clear();
   }

   private static void ensureBuffer() {
      if (skyboxBuffer == null || skyboxBuffer.isClosed()) {
         skyboxBuffer = VertexBuffer.createAndUpload(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR, SkyboxRenderer::tessellate);
      }
   }

   private static RenderLayer createTextureLayer(Identifier texture) {
      return RenderLayer.of(
         "DefineVisuals_skybox",
         VertexFormats.POSITION_TEXTURE_COLOR,
         VertexFormat.DrawMode.QUADS,
         EXPECTED_BUFFER_SIZE,
         false,
         false,
         RenderLayer.MultiPhaseParameters.builder()
            .program(RenderPhase.POSITION_TEXTURE_COLOR_PROGRAM)
            .texture(new RenderPhase.Texture(texture, TriState.FALSE, false))
            .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
            .cull(RenderPhase.DISABLE_CULLING)
            .writeMaskState(RenderPhase.COLOR_MASK)
            .build(false)
      );
   }

   private static RenderLayer createShaderLayer(SkyShaderProgram shader) {
      return RenderLayer.of(
         "DefineVisuals_skybox_shader",
         VertexFormats.POSITION_TEXTURE_COLOR,
         VertexFormat.DrawMode.QUADS,
         EXPECTED_BUFFER_SIZE,
         false,
         false,
         RenderLayer.MultiPhaseParameters.builder()
            .program(shader.renderPhaseProgram())
            .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
            .cull(RenderPhase.DISABLE_CULLING)
            .writeMaskState(RenderPhase.COLOR_MASK)
            .build(false)
      );
   }

   private static void tessellate(VertexConsumer vertexConsumer) {
      float size = SKYBOX_SIZE;
      face(vertexConsumer, 1, -size, size, -size, -size, size, size, size, size, size, size, size, -size);
      face(vertexConsumer, 0, -size, -size, size, -size, -size, -size, size, -size, -size, size, -size, size);
      face(vertexConsumer, 2, size, size, -size, size, -size, -size, -size, -size, -size, -size, size, -size);
      face(vertexConsumer, 4, -size, size, size, -size, -size, size, size, -size, size, size, size, size);
      face(vertexConsumer, 3, -size, size, -size, -size, -size, -size, -size, -size, size, -size, size, size);
      face(vertexConsumer, 5, size, size, size, size, -size, size, size, -size, -size, size, size, -size);
   }

   private static void face(
      VertexConsumer vertexConsumer,
      int face,
      float x1, float y1, float z1,
      float x2, float y2, float z2,
      float x3, float y3, float z3,
      float x4, float y4, float z4
   ) {
      int column = face % 3;
      int row = face / 3;
      float u1 = (float)column / 3.0F;
      float u2 = (float)(column + 1) / 3.0F;
      float v1 = (float)row / 2.0F;
      float v2 = (float)(row + 1) / 2.0F;
      Matrix4f matrix = new Matrix4f();
      vertexConsumer.vertex(matrix, x1, y1, z1).texture(u1, v1).color(-1);
      vertexConsumer.vertex(matrix, x2, y2, z2).texture(u1, v2).color(-1);
      vertexConsumer.vertex(matrix, x3, y3, z3).texture(u2, v2).color(-1);
      vertexConsumer.vertex(matrix, x4, y4, z4).texture(u2, v1).color(-1);
   }

   private SkyboxRenderer() {
   }
}
