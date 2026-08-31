package moscow.definevisuals.systems.modules.modules.visuals;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Generated;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.render.Render3DEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.BooleanSetting;
import moscow.definevisuals.systems.setting.settings.ColorSetting;
import moscow.definevisuals.systems.setting.settings.SliderSetting;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.render.RenderUtility;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

@ModuleInfo(name = "China Hat", category = ModuleCategory.VISUALS, desc = "Рисует китайскую шляпу над головой игрока", enabledByDefault = true)
public class ChinaHat extends BaseModule {
    private static final int CONE_SEGMENTS = 72;
    private static final String HIEROGLYPHS = "富強民主文明和諧自由平等公正法治愛國敬業誠信友善";

    private final ColorSetting topColor = new ColorSetting(this, "Цвет верха").color(Colors.getAccent());
    private final ColorSetting bottomColor = new ColorSetting(this, "Цвет низа").color(Colors.getAdditionalColor());

    private final SliderSetting radius = new SliderSetting(this, "Радиус", "Радиус китайской шляпы").min(0.4F).max(1.2F).step(0.05F).currentValue(0.7F);
    private final SliderSetting height = new SliderSetting(this, "Высота", "Высота конуса шляпы").min(0.2F).max(0.8F).step(0.05F).currentValue(0.4F);

    private final BooleanSetting hideInFirstPerson = new BooleanSetting(this, "Скрывать в первом лице").enable();
    private final BooleanSetting rim = new BooleanSetting(this, "Обод").enable();
    private final BooleanSetting hieroglyphsEnabled = new BooleanSetting(this, "Иероглифы").enable();

    private final EventListener<Render3DEvent> onRender3D = event -> {
        if (mc.player == null || mc.world == null) return;
        if (hideInFirstPerson.isEnabled() && mc.options.getPerspective().isFirstPerson()) return;

        MatrixStack matrices = event.getMatrices();
        Vec3d cameraPos = event.getCamera().getPos();
        float tickDelta = event.getTickDelta();

        Vec3d pos = mc.player.getLerpedPos(tickDelta);
        double x = pos.x - cameraPos.x;
        double y = pos.y + getYOffset(mc.player) + 1.95 - cameraPos.y;
        double z = pos.z - cameraPos.z;

        matrices.push();
        matrices.translate(x, y, z);

        float time = (System.currentTimeMillis() % 360000L) / 100.0F;
        float angle = time * 1.5F;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(net.minecraft.client.gl.ShaderProgramKeys.POSITION_COLOR);

        float rad = radius.getCurrentValue();
        float h = height.getCurrentValue();

        int topFinal = topColor.getColor().getRGB();
        int bottomFinal = bottomColor.getColor().getRGB();
        int bottomRGB = bottomColor.getColor().getRGB();
        int bottomA = 180;

        // Render cone
        renderCone(matrices, rad, h, topFinal, bottomFinal);

        // Render rim
        if (rim.isEnabled()) {
            renderRim(matrices, rad, bottomRGB, bottomA);
        }

        // Render hieroglyphs
        if (hieroglyphsEnabled.isEnabled()) {
            renderHieroglyphs(matrices, rad, bottomRGB, bottomA);
        }

        // Restore render state
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();

        matrices.pop();
    };

    private void renderCone(MatrixStack matrices, float rad, float h, int topColor, int bottomColor) {
        BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        Vec3d apex = new Vec3d(0, h, 0);
        builder.vertex(matrix, (float) apex.x, (float) apex.y, (float) apex.z).color(topColor);

        for (int i = 0; i <= CONE_SEGMENTS; i++) {
            double angle = i * Math.PI * 2 / CONE_SEGMENTS;
            float vx = (float) (Math.cos(angle) * rad);
            float vz = (float) (Math.sin(angle) * rad);
            Vec3d vertex = new Vec3d(vx, 0, vz);
            builder.vertex(matrix, (float) vertex.x, (float) vertex.y, (float) vertex.z).color(bottomColor);
        }

        RenderUtility.buildBuffer(builder);
    }

    private void renderRim(MatrixStack matrices, float rad, int bottomRGB, int bottomA) {
        BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        ColorRGBA bottomColor = ColorRGBA.fromInt(bottomRGB);
        int outer = bottomColor.withAlpha(bottomA * 1.3F).getRGB();
        int inner = bottomColor.withAlpha(0).getRGB();

        for (int i = 0; i <= CONE_SEGMENTS; i++) {
            double angle = i * Math.PI * 2 / CONE_SEGMENTS;
            float vx = (float) (Math.cos(angle) * rad * 1.15F);
            float vz = (float) (Math.sin(angle) * rad * 1.15F);

            Vec3d outerVertex = new Vec3d(vx, 0.01F, vz);
            Vec3d innerVertex = new Vec3d(vx * 0.9F, 0.01F, vz * 0.9F);

            builder.vertex(matrix, (float) outerVertex.x, (float) outerVertex.y, (float) outerVertex.z).color(outer);
            builder.vertex(matrix, (float) innerVertex.x, (float) innerVertex.y, (float) innerVertex.z).color(inner);
        }

        RenderUtility.buildBuffer(builder);
    }

    private void renderHieroglyphs(MatrixStack matrices, float rad, int bottomRGB, int bottomA) {
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        TextRenderer textRenderer = mc.textRenderer;
        float textRadius = rad * 1.2F;
        float textHeight = 0.05F;

        ColorRGBA bottomColor = ColorRGBA.fromInt(bottomRGB);
        int textColor = bottomColor.withAlpha(255 * 0.4F * 1.2F).getRGB();

        matrices.push();
        matrices.translate(0, textHeight, 0);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));

        int charsToDraw = HIEROGLYPHS.length();
        float charAngleStep = 360.0F / charsToDraw;

        for (int i = 0; i < charsToDraw; i++) {
            String ch = String.valueOf(HIEROGLYPHS.charAt(i));

            matrices.push();
            matrices.translate(textRadius, 0, 0);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * charAngleStep + 90.0F));
            matrices.scale(0.015F, -0.015F, 0.015F);

            MatrixStack.Entry entry = matrices.peek();
            float textX = -textRenderer.getWidth(ch) / 2.0F;
            
            textRenderer.draw(
                ch,
                textX,
                0,
                textColor,
                false,
                entry.getPositionMatrix(),
                mc.getBufferBuilders().getEntityVertexConsumers(),
                TextRenderer.TextLayerType.NORMAL,
                0,
                15728880
            );

            matrices.pop();
        }

        matrices.pop();
    }

    private float getYOffset(Entity entity) {
        if (entity instanceof LivingEntity living) {
            if (living.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof ArmorItem) {
                return 0.08F;
            }
        }
        return 0.0F;
    }

    @Generated
    public EventListener<Render3DEvent> getOnRender3D() {
        return onRender3D;
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Generated
    public ColorSetting getTopColor() {
        return topColor;
    }

    @Generated
    public ColorSetting getBottomColor() {
        return bottomColor;
    }

    @Generated
    public SliderSetting getRadius() {
        return radius;
    }

    @Generated
    public SliderSetting getHeight() {
        return height;
    }


    @Generated
    public BooleanSetting getHideInFirstPerson() {
        return hideInFirstPerson;
    }

    @Generated
    public BooleanSetting getRim() {
        return rim;
    }

    @Generated
    public BooleanSetting getHieroglyphsEnabled() {
        return hieroglyphsEnabled;
    }
}
