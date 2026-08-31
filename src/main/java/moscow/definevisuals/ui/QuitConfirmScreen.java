package moscow.definevisuals.ui;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.CustomScreen;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.objects.MouseButton;
import moscow.definevisuals.framework.msdf.Font;
import moscow.definevisuals.framework.msdf.Fonts;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.framework.objects.gradient.impl.VerticalGradient;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class QuitConfirmScreen extends CustomScreen implements IMinecraft {
    private final Screen parent;
    
    public QuitConfirmScreen(Screen parent) {
        this.parent = parent;
    }
    
    @Override
    public void render(UIContext context) {
        Font titleFont = Fonts.ROUND_BOLD.getFont(24.0f);
        Font textFont = Fonts.REGULAR.getFont(14.0f);
        
        float width = 300.0f;
        float height = 250.0f;
        float x = (float) this.width / 2.0f - width / 2.0f;
        float y = (float) this.height / 2.0f - height / 2.0f;
        
        // Background
        context.drawRoundedRect(x, y, width, height, BorderRadius.all(12.0f), 
            new VerticalGradient(new ColorRGBA(30.0f, 30.0f, 40.0f, 0.95f), new ColorRGBA(20.0f, 20.0f, 30.0f, 0.95f)));
        
        // Title
        context.drawCenteredText(titleFont, "§c§lПредупреждение!", (float) this.width / 2.0f, y + 25.0f, ColorRGBA.WHITE);
        
        // Risk info
        RiskInfo riskInfo = collectRiskInfo();
        float infoY = y + 60.0f;
        
        context.drawText(textFont, "§fВ вашем инвентаре есть:", x + 20.0f, infoY, ColorRGBA.WHITE);
        context.drawText(textFont, "§eШалкеры: §f" + riskInfo.shulkerBoxes, x + 20.0f, infoY + 25.0f, ColorRGBA.WHITE);
        context.drawText(textFont, "§dДраконьи яйца: §f" + riskInfo.dragonEggs, x + 20.0f, infoY + 50.0f, ColorRGBA.WHITE);
        context.drawText(textFont, "§bБоевые фрагменты: §f" + riskInfo.battleFragments, x + 20.0f, infoY + 75.0f, ColorRGBA.WHITE);
        
        context.drawCenteredText(textFont, "§cВы уверены, что хотите выйти?", (float) this.width / 2.0f, infoY + 110.0f, ColorRGBA.WHITE);
        
        // Buttons
        float buttonWidth = 100.0f;
        float buttonHeight = 30.0f;
        float buttonY = y + height - 50.0f;
        float cancelX = x + 20.0f;
        float quitX = x + width - buttonWidth - 20.0f;
        
        // Cancel button
        ColorRGBA cancelColor = isHovered(cancelX, buttonY, buttonWidth, buttonHeight, context) 
            ? new ColorRGBA(100.0f, 100.0f, 120.0f) 
            : new ColorRGBA(70.0f, 70.0f, 90.0f);
        context.drawRoundedRect(cancelX, buttonY, buttonWidth, buttonHeight, BorderRadius.all(8.0f), cancelColor);
        context.drawCenteredText(textFont, "Отмена", cancelX + buttonWidth / 2.0f, buttonY + buttonHeight / 2.0f - 5.0f, ColorRGBA.WHITE);
        
        // Quit button
        ColorRGBA quitColor = isHovered(quitX, buttonY, buttonWidth, buttonHeight, context) 
            ? new ColorRGBA(200.0f, 60.0f, 60.0f) 
            : new ColorRGBA(180.0f, 50.0f, 50.0f);
        context.drawRoundedRect(quitX, buttonY, buttonWidth, buttonHeight, BorderRadius.all(8.0f), quitColor);
        context.drawCenteredText(textFont, "Выйти", quitX + buttonWidth / 2.0f, buttonY + buttonHeight / 2.0f - 5.0f, ColorRGBA.WHITE);
    }
    
    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        float width = 300.0f;
        float height = 250.0f;
        float x = (float) this.width / 2.0f - width / 2.0f;
        float y = (float) this.height / 2.0f - height / 2.0f;
        
        float buttonWidth = 100.0f;
        float buttonHeight = 30.0f;
        float buttonY = y + height - 50.0f;
        float cancelX = x + 20.0f;
        float quitX = x + width - buttonWidth - 20.0f;
        
        // Cancel button
        if (isHovered(cancelX, buttonY, buttonWidth, buttonHeight, mouseX, mouseY)) {
            mc.setScreen(parent);
            return;
        }
        
        // Quit button
        if (isHovered(quitX, buttonY, buttonWidth, buttonHeight, mouseX, mouseY)) {
            mc.world.disconnect();
            mc.disconnect();
            return;
        }
    }
    
    private boolean isHovered(float x, float y, float width, float height, UIContext context) {
        return context.getMouseX() >= x && context.getMouseX() <= x + width &&
               context.getMouseY() >= y && context.getMouseY() <= y + height;
    }
    
    private boolean isHovered(float x, float y, float width, float height, double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width &&
               mouseY >= y && mouseY <= y + height;
    }
    
    private RiskInfo collectRiskInfo() {
        if (mc.player == null) {
            return new RiskInfo(0, 0, 0);
        }
        
        int shulkerBoxes = 0;
        int dragonEggs = 0;
        int battleFragments = 0;
        
        for (ItemStack stack : mc.player.getInventory().main) {
            if (isShulkerBox(stack)) {
                shulkerBoxes += stack.getCount();
            }
            if (stack.getItem() == Items.DRAGON_EGG) {
                dragonEggs += stack.getCount();
            }
            if (isBattleFragment(stack)) {
                battleFragments += stack.getCount();
            }
        }
        
        return new RiskInfo(shulkerBoxes, dragonEggs, battleFragments);
    }
    
    private boolean isShulkerBox(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (stack.getItem() instanceof BlockItem blockItem) {
            return blockItem.getBlock() instanceof ShulkerBoxBlock;
        }
        return false;
    }
    
    private boolean isBattleFragment(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        Identifier id = Registries.ITEM.getId(stack.getItem());
        if (id == null) return false;
        String path = id.getPath();
        return "prismarine_crystals".equals(path) || "battle_fragment".equals(path);
    }
    
    private static class RiskInfo {
        final int shulkerBoxes;
        final int dragonEggs;
        final int battleFragments;
        
        RiskInfo(int shulkerBoxes, int dragonEggs, int battleFragments) {
            this.shulkerBoxes = shulkerBoxes;
            this.dragonEggs = dragonEggs;
            this.battleFragments = battleFragments;
        }
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        mc.setScreen(parent);
        return false;
    }
}
