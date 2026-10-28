package moscow.definevisuals.systems.modules.modules.visuals;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.BooleanSetting;
import moscow.definevisuals.systems.setting.settings.ColorSetting;
import moscow.definevisuals.systems.setting.settings.ModeSetting;
import moscow.definevisuals.systems.setting.settings.SliderSetting;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

@Getter
@ModuleInfo(
    name = "Shader Hand",
    category = ModuleCategory.VISUALS,
    desc = "Премиальные шейдеры и эффекты свечения для рук и предметов"
)
public class ShaderHand extends BaseModule {
    public static boolean rendering = false;

    private final ModeSetting mode = new ModeSetting(this, "Режим");
    private final ModeSetting.Value modeNormal = new ModeSetting.Value(this.mode, "Normal").select();
    private final ModeSetting.Value modeFill1 = new ModeSetting.Value(this.mode, "Fill1");
    private final ModeSetting.Value modeFill2 = new ModeSetting.Value(this.mode, "Fill2");
    private final ModeSetting.Value modeFill3 = new ModeSetting.Value(this.mode, "Fill3");

    private final BooleanSetting glass = new BooleanSetting(this, "Стекло").enable();
    private final SliderSetting glassAlpha = new SliderSetting(this, "Прозрачность стекла", "Степень прозрачности стеклянного эффекта")
            .min(0.0F).max(1.0F).step(0.05F).currentValue(0.5F);

    private final BooleanSetting blur = new BooleanSetting(this, "Размытие фона").enable();
    private final SliderSetting blurStrength = new SliderSetting(this, "Сила размытия", "Интенсивность размытия")
            .min(0.0F).max(20.0F).step(0.5F).currentValue(3.0F);

    private final BooleanSetting glow = new BooleanSetting(this, "Свечение").enable();
    private final SliderSetting glowRadius = new SliderSetting(this, "Радиус свечения", "Радиус размытия Kawase Bloom")
            .min(1.0F).max(5.0F).step(1.0F).currentValue(3.0F);

    private final BooleanSetting outerGlow = new BooleanSetting(this, "Внешнее свечение").enable();
    private final SliderSetting outerExposure = new SliderSetting(this, "Яркость внешнего", "Интенсивность внешнего ореола")
            .min(0.5F).max(5.0F).step(0.1F).currentValue(2.0F);

    private final BooleanSetting innerGlow = new BooleanSetting(this, "Внутреннее свечение");
    private final SliderSetting innerExposure = new SliderSetting(this, "Яркость внутреннего", "Интенсивность внутреннего свечения")
            .min(0.5F).max(5.0F).step(0.1F).currentValue(2.0F);

    private final BooleanSetting handColor = new BooleanSetting(this, "Оригинальный цвет");
    private final ColorSetting customColor = new ColorSetting(this, "Цвет темы").color(Colors.getAccent());

    private final BooleanSetting motion = new BooleanSetting(this, "Шлейф движения");
    private final SliderSetting motionStrength = new SliderSetting(this, "Сила шлейфа", "Интенсивность остаточного шлейфа")
            .min(0.0F).max(0.99F).step(0.05F).currentValue(0.8F);

    private Framebuffer handsBuffer;
    private Framebuffer motionBuffer;
    private Framebuffer tempMotionBuffer;
    private final List<Framebuffer> bloomBuffers = new ArrayList<>();

    private int kawaseDownProgram = -1;
    private int kawaseUpProgram = -1;
    private int innerGlowProgram = -1;
    private int outerGlowProgram = -1;
    private int baseProgram = -1;
    private int blendProgram = -1;
    private long lastDrawTime = 0L;

    @Override
    public void onEnable() {
        this.clearBuffers();
    }

    @Override
    public void onDisable() {
        this.clearBuffers();
    }

    private void clearBuffers() {
        if (this.handsBuffer != null) {
            this.handsBuffer.delete();
            this.handsBuffer = null;
        }
        if (this.motionBuffer != null) {
            this.motionBuffer.delete();
            this.motionBuffer = null;
        }
        if (this.tempMotionBuffer != null) {
            this.tempMotionBuffer.delete();
            this.tempMotionBuffer = null;
        }
        for (Framebuffer buffer : this.bloomBuffers) {
            buffer.delete();
        }
        this.bloomBuffers.clear();
    }

