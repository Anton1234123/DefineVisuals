package moscow.definevisuals.framework.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import moscow.definevisuals.mixin.accessors.ShaderProgramAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderLoader.LoadException;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus.Internal;

public class GlProgram {
   private static final List<Runnable> REGISTERED_PROGRAMS = new ArrayList<>();
   protected ShaderProgram backingProgram;
   protected ShaderProgramKey programKey;
   private boolean loaded = false;

   public GlProgram(Identifier id, VertexFormat vertexFormat) {
      this.programKey = new ShaderProgramKey(id.withPrefixedPath("core/"), vertexFormat, Defines.EMPTY);
      REGISTERED_PROGRAMS.add(() -> {
         try {
            this.backingProgram = MinecraftClient.getInstance().getShaderLoader().getProgramToLoad(this.programKey);
            this.loaded = true;
            this.setup();
         } catch (LoadException var2) {
            throw new RuntimeException("Failed to initialize shader program", var2);
         }
      });
   }

   public net.minecraft.client.render.RenderPhase.ShaderProgram renderPhaseProgram() {
      return new net.minecraft.client.render.RenderPhase.ShaderProgram(this.programKey);
   }

   public ShaderProgram use() {
      return RenderSystem.setShader(this.programKey);
   }

   protected void setup() {
   }

   public GlUniform findUniform(String name) {
      if (!this.loaded || this.backingProgram == null) {
         return null;
      }
      return ((ShaderProgramAccessor)this.backingProgram).getUniformsByName().get(name);
   }

   public void setUniform(String name, float value) {
      GlUniform uniform = this.findUniform(name);
      if (uniform != null) {
         try {
            uniform.set(value);
         } catch (Exception ignored) {
         }
      }
   }

   public void setUniform(String name, float v1, float v2) {
      GlUniform uniform = this.findUniform(name);
      if (uniform != null) {
         try {
            uniform.set(v1, v2);
         } catch (Exception ignored) {
         }
      }
   }

   public void setUniform(String name, float v1, float v2, float v3, float v4) {
      GlUniform uniform = this.findUniform(name);
      if (uniform != null) {
         try {
            uniform.set(v1, v2, v3, v4);
         } catch (Exception ignored) {
         }
      }
   }

   public void setUniform(String name, float[] values) {
      GlUniform uniform = this.findUniform(name);
      if (uniform != null) {
         try {
            if (values.length == 1) {
               uniform.set(values[0]);
            } else if (values.length == 2) {
               uniform.set(values[0], values[1]);
            } else if (values.length == 3) {
               uniform.set(values[0], values[1], values[2]);
            } else if (values.length == 4) {
               uniform.set(values[0], values[1], values[2], values[3]);
            }
         } catch (Exception ignored) {
         }
      }
   }

   @Internal
   public static void loadAndSetupPrograms() {
      REGISTERED_PROGRAMS.forEach(Runnable::run);
   }
}
