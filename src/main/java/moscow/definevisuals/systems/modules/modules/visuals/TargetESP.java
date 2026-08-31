package moscow.definevisuals.systems.modules.modules.visuals;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.render.Render3DEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.ColorSetting;
import moscow.definevisuals.systems.setting.settings.ModeSetting;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.game.EntityUtility;
import moscow.definevisuals.utility.math.MathUtility;
import moscow.definevisuals.utility.render.CrystalRenderer;
import moscow.definevisuals.utility.render.DrawUtility;
import moscow.definevisuals.utility.render.RenderUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@ModuleInfo(name = "Target ESP", category = ModuleCategory.VISUALS, desc = "Помечает активную цель")
public class TargetESP extends BaseModule {
   private final ModeSetting mode = new ModeSetting(this, "Режим");
   private final ModeSetting.Value souls = new ModeSetting.Value(this.mode, "Души");
   private final ModeSetting.Value crystals = new ModeSetting.Value(this.mode, "Кристаллы").select();
   private final ModeSetting.Value marker1 = new ModeSetting.Value(this.mode, "куб 1");
   private final ModeSetting.Value marker2 = new ModeSetting.Value(this.mode, "рубик 2");
   private final ModeSetting.Value marker3 = new ModeSetting.Value(this.mode, "кубик 3");
   private final ModeSetting.Value marker4 = new ModeSetting.Value(this.mode, "кубек 4");
   private final ColorSetting colorTarget = new ColorSetting(this, "color").color(Colors.ACCENT);
   private final Animation animation = new Animation(300L, 0.0F, Easing.BOTH_CUBIC);
   private final Animation moving = new Animation(70L, 0.0F, Easing.LINEAR);
   private LivingEntity prevTarget;
   
   private final EventListener<Render3DEvent> onRender3D = event -> {
      if (EntityUtility.isInGame()) {
         // Проверяем наведение на игрока в радиусе 3 блоков
         LivingEntity target = getHoveredTarget();
         
         this.animation.setEasing(Easing.FIGMA_EASE_IN_OUT);
         this.animation.update(target != null);
         this.moving.update(this.moving.getValue() + 10.0F + 50.0F);
         if (target != null) {
            this.prevTarget = target;
         }

         if (this.prevTarget != null && this.animation.getValue() != 0.0F) {
            MatrixStack ms = event.getMatrices();
            ms.push();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
            RenderSystem.enableDepthTest();
            if (mc.world
                  .raycast(
                     new RaycastContext(mc.gameRenderer.getCamera().getPos(), this.prevTarget.getEyePos(), ShapeType.COLLIDER, FluidHandling.NONE, mc.player)
                  )
                  .getType()
               != Type.MISS) {
               RenderSystem.disableDepthTest();
            }

            RenderSystem.disableCull();
            RenderSystem.depthMask(false);
            if (this.crystals.isSelected()) {
               this.drawCrystals(ms, this.prevTarget);
            } else if (this.marker1.isSelected()) {
               this.drawMarker(ms, this.prevTarget, 1);
            } else if (this.marker2.isSelected()) {
               this.drawMarker(ms, this.prevTarget, 2);
            } else if (this.marker3.isSelected()) {
               this.drawMarker(ms, this.prevTarget, 3);
            } else if (this.marker4.isSelected()) {
               this.drawMarker(ms, this.prevTarget, 4);
            } else {
               this.drawGhosts(ms, this.prevTarget);
            }

            RenderSystem.depthMask(true);
            RenderSystem.setShaderTexture(0, 0);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.disableDepthTest();
            ms.pop();
         }
      }
   };

   private LivingEntity getHoveredTarget() {
      if (mc.player == null || mc.world == null) return null;
      
      Vec3d eyePos = mc.player.getEyePos();
      Vec3d lookVec = mc.player.getRotationVec(1.0f).multiply(3.0); // 3 блока дистанция
      Vec3d endPos = eyePos.add(lookVec);
      
      // Рейкаст для поиска сущностей
      for (net.minecraft.entity.Entity entity : mc.world.getEntities()) {
         if (entity == mc.player || !(entity instanceof LivingEntity)) continue;
         
         LivingEntity living = (LivingEntity) entity;
         
         // Проверяем дистанцию
         double distance = mc.player.squaredDistanceTo(entity);
         if (distance > 9.0) continue; // 3 блока в квадрате = 9
         
         // Проверяем наведение через raycast к хитбоксу
         net.minecraft.util.math.Box box = living.getBoundingBox();
         if (box.raycast(eyePos, endPos).isPresent()) {
            return living;
         }
      }
      
      return null;
   }

