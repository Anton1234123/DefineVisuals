package moscow.definevisuals.systems.commands.commands.hubswap;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import moscow.definevisuals.utility.interfaces.IMinecraft;

public final class TpMan implements IMinecraft {
    private static final String TYPE_KEY = "advancedserverselecter:server-type";
    private static final String SERVER_KEY = "advancedserverselecter:server";

    private State state = State.IDLE;
    private int num;
    private String cat;
    private int catSlot;
    private String key;
    private int ticks;
    private int retryCount;

    public TpMan() {
        this.reset();
    }

    public boolean isBusy() {
        return this.state != State.IDLE;
    }

    public String start(int number) {
        if (!AnReg.isValid(number)) {
            return "Неверный номер анархии #" + number + " (доступно 1-" + AnReg.getMaxNumber() + ")";
        }
        if (mc.player == null || mc.world == null) {
            return "Вы не находитесь в игре";
        }

        // Cancel previous attempt if any
        this.reset();

        this.num = number;
        this.cat = AnReg.catOf(number);
        this.catSlot = AnReg.catSlotOf(number);
        this.key = AnReg.keyOf(number);
        this.ticks = 0;
        this.retryCount = 0;

        // Send /hub command first
        this.state = State.WAITING_HUB;
        mc.player.networkHandler.sendChatCommand("hub");
        return null;
    }

    public void onMsg(String text) {
        if (text == null || this.state == State.IDLE) return;
        String lower = text.toLowerCase();
        if (lower.contains("подключение к hub") || lower.contains("перенаправление") || lower.contains("добро пожаловать") || lower.contains("lobby") || lower.contains("hub")) {
            if (this.state == State.WAITING_HUB) {
                this.state = State.WAITING_MENU;
                this.ticks = 0;
                if (mc.player != null) {
                    mc.player.networkHandler.sendChatCommand("lite");
                }
            }
        }
    }

    public void tick() {
        if (this.state == State.IDLE) return;
        if (mc.player == null || mc.world == null) {
            this.reset();
            return;
        }

        this.ticks++;

        // Timeout safety (3 seconds)
        if (this.ticks > 60) {
            this.err("Время ожидания истекло для " + AnReg.name(this.num));
            this.reset();
            return;
        }

        switch (this.state) {
            case WAITING_HUB -> {
                // Wait 4 ticks (~200ms) after /hub, then open /lite
                if (this.ticks >= 4) {
                    this.state = State.WAITING_MENU;
                    mc.player.networkHandler.sendChatCommand("lite");
                    this.ticks = 0;
                }
            }
            case WAITING_MENU -> {
                Screen screen = mc.currentScreen;
                if (screen instanceof HandledScreen<?> handledScreen) {
                    // 1. First check if the server item for 'num' is ALREADY visible in the current menu:
                    if (this.clickServerSlot(handledScreen)) {
                        this.state = State.IDLE;
                        this.ticks = 0;
                        if (mc.player != null) {
                            mc.player.sendMessage(Text.literal("§a[Define] §fПодключение к §e" + AnReg.name(this.num) + "§f..."), false);
                        }
                        return;
                    }

                    // 2. If not visible yet, click the target category tab (slot 0: Solo, 1: Duo, 2: Trio, 3: Clans)
                    if (this.clickCategoryTab(handledScreen)) {
                        this.state = State.WAITING_SERVER_SLOT;
                        this.ticks = 0;
                    }
                } else if (this.ticks > 8 && this.ticks % 8 == 0 && this.retryCount < 4) {
                    this.retryCount++;
                    mc.player.networkHandler.sendChatCommand("lite");
                }
            }
            case WAITING_SERVER_SLOT -> {
                Screen screen = mc.currentScreen;
                if (screen instanceof HandledScreen<?> handledScreen) {
                    if (this.clickServerSlot(handledScreen)) {
                        this.state = State.IDLE;
                        this.ticks = 0;
                        if (mc.player != null) {
                            mc.player.sendMessage(Text.literal("§a[Define] §fПодключение к §e" + AnReg.name(this.num) + "§f..."), false);
                        }
                    }
                }
            }
        }
    }

