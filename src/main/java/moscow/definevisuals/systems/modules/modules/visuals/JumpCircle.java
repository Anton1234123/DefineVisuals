package moscow.definevisuals.systems.modules.modules.visuals;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.mixin.accessors.FramebufferAccessor;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.game.EntityJumpEvent;
import moscow.definevisuals.systems.event.impl.player.ClientPlayerTickEvent;
import moscow.definevisuals.systems.event.impl.render.Render3DEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.BooleanSetting;
import moscow.definevisuals.systems.setting.settings.ColorSetting;
import moscow.definevisuals.systems.setting.settings.ModeSetting;
import moscow.definevisuals.systems.setting.settings.SliderSetting;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.render.RenderUtility;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

@Getter
@ModuleInfo(
    name = "Jump Circle",
    category = ModuleCategory.VISUALS,
    desc = "Эффектные круги и пространственное искажение при прыжках"
)
public class JumpCircle extends BaseModule {
    private static final Identifier TEXTURE_CLIENT = DefineVisuals.id("textures/effects/circle.png");
    private static final Identifier TEXTURE_LARGE = DefineVisuals.id("textures/effects/jump_circle/circle_large.png");
    private static final Identifier TEXTURE_SLIM = DefineVisuals.id("textures/effects/jump_circle/circle_slim.png");
    public static final long LIFESPAN = 1300L;

    public static class CircleInstance {
        public final long creationTime = System.currentTimeMillis();
        public final double x;
        public final double y;
        public final double z;