   private void drawCrystals(MatrixStack ms, LivingEntity target) {
      Camera camera = mc.gameRenderer.getCamera();
      Vec3d cameraPos = camera.getPos();
      ColorRGBA color = this.colorTarget.getColor();
      float width = this.prevTarget.getWidth() * 1.5F;
      RenderUtility.prepareMatrices(ms, this.getRenderPos(this.prevTarget));
      BufferBuilder builder = CrystalRenderer.createBuffer();

      for (int i = 0; i < 360; i += 20) {
         float val = 1.2F - 0.5F * this.animation.getValue();
         float sin = (float)(MathUtility.sin((float)Math.toRadians(i + this.moving.getValue() * 0.3F)) * width * val);
         float cos = (float)(MathUtility.cos((float)Math.toRadians(i + this.moving.getValue() * 0.3F)) * width * val);
         float size = 0.1F;
         ms.push();
         ms.translate(sin, 0.1F + target.getHeight() * Math.abs(MathUtility.sin(i)), cos);
         Vec3d crystalPos = this.getRenderPos(this.prevTarget).add(sin, 1.0, cos);
         Vec3d targetPos = target.getPos().add(0.0, target.getHeight() / 2.0, 0.0);
         Vector3f directionToTarget = new Vector3f(
               (float)(targetPos.x - crystalPos.x), (float)(targetPos.y - crystalPos.y), (float)(targetPos.z - crystalPos.z)
            )
            .normalize();
         Vector3f initialDirection = new Vector3f(0.0F, 1.0F, 0.0F);
         Quaternionf rotation = new Quaternionf().rotationTo(initialDirection, directionToTarget);
         ms.multiply(rotation);
         CrystalRenderer.render(ms, builder, 0.0F, 0.0F, 0.0F, size, color.withAlpha(255.0F * this.animation.getValue()));
         ms.pop();
      }

      BufferRenderer.drawWithGlobalProgram(builder.end());
      Identifier id = DefineVisuals.id("textures/bloom.png");
      RenderSystem.setShaderTexture(0, id);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      BufferBuilder buffer = RenderSystem.renderThreadTesselator().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      float bigSize = 1.0F;

      for (int i = 0; i < 360; i += 20) {
         float val = 1.2F - 0.5F * this.animation.getValue();
         float sin = (float)(MathUtility.sin((float)Math.toRadians(i + this.moving.getValue() * 0.3F)) * width * val);
         float cos = (float)(MathUtility.cos((float)Math.toRadians(i + this.moving.getValue() * 0.3F)) * width * val);
         float size = 0.1F;
         ms.push();
         ms.translate(sin, 0.1F + target.getHeight() * Math.abs(MathUtility.sin(i)), cos);
         ms.multiply(camera.getRotation());
         DrawUtility.drawImage(
            ms,
            buffer,
            (double)(-bigSize / 2.0F),
            (double)(-bigSize / 2.0F),
            0.0,
            (double)bigSize,
            (double)bigSize,
            color.withAlpha(255.0F * this.animation.getValue() * 0.2F)
         );
         ms.pop();
      }

      RenderUtility.buildBuffer(buffer);
   }