    private boolean clickCategoryTab(HandledScreen<?> handledScreen) {
        ScreenHandler handler = handledScreen.getScreenHandler();
        int containerSlots = handler.slots.size() - 36;
        if (containerSlots <= 0) return false;

        // Try direct slot index first (slots 0-3 on top row)
        if (this.catSlot < containerSlots && this.catSlot < 9) {
            Slot slot = handler.getSlot(this.catSlot);
            if (!slot.getStack().isEmpty()) {
                mc.interactionManager.clickSlot(
                        handler.syncId,
                        slot.getIndex(),
                        0,
                        SlotActionType.PICKUP,
                        mc.player
                );
                return true;
            }
        }

        // Search top row slots 0-8 for matching category
        for (int i = 0; i < Math.min(9, containerSlots); ++i) {
            Slot slot = handler.getSlot(i);
            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;

            String nbtVal = readNbt(stack, TYPE_KEY);
            if (this.cat.equalsIgnoreCase(nbtVal)) {
                mc.interactionManager.clickSlot(
                        handler.syncId,
                        slot.getIndex(),
                        0,
                        SlotActionType.PICKUP,
                        mc.player
                );
                return true;
            }

            String stackName = stack.getName().getString().toLowerCase();
            boolean match = false;
            if (this.cat.equalsIgnoreCase("solo") && (stackName.contains("соло") || stackName.contains("solo"))) match = true;
            else if (this.cat.equalsIgnoreCase("duo") && (stackName.contains("дуо") || stackName.contains("duo"))) match = true;
            else if (this.cat.equalsIgnoreCase("trio") && (stackName.contains("трио") || stackName.contains("trio"))) match = true;
            else if (this.cat.equalsIgnoreCase("clans") && (stackName.contains("клан") || stackName.contains("clan"))) match = true;

            if (match) {
                mc.interactionManager.clickSlot(
                        handler.syncId,
                        slot.getIndex(),
                        0,
                        SlotActionType.PICKUP,
                        mc.player
                );
                return true;
            }
        }
        return false;
    }

    private boolean clickServerSlot(HandledScreen<?> handledScreen) {
        ScreenHandler handler = handledScreen.getScreenHandler();
        int containerSlots = handler.slots.size() - 36;
        if (containerSlots <= 0) return false;

        // Anarchy items are located in slots 9 through containerSlots
        for (int i = 9; i < containerSlots; ++i) {
            Slot slot = handler.getSlot(i);
            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;

            boolean match = false;

            // 1. Check server NBT key
            String nbtVal = readNbt(stack, SERVER_KEY);
            if (this.key.equalsIgnoreCase(nbtVal)) {
                match = true;
            }

            // 2. Check item name
            if (!match) {
                String stackName = stack.getName().getString().toLowerCase();
                if (stackName.contains("#" + this.num) || stackName.contains("# " + this.num) || stackName.endsWith(" " + this.num) || stackName.equals(String.valueOf(this.num))) {
                    match = true;
                }
            }

            // 3. Check stack count
            if (!match && stack.getCount() == this.num) {
                match = true;
            }

            if (match) {
                mc.interactionManager.clickSlot(
                        handler.syncId,
                        slot.getIndex(),
                        0,
                        SlotActionType.PICKUP,
                        mc.player
                );
                return true;
            }
        }
        return false;
    }

    private static String readNbt(ItemStack stack, String key) {
        if (stack == null || stack.isEmpty()) return null;
        NbtComponent custom = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (custom == null) return null;

        NbtCompound root = custom.copyNbt();
        if (root != null) {
            if (root.contains("PublicBukkitValues")) {
                NbtCompound values = root.getCompound("PublicBukkitValues");
                if (values != null && values.contains(key)) {
                    String val = values.getString(key);
                    if (!val.isEmpty()) return val;
                }
            }
            if (root.contains(key)) {
                String val = root.getString(key);
                if (!val.isEmpty()) return val;
            }
        }
        return null;
    }

    private void err(String message) {
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal("§c[Define] §f" + message), false);
        }
    }

    private void reset() {
        this.state = State.IDLE;
        this.ticks = 0;
        this.retryCount = 0;
    }

    private enum State {
        IDLE,
        WAITING_HUB,
        WAITING_MENU,
        WAITING_SERVER_SLOT
    }
}