        public CircleInstance(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private final SliderSetting radius = new SliderSetting(this, "Радиус", "Радиус круга прыжка")
            .min(1.0F).max(6.0F).step(0.1F).currentValue(3.2F);

    private final BooleanSetting distortion = new BooleanSetting(this, "Искажение").enable();
    private final SliderSetting distortionStrength = new SliderSetting(this, "Сила искажения", "Интенсивность шейдера искажения", () -> !this.distortion.isEnabled())
            .min(0.2F).max(2.5F).step(0.1F).currentValue(1.0F);

    private final BooleanSetting chromatic = new BooleanSetting(this, "Хроматизм", () -> !this.distortion.isEnabled()).enable();
    private final BooleanSetting texture = new BooleanSetting(this, "Текстура").enable();

    private final ModeSetting style = new ModeSetting(this, "Стиль", () -> !this.texture.isEnabled());
    private final ModeSetting.Value styleClient = new ModeSetting.Value(this.style, "Client").select();
    private final ModeSetting.Value styleLarge = new ModeSetting.Value(this.style, "Large");
    private final ModeSetting.Value styleSlim = new ModeSetting.Value(this.style, "Slim");

    private final ColorSetting customColor = new ColorSetting(this, "Цвет темы").color(Colors.getAccent());

    private final List<CircleInstance> circles = new CopyOnWriteArrayList<>();
    private Framebuffer distortionFbo = null;
    private int distortionProgram = -1;
    private int distortionVao = -1;
    private int distortionVbo = -1;

    private final EventListener<EntityJumpEvent> onJump = event -> {
        if (mc.player != null && event.getEntity() == mc.player) {
            this.circles.add(new CircleInstance(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ()));
        }
    };

    private final EventListener<ClientPlayerTickEvent> onTick = event -> {
        this.circles.removeIf(circle -> System.currentTimeMillis() - circle.creationTime > 1500L);
    };

    private final EventListener<Render3DEvent> onRender3D = event -> {
        if (mc.player == null || mc.world == null || this.circles.isEmpty()) {
            return;
        }

        if (this.distortion.isEnabled()) {
            this.renderDistortionShader(event);
        }

        if (this.texture.isEnabled()) {
            this.renderJumpCircleMeshes(event);
        }
    };

    private Identifier getCircleTexture() {
        if (this.styleLarge.isSelected()) {
            return TEXTURE_LARGE;
        }
        if (this.styleSlim.isSelected()) {
            return TEXTURE_SLIM;
        }
        return TEXTURE_CLIENT;
    }

    private void renderDistortionShader(Render3DEvent event) {
        if (event.getCamera() == null) {
            return;
        }
        Framebuffer mainFbo = mc.getFramebuffer();
        if (mainFbo == null) {
            return;
        }

        int width = mainFbo.textureWidth;
        int height = mainFbo.textureHeight;
        if (width <= 0 || height <= 0) {
            return;
        }

        this.initDistortionShader();
        if (this.distortionProgram == -1) {
            return;
        }
        this.initDistortionQuad();
        if (this.distortionVao == -1) {
            return;
        }

        this.ensureDistortionFbo(width, height);

        int prevReadFbo = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        int prevDrawFbo = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        int mainFboId = ((FramebufferAccessor) mainFbo).getFbo();
        int distFboId = ((FramebufferAccessor) this.distortionFbo).getFbo();

        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, mainFboId);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, distFboId);
        GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, prevReadFbo);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, prevDrawFbo);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mainFboId);

        int prevProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        int prevVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        int prevVbo = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
        int prevActiveTex = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        int prevTex0 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        int prevTex1 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        boolean prevBlend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean prevDepthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        boolean prevCull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        boolean prevDepthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.disableBlend();

        GL20.glUseProgram(this.distortionProgram);

        Matrix4f viewRotMat = new Matrix4f().rotation(new Quaternionf(event.getCamera().getRotation()).conjugate());
        Matrix4f projMat = new Matrix4f(event.getProjectionMatrix());
        Matrix4f viewProjMat = new Matrix4f(projMat).mul(viewRotMat);
        Matrix4f invViewProjMat = new Matrix4f(viewProjMat).invert();

        float[] invViewProjArr = new float[16];
        invViewProjMat.get(invViewProjArr);
        float[] viewProjArr = new float[16];
        viewProjMat.get(viewProjArr);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.distortionFbo.getColorAttachment());

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, mainFbo.getDepthAttachment());
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, 34892, 0);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        this.setUniform1i("uColorTexture", 0);
        this.setUniform1i("uDepthTexture", 1);
        this.setUniformMatrix4fv("uInvViewProjMat", invViewProjArr);
        this.setUniformMatrix4fv("uViewProjMat", viewProjArr);
        this.setUniform2f("uResolution", width, height);
        this.setUniform1f("uDistortionStrength", this.distortionStrength.getCurrentValue());
        this.setUniform1i("uChromatic", this.chromatic.isEnabled() ? 1 : 0);

        double camX = event.getCamera().getPos().x;
        double camY = event.getCamera().getPos().y;
        double camZ = event.getCamera().getPos().z;
        float baseRadius = this.radius.getCurrentValue();
        int maxCircles = 16;
        int count = 0;

        for (CircleInstance circle : this.circles) {
            if (count >= maxCircles) {
                break;
            }
            long age = System.currentTimeMillis() - circle.creationTime;
            if (age <= LIFESPAN) {
                float progress = (float) age / (float) LIFESPAN;
                float sizeEase = 1.0F - (float) Math.pow(1.0F - progress, 3.0);
                float r = sizeEase * baseRadius * 0.5F;
                float alpha = 1.0F - progress;
                if (alpha > 0.0F) {
                    float relX = (float) (circle.x - camX);
                    float relY = (float) (circle.y - camY);
                    float relZ = (float) (circle.z - camZ);
                    this.setUniform3f("uCirclePos[" + count + "]", relX, relY, relZ);
                    this.setUniform1f("uCircleRadius[" + count + "]", r);
                    this.setUniform1f("uCircleProgress[" + count + "]", progress);
                    this.setUniform1f("uCircleAlpha[" + count + "]", alpha);
                    count++;
                }
            }
        }

        this.setUniform1i("uCircleCount", count);

        GL30.glBindVertexArray(this.distortionVao);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
        GL30.glBindVertexArray(prevVao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, prevVbo);

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, prevTex1);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, prevTex0);
        RenderSystem.setShaderTexture(0, prevTex0);
        GL13.glActiveTexture(prevActiveTex);
        GL20.glUseProgram(prevProgram);

        if (prevDepthTest) {
            RenderSystem.enableDepthTest();
        }
        if (prevCull) {
            RenderSystem.enableCull();
        }
        if (prevBlend) {
            RenderSystem.enableBlend();
        }
        RenderSystem.depthMask(prevDepthMask);
    }

    private void renderJumpCircleMeshes(Render3DEvent event) {
        double camX = event.getCamera().getPos().x;
        double camY = event.getCamera().getPos().y;
        double camZ = event.getCamera().getPos().z;

        ColorRGBA themed = this.customColor.getColor();
        float r = (themed != null ? themed.getRed() : Colors.getAccent().getRed()) / 255.0F;
        float g = (themed != null ? themed.getGreen() : Colors.getAccent().getGreen()) / 255.0F;
        float b = (themed != null ? themed.getBlue() : Colors.getAccent().getBlue()) / 255.0F;
        float baseRadius = this.radius.getCurrentValue();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);
        RenderSystem.depthMask(false);
        RenderSystem.setShaderTexture(0, this.getCircleTexture());
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);

        MatrixStack matrixStack = event.getMatrices();

        for (CircleInstance circle : this.circles) {
            long age = System.currentTimeMillis() - circle.creationTime;
            if (age <= LIFESPAN) {
                float progress = (float) age / (float) LIFESPAN;
                float sizeEase = 1.0F - (float) Math.pow(1.0F - progress, 3.0);
                float size = sizeEase * baseRadius;
                float spinEase = progress * progress * progress;
                float rotationAngle = spinEase * 360.0F;
                float alpha = 1.0F - progress;
                if (alpha > 0.0F) {
                    matrixStack.push();
                    matrixStack.translate(circle.x - camX, circle.y - camY + 0.02, circle.z - camZ);
                    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationAngle));
                    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
                    matrixStack.scale(size, size, size);

                    Matrix4f matrix = matrixStack.peek().getPositionMatrix();
                    BufferBuilder buffer = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                    buffer.vertex(matrix, -0.5F, -0.5F, 0.0F).texture(0.0F, 0.0F).color(r, g, b, alpha);
                    buffer.vertex(matrix, -0.5F,  0.5F, 0.0F).texture(0.0F, 1.0F).color(r, g, b, alpha);
                    buffer.vertex(matrix,  0.5F,  0.5F, 0.0F).texture(1.0F, 1.0F).color(r, g, b, alpha);
                    buffer.vertex(matrix,  0.5F, -0.5F, 0.0F).texture(1.0F, 0.0F).color(r, g, b, alpha);
                    RenderUtility.buildBuffer(buffer);

                    matrixStack.pop();
                }
            }
        }

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private void ensureDistortionFbo(int width, int height) {
        if (this.distortionFbo == null || this.distortionFbo.textureWidth != width || this.distortionFbo.textureHeight != height) {
            if (this.distortionFbo != null) {
                this.distortionFbo.delete();
            }
            this.distortionFbo = new SimpleFramebuffer(width, height, false);
            this.distortionFbo.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GlStateManager._bindTexture(this.distortionFbo.getColorAttachment());
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GlStateManager._bindTexture(0);
        }
    }

    private void initDistortionQuad() {
        if (this.distortionVao == -1) {
            float[] vertices = new float[]{
                -1.0F, -1.0F, 0.0F, 0.0F,
                 1.0F, -1.0F, 1.0F, 0.0F,
                 1.0F,  1.0F, 1.0F, 1.0F,
                -1.0F, -1.0F, 0.0F, 0.0F,
                 1.0F,  1.0F, 1.0F, 1.0F,
                -1.0F,  1.0F, 0.0F, 1.0F
            };
            int prevVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
            int prevVbo = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
            this.distortionVao = GL30.glGenVertexArrays();
            this.distortionVbo = GL15.glGenBuffers();
            GL30.glBindVertexArray(this.distortionVao);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.distortionVbo);
            FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
            buffer.put(vertices).flip();
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 16, 0L);
            GL20.glEnableVertexAttribArray(0);
            GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 16, 8L);
            GL20.glEnableVertexAttribArray(1);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, prevVbo);
            GL30.glBindVertexArray(prevVao);
        }
    }

    private void initDistortionShader() {
        if (this.distortionProgram == -1) {
            String vert = """
                #version 150 core
                in vec2 Position;
                in vec2 TexCoord;
                out vec2 vTexCoord;
                void main() {
                    vTexCoord = TexCoord;
                    gl_Position = vec4(Position, 0.0, 1.0);
                }
                """;

            String frag = """
                #version 150 core
                in vec2 vTexCoord;
                out vec4 fragColor;

                uniform sampler2D uColorTexture;
                uniform sampler2D uDepthTexture;
                uniform mat4 uInvViewProjMat;
                uniform mat4 uViewProjMat;
                uniform vec2 uResolution;
                uniform float uDistortionStrength;
                uniform int uChromatic;

                const int MAX_CIRCLES = 16;
                uniform int uCircleCount;
                uniform vec3 uCirclePos[MAX_CIRCLES];
                uniform float uCircleRadius[MAX_CIRCLES];
                uniform float uCircleProgress[MAX_CIRCLES];
                uniform float uCircleAlpha[MAX_CIRCLES];

                void main() {
                    vec4 baseColor = texture(uColorTexture, vTexCoord);
                    if (uCircleCount <= 0 || uDistortionStrength <= 0.001) {
                        fragColor = baseColor;
                        return;
                    }

                    float rawDepth = texture(uDepthTexture, vTexCoord).r;
                    if (rawDepth >= 0.999999) {
                        fragColor = baseColor;
                        return;
                    }

                    vec4 clipPos = vec4(vTexCoord * 2.0 - 1.0, rawDepth * 2.0 - 1.0, 1.0);
                    vec4 camRelPosH = uInvViewProjMat * clipPos;
                    vec3 camRelPos = camRelPosH.xyz / max(0.0001, camRelPosH.w);

                    vec2 totalUVShift = vec2(0.0);
                    float totalChromatic = 0.0;

                    for (int i = 0; i < uCircleCount; i++) {
                        if (i >= MAX_CIRCLES) break;

                        vec3 cPos = uCirclePos[i];
                        float radius = uCircleRadius[i];
                        float progress = uCircleProgress[i];
                        float alpha = uCircleAlpha[i];

                        if (alpha <= 0.001 || radius <= 0.01) continue;

                        vec3 delta = camRelPos - cPos;
                        float dist3D = length(delta);

                        float maxEffectRadius = radius * 1.05;
                        if (dist3D > maxEffectRadius) continue;

                        float waveWidth = max(0.12, radius * 0.28);
                        float ringDist = abs(dist3D - radius);
                        float ringFactor = smoothstep(waveWidth, 0.0, ringDist);
                        float ripple = sin((dist3D - radius) / waveWidth * 3.14159265) * ringFactor;

                        float blackholePull = smoothstep(radius, 0.0, dist3D) * (1.0 - progress);

                        vec3 normalDir = delta / max(0.001, dist3D);
                        vec3 tangent = normalize(cross(vec3(0.0, 1.0, 0.0), delta + vec3(0.0001)));
                        float swirl = blackholePull * 0.40 * sin(progress * 3.14159265);

                        vec3 worldDisplace = (normalDir * (ripple * 0.38 - blackholePull * 0.28) + tangent * swirl)
                                             * alpha * uDistortionStrength;

                        vec3 displacedWorldPos = camRelPos + worldDisplace;

                        vec4 distClip = uViewProjMat * vec4(displacedWorldPos, 1.0);
                        if (distClip.w > 0.01) {
                            vec2 distUV = (distClip.xy / distClip.w) * 0.5 + 0.5;
                            totalUVShift += (distUV - vTexCoord);
                        }

                        if (uChromatic == 1) {
                            totalChromatic += (abs(ripple) * 0.010 + blackholePull * 0.007) * alpha * uDistortionStrength;
                        }
                    }

                    if (length(totalUVShift) < 0.00005 && totalChromatic < 0.00005) {
                        fragColor = baseColor;
                        return;
                    }

                    vec2 uvR = vTexCoord + totalUVShift + vec2(totalChromatic, 0.0);
                    vec2 uvG = vTexCoord + totalUVShift;
                    vec2 uvB = vTexCoord + totalUVShift - vec2(totalChromatic, 0.0);

                    float r = texture(uColorTexture, clamp(uvR, 0.001, 0.999)).r;
                    float g = texture(uColorTexture, clamp(uvG, 0.001, 0.999)).g;
                    float b = texture(uColorTexture, clamp(uvB, 0.001, 0.999)).b;

                    fragColor = vec4(r, g, b, baseColor.a);
                }
                """;

            this.distortionProgram = this.createProgram(vert, frag);
        }
    }

    private int createProgram(String vert, String frag) {
        int v = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(v, vert);
        GL20.glCompileShader(v);

        int f = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(f, frag);
        GL20.glCompileShader(f);

        int p = GL20.glCreateProgram();
        GL20.glAttachShader(p, v);
        GL20.glAttachShader(p, f);
        GL20.glBindAttribLocation(p, 0, "Position");
        GL20.glBindAttribLocation(p, 1, "TexCoord");
        GL20.glLinkProgram(p);

        GL20.glDeleteShader(v);
        GL20.glDeleteShader(f);
        return p;
    }

    private void setUniform1i(String name, int val) {
        int loc = GL20.glGetUniformLocation(this.distortionProgram, name);
        if (loc != -1) {
            GL20.glUniform1i(loc, val);
        }
    }

    private void setUniform1f(String name, float val) {
        int loc = GL20.glGetUniformLocation(this.distortionProgram, name);
        if (loc != -1) {
            GL20.glUniform1f(loc, val);
        }
    }

    private void setUniform2f(String name, float x, float y) {
        int loc = GL20.glGetUniformLocation(this.distortionProgram, name);
        if (loc != -1) {
            GL20.glUniform2f(loc, x, y);
        }
    }

    private void setUniform3f(String name, float x, float y, float z) {
        int loc = GL20.glGetUniformLocation(this.distortionProgram, name);
        if (loc != -1) {
            GL20.glUniform3f(loc, x, y, z);
        }
    }

    private void setUniformMatrix4fv(String name, float[] mat) {
        int loc = GL20.glGetUniformLocation(this.distortionProgram, name);
        if (loc != -1) {
            FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
            buffer.put(mat).flip();
            GL20.glUniformMatrix4fv(loc, false, buffer);
        }
    }

    @Override
    public void onDisable() {
        this.circles.clear();
        if (this.distortionFbo != null) {
            this.distortionFbo.delete();
            this.distortionFbo = null;
        }
        if (this.distortionProgram != -1) {
            GL20.glDeleteProgram(this.distortionProgram);
            this.distortionProgram = -1;
        }
        if (this.distortionVao != -1) {
            GL30.glDeleteVertexArrays(this.distortionVao);
            this.distortionVao = -1;
        }
        if (this.distortionVbo != -1) {
            GL15.glDeleteBuffers(this.distortionVbo);
            this.distortionVbo = -1;
        }
    }
}