   private void drawGhosts(MatrixStack ms, LivingEntity target) {
      Camera camera = mc.gameRenderer.getCamera();
      ColorRGBA color = this.colorTarget.getColor();
      Identifier id = DefineVisuals.id("textures/bloom.png");
      float width = this.prevTarget.getWidth() * 1.5F;
      RenderSystem.setShaderTexture(0, id);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      RenderUtility.prepareMatrices(ms, this.getRenderPos(this.prevTarget));
      int step = 2;
      int wormTick = 0;
      int wormCD = 0;
      int wormCount = 0;

      for (int i = 0; i < 360; i += step) {
         float size = 0.13F + 0.005F * wormTick;
         float bigSize = 0.7F + 0.005F * wormTick;
         if (wormCD > 0) {
            wormCD -= step;
         } else {
            wormTick += step;
            if (wormTick > 50) {
               wormCD = 100;
               wormTick = 0;
               wormCount++;
            } else {
               float val = Math.max(0.5F, 1.2F - 0.5F * this.animation.getValue());
               float sin = (float)(MathUtility.sin((float)Math.toRadians(i + this.moving.getValue() * 1.0F)) * width * val);
               float cos = (float)(MathUtility.cos((float)Math.toRadians(i + this.moving.getValue() * 1.0F)) * width * val);
               ms.push();
               ms.translate(
                  sin,
                  this.prevTarget.getHeight() / 1.5F
                     + this.prevTarget.getHeight() / 3.0F * MathUtility.sin(Math.toRadians(i / 2.0F + this.moving.getValue() / 5.0F)),
                  cos
               );
               ms.multiply(camera.getRotation());
               DrawUtility.drawImage(
                  ms,
                  builder,
                  (double)(-bigSize / 2.0F),
                  (double)(-bigSize / 2.0F),
                  (double)(-size / 2.0F),
                  (double)bigSize,
                  (double)bigSize,
                  color.withAlpha(color.getAlpha() * this.animation.getValue() * 0.05F)
               );
               DrawUtility.drawImage(
                  ms,
                  builder,
                  (double)(-size / 2.0F),
                  (double)(-size / 2.0F),
                  (double)(-size / 2.0F),
                  (double)size,
                  (double)size,
                  color.withAlpha(color.getAlpha() * this.animation.getValue())
               );
               ms.pop();
            }
         }
      }

      BufferRenderer.drawWithGlobalProgram(builder.end());
   }

   private void drawMarker(MatrixStack ms, LivingEntity target, int markerType) {
      Camera camera = mc.gameRenderer.getCamera();
      ColorRGBA color = this.colorTarget.getColor();
      Identifier id;
      switch (markerType) {
         case 1:
            id = DefineVisuals.id("textures/marker/marker1.png");
            break;
         case 2:
            id = DefineVisuals.id("textures/marker/marker2.png");
            break;
         case 3:
            id = DefineVisuals.id("textures/marker/marker3.png");
            break;
         case 4:
            id = DefineVisuals.id("textures/marker/marker.png");
            break;
         default:
            id = DefineVisuals.id("textures/marker/marker.png");
      }
      
      RenderSystem.setShaderTexture(0, id);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      
      float baseSize = 1.2F;
      float size = baseSize * this.animation.getValue();
      Vec3d center = new Vec3d(
         this.getRenderPos(this.prevTarget).x, 
         this.getRenderPos(this.prevTarget).y + target.getHeight() / 2.0, 
         this.getRenderPos(this.prevTarget).z
      );
      
      long time = System.currentTimeMillis();
      double sin = Math.sin((double)time / 1000.0);
      float rotation = (float)(sin * 360.0);
      
      ms.push();
      ms.translate(center.x, center.y, center.z);
      ms.multiply(camera.getRotation());
      ms.multiply(new Quaternionf().rotationY((float)Math.toRadians(rotation)));
      
      DrawUtility.drawImage(
         ms,
         builder,
         (double)(-size / 2.0F),
         (double)(-size / 2.0F),
         0.0,
         (double)size,
         (double)size,
         color.withAlpha(1.0F)
      );
      
      ms.pop();
      BufferRenderer.drawWithGlobalProgram(builder.end());
   }

   private Vec3d getRenderPos(LivingEntity target) {
      float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);
      return new Vec3d(
         MathHelper.lerp(tickDelta, target.prevX, target.getX()),
         MathHelper.lerp(tickDelta, target.prevY, target.getY()),
         MathHelper.lerp(tickDelta, target.prevZ, target.getZ())
      );
   }

   @Override
   public void tick() {
      super.tick();
   }
}
