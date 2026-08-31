package moscow.definevisuals.systems.modules.modules.player.hwhelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.util.Locale;

public final class TimerHud {
    private static final int BG = 2013265920;

    private TimerHud() {
    }

    public static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null && client.player != null && client.textRenderer != null && client.gameRenderer != null) {
            TextRenderer font = client.textRenderer;
            long now = System.currentTimeMillis();

            for (TimerTracker.ActiveTimer timer : TimerTracker.active()) {
                Vec3d world = timer.labelPos();
                Vec3d screen = project(client, world);
                if (screen != null) {
                    float scale = Math.max(1.0f - (float)(client.player.squaredDistanceTo(world) / 400.0), 0.5f);
                    drawTag(context, font, timer.type.icon().getDefaultStack(), formatTime(timer.remainingSeconds(now)), timer.progress(now), (float)screen.x, (float)screen.y, scale);
                }
            }
        }
    }

    private static Vec3d project(MinecraftClient client, Vec3d world) {
        if (client.gameRenderer != null && client.gameRenderer.getCamera() != null) {
            Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
            double dx = world.x - cameraPos.x;
            double dy = world.y - cameraPos.y;
            double dz = world.z - cameraPos.z;
            
            // Simple distance check
            double distSq = dx * dx + dy * dy + dz * dz;
            if (distSq > 2304.0) { // 48 blocks squared
                return null;
            }
            
            // Check if in front of camera
            if (dz > 0) {
                return null;
            }
            
            // Project to screen (simplified)
            double fov = 70.0;
            double aspect = (double)client.getWindow().getScaledWidth() / (double)client.getWindow().getScaledHeight();
            double halfHeight = -dz * Math.tan(Math.toRadians(fov * 0.5));
            double halfWidth = halfHeight * aspect;
            
            if (halfWidth <= 1.0E-4 || halfHeight <= 1.0E-4) {
                return null;
            }
            
            float screenX = (float)((double)client.getWindow().getScaledWidth() * 0.5 + dx / halfWidth * (double)client.getWindow().getScaledWidth() * 0.5);
            float screenY = (float)((double)client.getWindow().getScaledHeight() * 0.5 - dy / halfHeight * (double)client.getWindow().getScaledHeight() * 0.5);
            
            if (Float.isFinite(screenX) && Float.isFinite(screenY)) {
                if (screenX >= -80.0f && screenY >= -80.0f && screenX <= client.getWindow().getScaledWidth() + 80.0f && screenY <= client.getWindow().getScaledHeight() + 80.0f) {
                    return new Vec3d(screenX, screenY, 0);
                }
            }
        }
        return null;
    }

    private static void drawTag(DrawContext context, TextRenderer font, ItemStack icon, String time, double progress, float sx, float sy, float scale) {
        int textWidth = font.getWidth(time);
        int textBoxWidth = textWidth + 4;
        int totalWidth = 14 + textBoxWidth;
        
        context.getMatrices().push();
        context.getMatrices().translate(sx, sy, 0.0f);
        context.getMatrices().scale(scale, scale, 1.0f);
        context.getMatrices().translate(-(float)totalWidth / 2.0f, -6.0f, 0.0f);
        
        context.fill(0, 0, 12, 12, BG);
        context.fill(14, 0, totalWidth, 12, BG);
        
        context.getMatrices().push();
        context.getMatrices().scale(1.5f, 1.5f, 1.0f);
        context.getMatrices().translate(0.55f, 0.55f, 0.0f);
        context.drawItem(icon, 0, 0);
        context.getMatrices().pop();
        
        context.drawText(font, time, 14 + (textBoxWidth - textWidth) / 2, 2, -1, true);
        
        int fill = Math.max(1, Math.round((float)totalWidth * (float)progress));
        context.fill(0, 12, fill, 14, -65536);
        
        context.getMatrices().pop();
    }

    private static String formatTime(double seconds) {
        return String.format(Locale.US, "%.1f", Math.max(0.0, seconds));
    }
}