    private void setLinearFilter(Framebuffer buffer) {
        if (buffer != null) {
            GlStateManager._bindTexture(buffer.getColorAttachment());
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GlStateManager._bindTexture(0);
        }
    }

    public Framebuffer getHandsBuffer() {
        int width = mc.getWindow().getFramebufferWidth();
        int height = mc.getWindow().getFramebufferHeight();
        if (this.handsBuffer == null || this.handsBuffer.textureWidth != width || this.handsBuffer.textureHeight != height) {
            if (this.handsBuffer != null) {
                this.handsBuffer.delete();
            }
            this.handsBuffer = new SimpleFramebuffer(width, height, true);
            this.handsBuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            this.setLinearFilter(this.handsBuffer);
        }
        return this.handsBuffer;
    }

    public void beginRender() {
        Framebuffer fbo = this.getHandsBuffer();
        fbo.clear();
        fbo.beginWrite(true);
    }

    public void draw() {
        if (this.handsBuffer == null) {
            return;
        }

        if (this.kawaseDownProgram == -1) {
            this.initShaders();
        }

        if (this.kawaseDownProgram <= 0) {
            return;
        }

        int prevProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        int prevActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        int prevTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

        Framebuffer inputBuffer = this.handsBuffer;

        if (this.motion.isEnabled() && this.motionStrength.getCurrentValue() > 0.0F) {
            long now = System.currentTimeMillis();
            double dt = this.lastDrawTime == 0L ? 0.016 : (now - this.lastDrawTime) / 1000.0;
            dt = Math.max(0.001, Math.min(0.1, dt));

            if (now - this.lastDrawTime > 100L) {
                if (this.motionBuffer != null) {
                    this.motionBuffer.clear();
                }
                if (this.tempMotionBuffer != null) {
                    this.tempMotionBuffer.clear();
                }
                dt = 0.016;
            }

            this.lastDrawTime = now;
            float adjustedStrength = (float) Math.pow(this.motionStrength.getCurrentValue(), dt * 60.0);
            int width = mc.getWindow().getFramebufferWidth();
            int height = mc.getWindow().getFramebufferHeight();

            if (this.motionBuffer == null || this.motionBuffer.textureWidth != width || this.motionBuffer.textureHeight != height) {
                if (this.motionBuffer != null) {
                    this.motionBuffer.delete();
                }
                this.motionBuffer = new SimpleFramebuffer(width, height, true);
                this.motionBuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                this.setLinearFilter(this.motionBuffer);
                this.motionBuffer.clear();
            }

            if (this.tempMotionBuffer == null || this.tempMotionBuffer.textureWidth != width || this.tempMotionBuffer.textureHeight != height) {
                if (this.tempMotionBuffer != null) {
                    this.tempMotionBuffer.delete();
                }
                this.tempMotionBuffer = new SimpleFramebuffer(width, height, true);
                this.tempMotionBuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                this.setLinearFilter(this.tempMotionBuffer);
                this.tempMotionBuffer.clear();
            }

            this.tempMotionBuffer.clear();
            this.tempMotionBuffer.beginWrite(true);

            GL20.glUseProgram(this.blendProgram);
            GL20.glUniform1i(GL20.glGetUniformLocation(this.blendProgram, "currentTexture"), 0);
            GL20.glUniform1i(GL20.glGetUniformLocation(this.blendProgram, "historyTexture"), 1);
            GL20.glUniform1f(GL20.glGetUniformLocation(this.blendProgram, "motionStrength"), adjustedStrength);

            GlStateManager._activeTexture(GL13.GL_TEXTURE1);
            GlStateManager._bindTexture(this.motionBuffer.getColorAttachment());
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

            GlStateManager._activeTexture(GL13.GL_TEXTURE0);
            GlStateManager._bindTexture(this.handsBuffer.getColorAttachment());
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

            this.drawQuads();

            Framebuffer temp = this.motionBuffer;
            this.motionBuffer = this.tempMotionBuffer;
            this.tempMotionBuffer = temp;
            inputBuffer = this.motionBuffer;
        } else {
            if (this.motionBuffer != null) {
                this.motionBuffer.delete();
                this.motionBuffer = null;
            }
            if (this.tempMotionBuffer != null) {
                this.tempMotionBuffer.delete();
                this.tempMotionBuffer = null;
            }
        }

        mc.getFramebuffer().beginWrite(true);
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();

        if (!this.glass.isEnabled() && !this.blur.isEnabled()) {
            RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        } else {
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        }

        ColorRGBA themed = this.customColor.getColor();
        float r = (themed != null ? themed.getRed() : Colors.getAccent().getRed()) / 255.0F;
        float g = (themed != null ? themed.getGreen() : Colors.getAccent().getGreen()) / 255.0F;
        float b = (themed != null ? themed.getBlue() : Colors.getAccent().getBlue()) / 255.0F;

        GL20.glUseProgram(this.baseProgram);
        GL20.glUniform1i(GL20.glGetUniformLocation(this.baseProgram, "inTexture"), 0);
        GL20.glUniform1i(GL20.glGetUniformLocation(this.baseProgram, "bgTexture"), 1);
        float alpha = this.glass.isEnabled() ? this.glassAlpha.getCurrentValue() : 1.0F;
        GL20.glUniform1f(GL20.glGetUniformLocation(this.baseProgram, "alpha"), alpha);
        GL20.glUniform1i(GL20.glGetUniformLocation(this.baseProgram, "doBlur"), this.blur.isEnabled() ? 1 : 0);
        GL20.glUniform1f(GL20.glGetUniformLocation(this.baseProgram, "blurStrength"), this.blurStrength.getCurrentValue());
        GL20.glUniform2f(GL20.glGetUniformLocation(this.baseProgram, "resolution"),
                mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight());
        GL20.glUniform1f(GL20.glGetUniformLocation(this.baseProgram, "time"), (float) (System.nanoTime() / 1.0E9 % 100000.0));
        GL20.glUniform3f(GL20.glGetUniformLocation(this.baseProgram, "themeColor"), r, g, b);
        GL20.glUniform1i(GL20.glGetUniformLocation(this.baseProgram, "useHandColor"), this.handColor.isEnabled() ? 1 : 0);

        int modeVal = 0;
        if (this.modeFill1.isSelected()) {
            modeVal = 1;
        } else if (this.modeFill2.isSelected()) {
            modeVal = 2;
        } else if (this.modeFill3.isSelected()) {
            modeVal = 3;
        }
        GL20.glUniform1i(GL20.glGetUniformLocation(this.baseProgram, "mode"), modeVal);

        GlStateManager._activeTexture(GL13.GL_TEXTURE1);
        GlStateManager._bindTexture(mc.getFramebuffer().getColorAttachment());
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GlStateManager._activeTexture(GL13.GL_TEXTURE0);
        GlStateManager._bindTexture(inputBuffer.getColorAttachment());
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        this.drawQuads();

        if (this.glow.isEnabled()) {
            this.renderKawaseBloom(inputBuffer);
        }

        GL20.glUseProgram(prevProgram);
        GlStateManager._activeTexture(prevActiveTexture);
        GlStateManager._bindTexture(prevTexture);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
    }

