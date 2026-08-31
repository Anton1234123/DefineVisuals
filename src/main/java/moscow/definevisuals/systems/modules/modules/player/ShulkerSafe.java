package moscow.definevisuals.systems.modules.modules.player;

import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.network.SendPacketEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Locale;
import java.util.Set;

@ModuleInfo(name = "ShulkerSafe", category = ModuleCategory.PLAYER, desc = "Защищает от выхода с шалкерами и ценными предметами на HW сервере", enabledByDefault = false)
public class ShulkerSafe extends BaseModule {
    private static final Set<String> LEAVE_COMMANDS = Set.of("hub", "lobby", "рги", "дщиин");
    private static final long CONFIRMATION_TIMEOUT_MS = 10000L;
    private boolean confirmationOpen = false;
    private String confirmedCommand = null;
    private long confirmationStartTime = 0;
    
    private final EventListener<SendPacketEvent> onSendPacket = event -> {
        if (mc.player == null || mc.world == null || !isEnabled()) {
            return;
        }
        
        if (event.getPacket() instanceof CommandExecutionC2SPacket packet) {
            String command = packet.command();
            if (shouldInterceptCommand(command)) {
                event.cancel();
                mc.execute(() -> openCommandConfirm(command));
            }
        }
    };
    
    public boolean hasOpenConfirmation() {
        return confirmationOpen;
    }
    
    public boolean shouldInterceptCommand(String command) {
        if (confirmationOpen && isConfirmationExpired()) {
            closeConfirm();
        }
        
        if (consumeConfirmedCommand(command)) {
            return false;
        }
        return isLeaveCommand(command) && shouldProtectExit();
    }
    
    public void openCommandConfirm(String command) {
        confirmationOpen = true;
        confirmationStartTime = System.currentTimeMillis();
        RiskInfo riskInfo = collectRiskInfo();
        
        String message = "§c§lПредупреждение!\n" +
                        "§fВ вашем инвентаре есть ценные предметы:\n" +
                        "§eШалкеры: §f" + riskInfo.shulkerBoxes + "\n" +
                        "§dДраконьи яйца: §f" + riskInfo.dragonEggs + "\n" +
                        "§bБоевые фрагменты: §f" + riskInfo.battleFragments + "\n\n" +
                        "§cЧтобы выйти, напишите §e/" + normalizeCommand(command) + " §cеще раз в течение §e10 секунд";
        
        mc.player.sendMessage(Text.of(message), false);
        
        confirmedCommand = normalizeCommand(command);
    }
    
    public void closeConfirm() {
        confirmationOpen = false;
        confirmedCommand = null;
        confirmationStartTime = 0;
    }
    
    private boolean isConfirmationExpired() {
        return System.currentTimeMillis() - confirmationStartTime > CONFIRMATION_TIMEOUT_MS;
    }
    
    public void sendConfirmedCommand(String command) {
        closeConfirm();
        mc.player.networkHandler.sendChatCommand(normalizeCommand(command));
    }
    
    private boolean shouldProtectExit() {
        if (!isHolyWorld()) return false;
        RiskInfo riskInfo = collectRiskInfo();
        return riskInfo.shulkerBoxes >= 2 || riskInfo.dragonEggs > 0 || riskInfo.battleFragments > 0;
    }
    
    private boolean isLeaveCommand(String command) {
        if (command == null) return false;
        return LEAVE_COMMANDS.contains(normalizeCommand(command).toLowerCase(Locale.ROOT));
    }
    
    private boolean consumeConfirmedCommand(String command) {
        String normalized = normalizeCommand(command);
        if (confirmedCommand != null && confirmedCommand.equalsIgnoreCase(normalized)) {
            confirmedCommand = null;
            return true;
        }
        return false;
    }
    
    private String normalizeCommand(String command) {
        if (command == null) return "";
        String normalized = command.trim();
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }
    
    private boolean isHolyWorld() {
        if (mc.getCurrentServerEntry() == null) return false;
        String address = mc.getCurrentServerEntry().address.toLowerCase();
        return address.contains("holyworld");
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
        Item item = stack.getItem();
        if (item instanceof BlockItem blockItem) {
            return blockItem.getBlock() instanceof ShulkerBoxBlock;
        }
        return false;
    }
    
    private boolean isBattleFragment(ItemStack stack) {
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
        
        boolean dangerous() {
            return shulkerBoxes > 0 || dragonEggs > 0 || battleFragments > 0;
        }
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        closeConfirm();
    }
}
