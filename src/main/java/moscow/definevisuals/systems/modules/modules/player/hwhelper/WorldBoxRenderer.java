package moscow.definevisuals.systems.modules.modules.player.hwhelper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.List;

public final class WorldBoxRenderer {
    private WorldBoxRenderer() {
    }

    public static void draw(net.minecraft.client.util.math.MatrixStack matrices, Vec3d camera, List<Box> boxes, int red, int green, int blue, int lineAlpha) {
        if (boxes != null && !boxes.isEmpty()) {
            matrices.push();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.depthMask(false);
            RenderSystem.enableDepthTest();
            RenderSystem.setShader(net.minecraft.client.gl.ShaderProgramKeys.POSITION_COLOR);
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            BufferBuilder tessellator = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            for (Box box : boxes) {
                addQuads(tessellator, matrix, offset(box, camera), red, green, blue, 35);
            }

            moscow.definevisuals.utility.render.RenderUtility.buildBuffer(tessellator);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            RenderSystem.lineWidth(2.4F);
            BufferBuilder lines = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);

            for (Box box : boxes) {
                addLines(lines, matrix, offset(box, camera), red, green, blue, lineAlpha);
            }

            moscow.definevisuals.utility.render.RenderUtility.buildBuffer(lines);
            RenderSystem.lineWidth(1.0F);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            matrices.pop();
        }
    }

    public static void drawConnected(net.minecraft.client.util.math.MatrixStack matrices, Vec3d camera, List<Box> boxes, int red, int green, int blue, int lineAlpha) {
        VoxelMesh mesh = VoxelMesh.of(boxes);
        if (!mesh.quads.isEmpty() || !mesh.lines.isEmpty()) {
            matrices.push();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.depthMask(false);
            RenderSystem.enableDepthTest();
            RenderSystem.setShader(net.minecraft.client.gl.ShaderProgramKeys.POSITION_COLOR);
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            net.minecraft.client.render.Tessellator tessellator = net.minecraft.client.render.Tessellator.getInstance();
            if (!mesh.quads.isEmpty()) {
                BufferBuilder fill = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

                for (VoxelMesh.Quad quad : mesh.quads) {
                    quad(fill, matrix, red, green, blue, 35, quad.x1() - (float)camera.x, quad.y1() - (float)camera.y, quad.z1() - (float)camera.z, quad.x2() - (float)camera.x, quad.y2() - (float)camera.y, quad.z2() - (float)camera.z, quad.x3() - (float)camera.x, quad.y3() - (float)camera.y, quad.z3() - (float)camera.z, quad.x4() - (float)camera.x, quad.y4() - (float)camera.y, quad.z4() - (float)camera.z);
                }

                moscow.definevisuals.utility.render.RenderUtility.buildBuffer(fill);
            }

            if (!mesh.lines.isEmpty()) {
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                RenderSystem.lineWidth(2.4F);
                BufferBuilder lines = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);

                for (VoxelMesh.Line edge : mesh.lines) {
                    line(lines, matrix, red, green, blue, lineAlpha, edge.x1() - (float)camera.x, edge.y1() - (float)camera.y, edge.z1() - (float)camera.z, edge.x2() - (float)camera.x, edge.y2() - (float)camera.y, edge.z2() - (float)camera.z);
                }

                moscow.definevisuals.utility.render.RenderUtility.buildBuffer(lines);
                RenderSystem.lineWidth(1.0F);
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
            }

            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            matrices.pop();
        }
    }

    private static Box offset(Box box, Vec3d camera) {
        return box.offset(-camera.x, -camera.y, -camera.z);
    }

    private static void addQuads(BufferBuilder buffer, Matrix4f matrix, Box box, int r, int g, int b, int a) {
        float x1 = (float)box.minX;
        float y1 = (float)box.minY;
        float z1 = (float)box.minZ;
        float x2 = (float)box.maxX;
        float y2 = (float)box.maxY;
        float z2 = (float)box.maxZ;
        quad(buffer, matrix, r, g, b, a, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2);
        quad(buffer, matrix, r, g, b, a, x1, y2, z1, x2, y2, z1, x2, y2, z2, x1, y2, z2);
        quad(buffer, matrix, r, g, b, a, x1, y1, z1, x2, y1, z1, x2, y2, z1, x1, y2, z1);
        quad(buffer, matrix, r, g, b, a, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2);
        quad(buffer, matrix, r, g, b, a, x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1);
        quad(buffer, matrix, r, g, b, a, x2, y1, z1, x2, y1, z2, x2, y2, z2, x2, y2, z1);
    }

    private static void addLines(BufferBuilder buffer, Matrix4f matrix, Box box, int r, int g, int b, int a) {
        float x1 = (float)box.minX;
        float y1 = (float)box.minY;
        float z1 = (float)box.minZ;
        float x2 = (float)box.maxX;
        float y2 = (float)box.maxY;
        float z2 = (float)box.maxZ;
        line(buffer, matrix, r, g, b, a, x1, y1, z1, x1, y2, z1);
        line(buffer, matrix, r, g, b, a, x2, y1, z1, x2, y2, z1);
        line(buffer, matrix, r, g, b, a, x1, y1, z2, x1, y2, z2);
        line(buffer, matrix, r, g, b, a, x2, y1, z2, x2, y2, z2);
        line(buffer, matrix, r, g, b, a, x1, y1, z1, x2, y1, z1);
        line(buffer, matrix, r, g, b, a, x1, y1, z2, x2, y1, z2);
        line(buffer, matrix, r, g, b, a, x1, y2, z1, x2, y2, z1);
        line(buffer, matrix, r, g, b, a, x1, y2, z2, x2, y2, z2);
        line(buffer, matrix, r, g, b, a, x1, y1, z1, x1, y1, z2);
        line(buffer, matrix, r, g, b, a, x2, y1, z1, x2, y1, z2);
        line(buffer, matrix, r, g, b, a, x1, y2, z1, x1, y2, z2);
        line(buffer, matrix, r, g, b, a, x2, y2, z1, x2, y2, z2);
    }

    private static void quad(BufferBuilder buffer, Matrix4f matrix, int r, int g, int b, int a, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x3, y3, z3).color(r, g, b, a);
        buffer.vertex(matrix, x4, y4, z4).color(r, g, b, a);
    }

    private static void line(BufferBuilder buffer, Matrix4f matrix, int r, int g, int b, int a, float x1, float y1, float z1, float x2, float y2, float z2) {
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
    }
}
