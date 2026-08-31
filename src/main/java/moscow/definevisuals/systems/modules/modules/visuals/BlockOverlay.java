package moscow.definevisuals.systems.modules.modules.visuals;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Generated;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.render.Render3DEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.BooleanSetting;
import moscow.definevisuals.systems.setting.settings.ColorSetting;
import moscow.definevisuals.systems.setting.settings.SliderSetting;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import moscow.definevisuals.utility.render.Draw3DUtility;
import moscow.definevisuals.utility.render.RenderUtility;
import net.minecraft.block.BlockState;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Matrix4f;
@ModuleInfo(name = "BlockOverlay", category = ModuleCategory.VISUALS, enabledByDefault = true, desc = "modules.descriptions.blockoverlay")
public class BlockOverlay extends BaseModule implements IMinecraft {
   private static final double BOX_EXPAND_OUTLINE = 0.002d;
   private static final double BOX_EXPAND_FILL = 0.01d;
   private static final double MAX_DISTANCE_SQ = 36.0d;
   private final BooleanSetting filled = new BooleanSetting(this, "modules.settings.blockoverlay.filled")
           .enabled(true);
   private final SliderSetting fillAlpha = new SliderSetting(this, "modules.settings.blockoverlay.fillAlpha")
           .min(0.1f)
           .max(1.0f)
           .step(0.05f)
           .currentValue(0.6f);
   private final BooleanSetting smoothAnimation = new BooleanSetting(this, "modules.settings.blockoverlay.smooth")
           .enabled(true);
   private final SliderSetting smoothSpeed = new SliderSetting(this, "modules.settings.blockoverlay.smoothSpeed")
           .min(2.0f)
           .max(25.0f)
           .step(0.5f)
           .currentValue(10.0f);
   private final ColorSetting color = new ColorSetting(this, "modules.settings.blockoverlay.color")
           .color(Colors.ACCENT);
   private Box animatedBox;
   private VoxelShape animatedShape;
   private BlockPos targetPos;
   private long lastAnimUpdateNs;
   private final Animation enableAnimation = new Animation(300L, 0.0F, Easing.FIGMA_EASE_IN_OUT);
   private final EventListener<Render3DEvent> onRender3D = event -> {
      if (!this.isEnabled()) {
         this.resetAnimation();
         return;
      }
      if (mc.world == null || mc.player == null) {
         this.resetAnimation();
         return;
      }
      HitResult crosshairTarget = mc.crosshairTarget;
      if (crosshairTarget == null || crosshairTarget.getType() != Type.BLOCK) {
         this.resetAnimation();
         return;
      }
      BlockPos targetPos = ((BlockHitResult) crosshairTarget).getBlockPos();
      double distanceSq = mc.player.squaredDistanceTo(
              targetPos.getX() + 0.5d, targetPos.getY() + 0.5d, targetPos.getZ() + 0.5d);
      if (distanceSq > MAX_DISTANCE_SQ) {
         this.resetAnimation();
         return;
      }
      Box targetBox = this.resolveTargetBox(mc.world, targetPos);
      this.updateAnimatedBox(targetBox);
      if (this.animatedBox == null) {
         return;
      }
      this.enableAnimation.update(true);
      this.renderBlockOverlay(event, this.animatedBox);
   };
   @Override
   public void onDisable() {
      this.resetAnimation();
      this.enableAnimation.update(false);
      super.onDisable();
   }
   private void resetAnimation() {
      this.animatedBox = null;
      this.animatedShape = null;
      this.lastAnimUpdateNs = -1L;
   }
   private Box resolveTargetBox(net.minecraft.world.World world, BlockPos pos) {
      BlockState state = world.getBlockState(pos);
      VoxelShape outline = state.getOutlineShape(world, pos);
      if (outline == null || outline.isEmpty()) {
         return new Box(pos);
      }
      Box shape = outline.getBoundingBox();
      if (shape.isNaN()
              || (shape.minX >= shape.maxX - 1.0E-7d
              && shape.minY >= shape.maxY - 1.0E-7d
              && shape.minZ >= shape.maxZ - 1.0E-7d)) {
         return new Box(pos);
      }
      return shape.offset(pos.getX(), pos.getY(), pos.getZ());
   }
   private void updateAnimatedBox(Box targetBox) {
      long now = System.nanoTime();
      if (!this.smoothAnimation.isEnabled()) {
         this.animatedBox = targetBox;
         this.lastAnimUpdateNs = now;
         return;
      }
      if (this.animatedBox == null || this.lastAnimUpdateNs < 0L) {
         this.animatedBox = targetBox;
         this.lastAnimUpdateNs = now;
         return;
      }
      double elapsedSeconds = (now - this.lastAnimUpdateNs) / 1.0E9d;
      this.lastAnimUpdateNs = now;
      double speed = this.smoothSpeed.getCurrentValue();
      double factor = 1.0d - Math.exp(-speed * Math.max(0.0d, elapsedSeconds));
      this.animatedBox = new Box(
              this.lerp(this.animatedBox.minX, targetBox.minX, factor),
              this.lerp(this.animatedBox.minY, targetBox.minY, factor),
              this.lerp(this.animatedBox.minZ, targetBox.minZ, factor),
              this.lerp(this.animatedBox.maxX, targetBox.maxX, factor),
              this.lerp(this.animatedBox.maxY, targetBox.maxY, factor),
              this.lerp(this.animatedBox.maxZ, targetBox.maxZ, factor)
      );
   }
   private void renderBlockOverlay(Render3DEvent event, Box box) {
      Camera camera = mc.gameRenderer.getCamera();
      Vec3d cameraPos = camera.getPos();
      MatrixStack matrices = event.getMatrices();
      ColorRGBA baseColor = this.color.getColor().withAlpha(255.0F * this.enableAnimation.getValue());
      matrices.push();
      matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
      if (this.filled.isEnabled()) {
         this.renderFill(matrices, box.expand(BOX_EXPAND_FILL), baseColor);
      }
      this.renderOutline(matrices, box.expand(BOX_EXPAND_OUTLINE), baseColor);
      matrices.pop();
   }
   private void renderFill(MatrixStack matrices, Box box, ColorRGBA color) {
      float alpha = this.fillAlpha.getCurrentValue() * color.getAlpha() / 255.0F;
      ColorRGBA fillColor = new ColorRGBA(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 255.0F));
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
      RenderSystem.disableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      BufferBuilder buffer = RenderSystem.renderThreadTesselator().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      Draw3DUtility.renderFilledBox(matrices, buffer, box, fillColor);
      BufferRenderer.drawWithGlobalProgram(buffer.end());
      RenderSystem.depthMask(true);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }
   private void renderOutline(MatrixStack matrices, Box box, ColorRGBA color) {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
      RenderSystem.disableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_LINES);
      RenderSystem.lineWidth(1.6f);
      BufferBuilder buffer = RenderSystem.renderThreadTesselator().begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      Draw3DUtility.renderOutlinedBox(matrices, buffer, box, color);
      BufferRenderer.drawWithGlobalProgram(buffer.end());
      RenderSystem.depthMask(true);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }
   private static double lerp(double start, double end, double factor) {
      return start + (end - start) * factor;
   }
   @Generated
   public BooleanSetting getFilled() {
      return this.filled;
   }
   @Generated
   public SliderSetting getFillAlpha() {
      return this.fillAlpha;
   }
   @Generated
   public BooleanSetting getSmoothAnimation() {
      return this.smoothAnimation;
   }
   @Generated
   public SliderSetting getSmoothSpeed() {
      return this.smoothSpeed;
   }
   @Generated
   public ColorSetting getColor() {
      return this.color;
   }
   @Generated
   public EventListener<Render3DEvent> getOnRender3D() {
      return this.onRender3D;
   }
}