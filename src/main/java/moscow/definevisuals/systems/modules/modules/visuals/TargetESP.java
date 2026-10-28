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
import moscow.definevisuals.systems.setting.settings.BooleanSetting;
import moscow.definevisuals.systems.setting.settings.ColorSetting;
import moscow.definevisuals.systems.setting.settings.ModeSetting;
import moscow.definevisuals.systems.setting.settings.SliderSetting;
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
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
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
   private final ModeSetting.Value ghosts = new ModeSetting.Value(this.mode, "Призраки");
   private final ModeSetting.Value crystals = new ModeSetting.Value(this.mode, "Кристаллы").select();
   private final ModeSetting.Value cube = new ModeSetting.Value(this.mode, "Куб");

   private final ColorSetting ghostsColor = new ColorSetting(this, "Цвет призраков", () -> !this.ghosts.isSelected()).color(Colors.ACCENT);
   private final SliderSetting ghostsSpeed = new SliderSetting(this, "Скорость призраков", () -> !this.ghosts.isSelected())
      .min(0.2F).max(3.0F).step(0.1F).currentValue(1.0F);
   private final SliderSetting ghostsRadius = new SliderSetting(this, "Радиус спирали", () -> !this.ghosts.isSelected())
      .min(0.5F).max(3.0F).step(0.1F).currentValue(1.5F);
   private final SliderSetting ghostsStep = new SliderSetting(this, "Плотность призраков", () -> !this.ghosts.isSelected())
      .min(1.0F).max(6.0F).step(1.0F).currentValue(2.0F);
   private final SliderSetting ghostsSize = new SliderSetting(this, "Размер призраков", () -> !this.ghosts.isSelected())
      .min(0.05F).max(0.3F).step(0.01F).currentValue(0.13F);

   private final ColorSetting crystalsColor = new ColorSetting(this, "Цвет кристаллов", () -> !this.crystals.isSelected()).color(Colors.ACCENT);
   private final SliderSetting crystalsSpeed = new SliderSetting(this, "Скорость кристаллов", () -> !this.crystals.isSelected())
      .min(0.2F).max(3.0F).step(0.1F).currentValue(1.0F);
   private final SliderSetting crystalsRadius = new SliderSetting(this, "Радиус кольца", () -> !this.crystals.isSelected())
      .min(0.5F).max(3.0F).step(0.1F).currentValue(1.5F);
   private final SliderSetting crystalsAmount = new SliderSetting(this, "Количество кристаллов", () -> !this.crystals.isSelected())
      .min(8.0F).max(36.0F).step(2.0F).currentValue(18.0F);
   private final SliderSetting crystalsSize = new SliderSetting(this, "Размер кристаллов", () -> !this.crystals.isSelected())
      .min(0.05F).max(0.3F).step(0.01F).currentValue(0.10F);
   private final BooleanSetting crystalsBloom = new BooleanSetting(this, "Свечение кристаллов", () -> !this.crystals.isSelected()).enabled(true);

   private final ModeSetting cubeTexture = new ModeSetting(this, "Текстура", () -> !this.cube.isSelected());
   private final ModeSetting.Value cube1 = new ModeSetting.Value(this.cubeTexture, "Куб 1");
   private final ModeSetting.Value rubik2 = new ModeSetting.Value(this.cubeTexture, "Рубик 2");
   private final ModeSetting.Value cube3 = new ModeSetting.Value(this.cubeTexture, "Кубик 3");
   private final ModeSetting.Value cube4 = new ModeSetting.Value(this.cubeTexture, "Кубек 4").select();
   private final ColorSetting cubeColor = new ColorSetting(this, "Цвет куба", () -> !this.cube.isSelected()).color(Colors.ACCENT);
   private final SliderSetting cubeSize = new SliderSetting(this, "Размер куба", () -> !this.cube.isSelected())
      .min(0.5F).max(3.0F).step(0.1F).currentValue(1.35F);
   private final SliderSetting cubeSpeed = new SliderSetting(this, "Скорость куба", () -> !this.cube.isSelected())
      .min(0.2F).max(3.0F).step(0.1F).currentValue(1.0F);
   private final BooleanSetting cubeGradient = new BooleanSetting(this, "Градиент", () -> !this.cube.isSelected()).enabled(true);

   private final Animation animation = new Animation(300L, 0.0F, Easing.BOTH_CUBIC);
   private final Animation moving = new Animation(70L, 0.0F, Easing.LINEAR);
   private LivingEntity prevTarget;
   
   private final EventListener<Render3DEvent> onRender3D = event -> {
      if (EntityUtility.isInGame()) {
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
            } else if (this.cube.isSelected()) {
               this.drawCube(ms, this.prevTarget);
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
      
      LivingEntity managerTarget = DefineVisuals.getInstance().getTargetManager().getLivingTarget();
      if (managerTarget != null && managerTarget.isAlive()) {
         return managerTarget;
      }

      double reach = 3.0;
      Vec3d eyePos = mc.player.getEyePos();

      if (mc.targetedEntity instanceof LivingEntity living && living.isAlive() && living != mc.player) {
         if (mc.player.distanceTo(living) <= reach || (mc.crosshairTarget != null && mc.crosshairTarget.getPos().squaredDistanceTo(eyePos) <= reach * reach)) {
            return living;
         }
      }
      
      Vec3d lookVec = mc.player.getRotationVec(1.0f);
      Vec3d reachVec = eyePos.add(lookVec.multiply(reach));
      net.minecraft.util.math.Box searchBox = mc.player.getBoundingBox().stretch(lookVec.multiply(reach)).expand(1.0);
      
      EntityHitResult hitResult = ProjectileUtil.raycast(
         mc.player,
         eyePos,
         reachVec,
         searchBox,
         entity -> entity instanceof LivingEntity && entity.isAlive() && entity != mc.player && !entity.isSpectator(),
         reach * reach
      );
      
      if (hitResult != null && hitResult.getEntity() instanceof LivingEntity living) {
         if (mc.player.distanceTo(living) <= reach || hitResult.getPos().squaredDistanceTo(eyePos) <= reach * reach) {
            return living;
         }
      }
      
      return null;
   }

   private void drawCrystals(MatrixStack ms, LivingEntity target) {
      Camera camera = mc.gameRenderer.getCamera();
      ColorRGBA color = this.crystalsColor.getColor();
      float width = this.prevTarget.getWidth() * this.crystalsRadius.getCurrentValue();
      float speed = this.crystalsSpeed.getCurrentValue();
      float crystalSize = this.crystalsSize.getCurrentValue();
      int count = Math.max(4, (int)this.crystalsAmount.getCurrentValue());
      int angleStep = Math.max(1, 360 / count);

      RenderUtility.prepareMatrices(ms, this.getRenderPos(this.prevTarget));
      BufferBuilder builder = CrystalRenderer.createBuffer();

      for (int i = 0; i < 360; i += angleStep) {
         float val = 1.2F - 0.5F * this.animation.getValue();
         float sin = (float)(MathUtility.sin((float)Math.toRadians(i + this.moving.getValue() * 0.3F * speed)) * width * val);
         float cos = (float)(MathUtility.cos((float)Math.toRadians(i + this.moving.getValue() * 0.3F * speed)) * width * val);
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
         CrystalRenderer.render(ms, builder, 0.0F, 0.0F, 0.0F, crystalSize, color.withAlpha(255.0F * this.animation.getValue()));
         ms.pop();
      }

      BufferRenderer.drawWithGlobalProgram(builder.end());

      if (this.crystalsBloom.isEnabled()) {
         Identifier id = DefineVisuals.id("textures/bloom.png");
         RenderSystem.setShaderTexture(0, id);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         BufferBuilder buffer = RenderSystem.renderThreadTesselator().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         float bigSize = crystalSize * 10.0F;

         for (int i = 0; i < 360; i += angleStep) {
            float val = 1.2F - 0.5F * this.animation.getValue();
            float sin = (float)(MathUtility.sin((float)Math.toRadians(i + this.moving.getValue() * 0.3F * speed)) * width * val);
            float cos = (float)(MathUtility.cos((float)Math.toRadians(i + this.moving.getValue() * 0.3F * speed)) * width * val);
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
   }

   private void drawGhosts(MatrixStack ms, LivingEntity target) {
      Camera camera = mc.gameRenderer.getCamera();
      ColorRGBA color = this.ghostsColor.getColor();
      Identifier id = DefineVisuals.id("textures/bloom.png");
      float width = this.prevTarget.getWidth() * this.ghostsRadius.getCurrentValue();
      float speed = this.ghostsSpeed.getCurrentValue();
      float ghostBaseSize = this.ghostsSize.getCurrentValue();
      int step = Math.max(1, (int)this.ghostsStep.getCurrentValue());

      RenderSystem.setShaderTexture(0, id);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      RenderUtility.prepareMatrices(ms, this.getRenderPos(this.prevTarget));
      int wormTick = 0;
      int wormCD = 0;

      for (int i = 0; i < 360; i += step) {
         float size = ghostBaseSize + 0.005F * wormTick;
         float bigSize = ghostBaseSize * 5.4F + 0.005F * wormTick;
         if (wormCD > 0) {
            wormCD -= step;
         } else {
            wormTick += step;
            if (wormTick > 50) {
               wormCD = 100;
               wormTick = 0;
            } else {
               float val = Math.max(0.5F, 1.2F - 0.5F * this.animation.getValue());
               float sin = (float)(MathUtility.sin((float)Math.toRadians(i + this.moving.getValue() * speed)) * width * val);
               float cos = (float)(MathUtility.cos((float)Math.toRadians(i + this.moving.getValue() * speed)) * width * val);
               ms.push();
               ms.translate(
                  sin,
                  this.prevTarget.getHeight() / 1.5F
                     + this.prevTarget.getHeight() / 3.0F * MathUtility.sin(Math.toRadians(i / 2.0F + this.moving.getValue() * speed / 5.0F)),
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

   private void drawCube(MatrixStack ms, LivingEntity target) {
      Camera camera = mc.gameRenderer.getCamera();
      Identifier id;
      if (this.cube1.isSelected()) {
         id = DefineVisuals.id("textures/marker/marker1.png");
      } else if (this.rubik2.isSelected()) {
         id = DefineVisuals.id("textures/marker/marker2.png");
      } else if (this.cube3.isSelected()) {
         id = DefineVisuals.id("textures/marker/marker3.png");
      } else {
         id = DefineVisuals.id("textures/marker/marker.png");
      }
      
      float baseSize = this.cubeSize.getCurrentValue();
      float size = baseSize * this.animation.getValue();
      float half = size / 2.0F;

      Vec3d center = this.getRenderPos(this.prevTarget).add(0.0, target.getHeight() / 2.0, 0.0);
      
      float speed = this.cubeSpeed.getCurrentValue();
      double sin = Math.sin((double)System.currentTimeMillis() / 1000.0 * speed);
      float rotation = (float)(sin * 360.0);
      
      ColorRGBA baseColor = this.cubeColor.getColor();
      float alphaVal = (float)baseColor.getAlpha() * this.animation.getValue() * 0.86F;
      
      ColorRGBA c1, c2, c3, c4;
      if (this.cubeGradient.isEnabled()) {
         c1 = baseColor.withAlpha(alphaVal);
         c2 = Colors.getAccent().withAlpha(alphaVal);
         c3 = baseColor.withAlpha(alphaVal);
         c4 = Colors.getAccent().withAlpha(alphaVal);
      } else {
         c1 = baseColor.withAlpha(alphaVal);
         c2 = baseColor.withAlpha(alphaVal);
         c3 = baseColor.withAlpha(alphaVal);
         c4 = baseColor.withAlpha(alphaVal);
      }

      RenderSystem.setShaderTexture(0, id);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      
      BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

      ms.push();
      RenderUtility.prepareMatrices(ms, center);
      ms.multiply(camera.getRotation());
      ms.multiply(new Quaternionf().rotationZ((float)Math.toRadians(rotation)));
      
      Matrix4f matrix = ms.peek().getPositionMatrix();
      builder.vertex(matrix, -half, half, 0.0F).texture(0.0F, 1.0F).color(c2.getRGB());
      builder.vertex(matrix, half, half, 0.0F).texture(1.0F, 1.0F).color(c3.getRGB());
      builder.vertex(matrix, half, -half, 0.0F).texture(1.0F, 0.0F).color(c4.getRGB());
      builder.vertex(matrix, -half, -half, 0.0F).texture(0.0F, 0.0F).color(c1.getRGB());
      
      BufferRenderer.drawWithGlobalProgram(builder.end());
      ms.pop();
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