    private void renderKawaseBloom(Framebuffer inputBuffer) {
        int iterations = Math.round(this.glowRadius.getCurrentValue());
        this.setupBloomBuffers(iterations);
        int currentTexture = inputBuffer.getColorAttachment();

        ColorRGBA themed = this.customColor.getColor();
        float r = (themed != null ? themed.getRed() : Colors.getAccent().getRed()) / 255.0F;
        float g = (themed != null ? themed.getGreen() : Colors.getAccent().getGreen()) / 255.0F;
        float b = (themed != null ? themed.getBlue() : Colors.getAccent().getBlue()) / 255.0F;

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        GL20.glUseProgram(this.kawaseDownProgram);
        GL20.glUniform1i(GL20.glGetUniformLocation(this.kawaseDownProgram, "inTexture"), 0);

        for (int i = 0; i < iterations; i++) {
            Framebuffer buffer = this.bloomBuffers.get(i);
            buffer.clear();
            buffer.beginWrite(true);
            GL20.glUniform2f(GL20.glGetUniformLocation(this.kawaseDownProgram, "uHalfPixel"),
                    0.5F / buffer.textureWidth, 0.5F / buffer.textureHeight);
            GL20.glUniform2f(GL20.glGetUniformLocation(this.kawaseDownProgram, "uOffset"), 1 + i, 1 + i);

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTexture);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

            this.drawQuads();
            currentTexture = buffer.getColorAttachment();
        }

        GL20.glUseProgram(this.kawaseUpProgram);
        GL20.glUniform1i(GL20.glGetUniformLocation(this.kawaseUpProgram, "inTexture"), 0);
        GL20.glUniform3f(GL20.glGetUniformLocation(this.kawaseUpProgram, "color"), 1.0F, 1.0F, 1.0F);

        for (int i = iterations - 1; i > 0; i--) {
            Framebuffer buffer = this.bloomBuffers.get(i - 1);
            buffer.beginWrite(true);
            GL20.glUniform2f(GL20.glGetUniformLocation(this.kawaseUpProgram, "uHalfPixel"),
                    0.5F / buffer.textureWidth, 0.5F / buffer.textureHeight);
            GL20.glUniform2f(GL20.glGetUniformLocation(this.kawaseUpProgram, "uOffset"), 1 + i, 1 + i);

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTexture);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

            this.drawQuads();
            currentTexture = buffer.getColorAttachment();
        }

        mc.getFramebuffer().beginWrite(true);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        int handColorVal = this.handColor.isEnabled() ? 1 : 0;

        if (this.outerGlow.isEnabled()) {
            GL20.glUseProgram(this.outerGlowProgram);
            GL20.glUniform1i(GL20.glGetUniformLocation(this.outerGlowProgram, "bloomTexture"), 0);
            GL20.glUniform1i(GL20.glGetUniformLocation(this.outerGlowProgram, "maskTexture"), 1);
            GL20.glUniform3f(GL20.glGetUniformLocation(this.outerGlowProgram, "glowColor1"), r, g, b);
            GL20.glUniform3f(GL20.glGetUniformLocation(this.outerGlowProgram, "glowColor2"), r, g, b);
            GL20.glUniform1f(GL20.glGetUniformLocation(this.outerGlowProgram, "alpha"), this.outerExposure.getCurrentValue());
            GL20.glUniform1i(GL20.glGetUniformLocation(this.outerGlowProgram, "useHandColor"), handColorVal);

            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, inputBuffer.getColorAttachment());
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTexture);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

            this.drawQuads();
        }

        if (this.innerGlow.isEnabled()) {
            GL20.glUseProgram(this.innerGlowProgram);
            GL20.glUniform1i(GL20.glGetUniformLocation(this.innerGlowProgram, "bloomTexture"), 0);
            GL20.glUniform1i(GL20.glGetUniformLocation(this.innerGlowProgram, "maskTexture"), 1);
            GL20.glUniform3f(GL20.glGetUniformLocation(this.innerGlowProgram, "glowColor1"), r, g, b);
            GL20.glUniform3f(GL20.glGetUniformLocation(this.innerGlowProgram, "glowColor2"), r, g, b);
            GL20.glUniform1f(GL20.glGetUniformLocation(this.innerGlowProgram, "alpha"), this.innerExposure.getCurrentValue());
            GL20.glUniform1i(GL20.glGetUniformLocation(this.innerGlowProgram, "useHandColor"), handColorVal);

            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, inputBuffer.getColorAttachment());
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTexture);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

            this.drawQuads();
        }

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    private void setupBloomBuffers(int iterations) {
        int fbWidth = mc.getWindow().getFramebufferWidth();
        int fbHeight = mc.getWindow().getFramebufferHeight();

        if (this.bloomBuffers.size() != iterations) {
            for (Framebuffer buffer : this.bloomBuffers) {
                buffer.delete();
            }
            this.bloomBuffers.clear();

            for (int i = 0; i < iterations; i++) {
                int w = (int) Math.max(2.0, fbWidth / Math.pow(2.0, i + 1));
                int h = (int) Math.max(2.0, fbHeight / Math.pow(2.0, i + 1));
                Framebuffer fbo = new SimpleFramebuffer(w, h, false);
                fbo.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                this.setLinearFilter(fbo);
                this.bloomBuffers.add(fbo);
            }
        }

        for (int i = 0; i < iterations; i++) {
            int w = (int) Math.max(2.0, fbWidth / Math.pow(2.0, i + 1));
            int h = (int) Math.max(2.0, fbHeight / Math.pow(2.0, i + 1));
            Framebuffer fbo = this.bloomBuffers.get(i);
            if (fbo.textureWidth != w || fbo.textureHeight != h) {
                fbo.resize(w, h);
                fbo.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                this.setLinearFilter(fbo);
            }
        }
    }

    private void drawQuads() {
        int vao = GL30.glGenVertexArrays();
        int vbo = GL15.glGenBuffers();
        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        float[] vertices = new float[]{
            -1.0F, -1.0F, 0.0F, 0.0F,
             1.0F, -1.0F, 1.0F, 0.0F,
            -1.0F,  1.0F, 0.0F, 1.0F,
             1.0F,  1.0F, 1.0F, 1.0F
        };
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 16, 0L);
        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 16, 8L);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glDeleteVertexArrays(vao);
        GL15.glDeleteBuffers(vbo);
    }

    private void initShaders() {
        String vert = """
            #version 150 core
            in vec2 Position;
            in vec2 UV;
            out vec2 texCoord;
            void main() {
                texCoord = UV;
                gl_Position = vec4(Position, 0.0, 1.0);
            }
            """;

        String baseFrag = """
            #version 150 core
            uniform sampler2D inTexture;
            uniform sampler2D bgTexture;
            uniform float alpha;
            uniform int doBlur;
            uniform float blurStrength;
            uniform vec2 resolution;
            uniform float time;
            uniform vec3 themeColor;
            uniform int useHandColor;
            uniform int mode;
            in vec2 texCoord;
            out vec4 fragColor;

            float wave(float x, float y, float time) {
                return sin(10.0*x + 10.0*y + time) / 5.0 +
                       sin(20.0*x + 15.0*y - time * 1.2) / 3.0 +
                       sin(4.0*x + 10.0*y + time * 0.8) / -4.0 +
                       sin(y + time * 0.5) / 2.0 +
                       sin(x*x*y*20.0 + time * 1.4) +
                       sin(x * 20.0 + 4.0 - time) / 5.0 +
                       sin(y * 30.0 + time * 1.1) / 5.0 +
                       sin(x - time * 0.6) / 4.0;
            }

            void main() {
                vec4 tex = texture(inTexture, texCoord);
                if(tex.a < 0.01) discard;
                vec3 colorToUse = (useHandColor == 1) ? tex.rgb : themeColor;
                if (mode == 1) {
                    vec2 uv = (2.0 * (texCoord * resolution) - resolution) / min(resolution.x, resolution.y);
                    for(float i = 1.0; i < 12.0; i++){
                        uv.x += 0.6 / i * cos(i * 2.5 * uv.y + time);
                        uv.y += 0.6 / i * cos(i * 1.5 * uv.x + time);
                    }
                    float intensity = 0.1 / abs(sin(time - uv.y - uv.x));
                    fragColor = vec4(intensity * colorToUse, tex.a * alpha);
                } else if (mode == 2) {
                    vec2 uv = texCoord;
                    float z = wave(uv.x, uv.y, time) + 2.0;
                    z *= 5.0 + 2.0 * sin(time * 0.4);
                    float d = fract(z);
                    if (mod(z, 2.0) > 1.0) d = 1.0 - d;
                    d = d / fwidth(z);
                    float lineVal = clamp(1.0 - d, 0.0, 1.0);
                    fragColor = vec4(lineVal * colorToUse, tex.a * alpha);
                } else if (mode == 3) {
                    float epsX = 2.0 / resolution.x;
                    float epsY = 2.0 / resolution.y;
                    float aLeft  = texture(inTexture, texCoord + vec2(-epsX, 0.0)).a;
                    float aRight = texture(inTexture, texCoord + vec2(epsX, 0.0)).a;
                    float aDown  = texture(inTexture, texCoord + vec2(0.0, -epsY)).a;
                    float aUp    = texture(inTexture, texCoord + vec2(0.0, epsY)).a;
                    vec2 grad = vec2(aRight - aLeft, aUp - aDown);
                    vec2 dir = length(grad) > 0.001 ? normalize(grad) : vec2(0.0);
                    vec2 mirrorCoord = vec2(1.0 - texCoord.x, texCoord.y);
                    vec2 distortedMirror = mirrorCoord + vec2(-dir.x, dir.y) * 0.05 * (1.0 - tex.a);
                    distortedMirror = clamp(distortedMirror, 0.001, 0.999);
                    vec3 reflected = texture(bgTexture, distortedMirror).rgb;
                    float shine = pow(max(0.0, 1.0 - length(texCoord - vec2(0.5))), 4.0);
                    vec3 glassColor = mix(reflected, colorToUse, 0.25) + vec3(shine * 0.15);
                    float edgeGlow = smoothstep(0.0, 1.0, length(grad) * 2.0);
                    glassColor = mix(glassColor, colorToUse * 1.5, edgeGlow * 0.5);
                    fragColor = vec4(glassColor, tex.a * alpha);
                } else if (doBlur == 1) {
                    vec4 sum = vec4(0.0);
                    vec2 offset = (blurStrength * 0.5) / resolution;
                    sum += texture(bgTexture, texCoord + vec2(-offset.x * 1.5, -offset.y * 1.5)) * 0.09;
                    sum += texture(bgTexture, texCoord + vec2(0.0, -offset.y * 1.5)) * 0.12;
                    sum += texture(bgTexture, texCoord + vec2(offset.x * 1.5, -offset.y * 1.5)) * 0.09;
                    sum += texture(bgTexture, texCoord + vec2(-offset.x * 1.5, 0.0)) * 0.12;
                    sum += texture(bgTexture, texCoord) * 0.16;
                    sum += texture(bgTexture, texCoord + vec2(offset.x * 1.5, 0.0)) * 0.12;
                    sum += texture(bgTexture, texCoord + vec2(-offset.x * 1.5, offset.y * 1.5)) * 0.09;
                    sum += texture(bgTexture, texCoord + vec2(0.0, offset.y * 1.5)) * 0.12;
                    sum += texture(bgTexture, texCoord + vec2(offset.x * 1.5, offset.y * 1.5)) * 0.09;
                    vec4 bgColor = sum;
                    vec3 glassColor = mix(bgColor.rgb, tex.rgb, 0.4) + vec3(tex.a * 0.1);
                    fragColor = vec4(glassColor, alpha);
                } else {
                    fragColor = vec4(tex.rgb, tex.a * alpha);
                }
            }
            """;

        String kawaseDown = """
            #version 150 core
            uniform sampler2D inTexture;
            uniform vec2 uOffset, uHalfPixel;
            in vec2 texCoord;
            out vec4 fragColor;
            void main() {
                vec2 uv = texCoord;
                vec2 halfPixel = uHalfPixel * uOffset;
                vec4 sum = texture(inTexture, uv) * 4.0;
                sum += texture(inTexture, uv - halfPixel);
                sum += texture(inTexture, uv + halfPixel);
                sum += texture(inTexture, uv + vec2(halfPixel.x, -halfPixel.y));
                sum += texture(inTexture, uv - vec2(halfPixel.x, -halfPixel.y));
                fragColor = sum / 8.0;
            }
            """;

        String kawaseUp = """
            #version 150 core
            uniform sampler2D inTexture;
            uniform vec2 uOffset, uHalfPixel;
            uniform vec3 color;
            in vec2 texCoord;
            out vec4 fragColor;
            void main() {
                vec2 uv = texCoord;
                vec2 halfPixel = uHalfPixel * uOffset;
                vec4 sum = texture(inTexture, uv + vec2(-halfPixel.x * 2.0, 0.0));
                sum += texture(inTexture, uv + vec2(-halfPixel.x, halfPixel.y)) * 2.0;
                sum += texture(inTexture, uv + vec2(0.0, halfPixel.y * 2.0));
                sum += texture(inTexture, uv + vec2(halfPixel.x, halfPixel.y)) * 2.0;
                sum += texture(inTexture, uv + vec2(halfPixel.x * 2.0, 0.0));
                sum += texture(inTexture, uv + vec2(halfPixel.x, -halfPixel.y)) * 2.0;
                sum += texture(inTexture, uv + vec2(0.0, -halfPixel.y * 2.0));
                sum += texture(inTexture, uv + vec2(-halfPixel.x, -halfPixel.y)) * 2.0;
                fragColor = vec4((sum / 12.0).rgb * color, (sum / 12.0).a);
            }
            """;

        String innerGlowFrag = """
            #version 150 core
            uniform sampler2D bloomTexture;
            uniform sampler2D maskTexture;
            uniform vec3 glowColor1;
            uniform vec3 glowColor2;
            uniform float alpha;
            uniform int useHandColor;
            in vec2 texCoord;
            out vec4 fragColor;
            void main() {
                vec2 uv = texCoord;
                vec4 bloom = texture(bloomTexture, uv);
                vec4 mask = texture(maskTexture, uv);
                if (mask.a < 0.01) discard;
                vec3 gradientColor = (useHandColor == 1) ? mask.rgb : mix(glowColor1, glowColor2, uv.y);
                float edgeGlow = 1.0 - bloom.a;
                float totalIntensity = (0.5 + edgeGlow * 0.5) * mask.a;
                fragColor = vec4(gradientColor, totalIntensity * alpha);
            }
            """;

        String outerGlowFrag = """
            #version 150 core
            uniform sampler2D bloomTexture;
            uniform sampler2D maskTexture;
            uniform vec3 glowColor1;
            uniform vec3 glowColor2;
            uniform float alpha;
            uniform int useHandColor;
            in vec2 texCoord;
            out vec4 fragColor;
            void main() {
                vec2 uv = texCoord;
                vec4 bloom = texture(bloomTexture, uv);
                vec4 mask = texture(maskTexture, uv);
                float baseGlow = bloom.a * (1.0 - mask.a);
                if (baseGlow <= 0.001) discard;
                vec3 gradientColor = (useHandColor == 1) ? (bloom.rgb / max(bloom.a, 0.001)) : mix(glowColor1, glowColor2, uv.y);
                fragColor = vec4(gradientColor, baseGlow * alpha);
            }
            """;

        String blendFrag = """
            #version 150 core
            uniform sampler2D currentTexture;
            uniform sampler2D historyTexture;
            uniform float motionStrength;
            in vec2 texCoord;
            out vec4 fragColor;
            void main() {
                vec4 current = texture(currentTexture, texCoord);
                vec4 history = texture(historyTexture, texCoord);
                vec4 fadedHistory = history * motionStrength;
                if (fadedHistory.a < 0.02) {
                    fadedHistory = vec4(0.0);
                }
                fragColor = current + fadedHistory * (1.0 - current.a);
            }
            """;

        this.baseProgram = this.createProgram(vert, baseFrag);
        this.kawaseDownProgram = this.createProgram(vert, kawaseDown);
        this.kawaseUpProgram = this.createProgram(vert, kawaseUp);
        this.innerGlowProgram = this.createProgram(vert, innerGlowFrag);
        this.outerGlowProgram = this.createProgram(vert, outerGlowFrag);
        this.blendProgram = this.createProgram(vert, blendFrag);
    }

    private int createProgram(String vert, String frag) {
        int v = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(v, vert);
        GL20.glCompileShader(v);
        if (GL20.glGetShaderi(v, GL20.GL_COMPILE_STATUS) == 0) {
            System.err.println("ShaderHand Vertex Shader Error: " + GL20.glGetShaderInfoLog(v));
        }

        int f = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(f, frag);
        GL20.glCompileShader(f);
        if (GL20.glGetShaderi(f, GL20.GL_COMPILE_STATUS) == 0) {
            System.err.println("ShaderHand Fragment Shader Error: " + GL20.glGetShaderInfoLog(f));
        }

        int p = GL20.glCreateProgram();
        GL20.glAttachShader(p, v);
        GL20.glAttachShader(p, f);
        GL20.glBindAttribLocation(p, 0, "Position");
        GL20.glBindAttribLocation(p, 1, "UV");
        GL20.glLinkProgram(p);
        if (GL20.glGetProgrami(p, GL20.GL_LINK_STATUS) == 0) {
            System.err.println("ShaderHand Program Link Error: " + GL20.glGetProgramInfoLog(p));
        }

        GL20.glDeleteShader(v);
        GL20.glDeleteShader(f);
        return p;
    }

    public boolean shouldRender() {
        if (!this.isEnabled() || mc.player == null) {
            return false;
        }
        ItemStack mainHand = mc.player.getMainHandStack();
        ItemStack offHand = mc.player.getOffHandStack();
        return !this.isMap(mainHand) && !this.isMap(offHand);
    }

    private boolean isMap(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return stack.getItem() == Items.FILLED_MAP || stack.getItem() == Items.MAP;
    }
}
