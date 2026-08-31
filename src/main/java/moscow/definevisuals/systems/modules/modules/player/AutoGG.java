package moscow.definevisuals.systems.modules.modules.player;

import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.game.AttackEvent;
import moscow.definevisuals.systems.event.impl.game.EntityDeathEvent;
import moscow.definevisuals.systems.event.impl.network.ReceivePacketEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ModuleInfo(name = "AutoGG", category = ModuleCategory.PLAYER, desc = "Автоматически отправляет GG после убийства игрока", enabledByDefault = false)
public class AutoGG extends BaseModule {
    private static final long WINDOW_MS = 5000L;
    private static final String[] KILL_MESSAGES = new String[]{
        "респект, хороший бой получился",
        "gg bro!",
        "а ты не плох",
        "получил бесплатный билет на спавн",
        "спасибо за честный бой!",
        "было непросто, хороший бой",
        "сори, меня топка забустила",
        "спасибо за файт",
        "достойный соперник, до встречи)",
        "удачи, было неплохо"
    };
    
    private final Map<Integer, Long> lastHit = new ConcurrentHashMap<>();
    private int messageIndex = 0;
    
    private final EventListener<AttackEvent> onAttack = event -> {
        if (mc.player == null || mc.world == null) {
            return;
        }
        
        Entity target = event.getEntity();
        if (target instanceof LivingEntity) {
            lastHit.put(target.getId(), System.currentTimeMillis());
        }
    };
    
    private final EventListener<ReceivePacketEvent> onPacket = event -> {
        if (mc.player == null || mc.world == null || !isEnabled()) {
            return;
        }
        
        if (event.getPacket() instanceof EntityStatusS2CPacket packet) {
            if (packet.getStatus() == 3) { // Entity death status
                Entity entity = packet.getEntity(mc.world);
                if (entity instanceof PlayerEntity && entity != mc.player) {
                    if (wasRecentlyHitByMe(entity)) {
                        String victim = ((PlayerEntity) entity).getName().getString();
                        String msg = makeKillMessage(victim);
                        mc.player.networkHandler.sendChatMessage(msg);
                    }
                }
            }
        }
    };
    
    private boolean wasRecentlyHitByMe(Entity entity) {
        Long t = lastHit.get(entity.getId());
        if (t == null) {
            return false;
        }
        
        long now = System.currentTimeMillis();
        if (now - t <= WINDOW_MS) {
            return true;
        } else {
            lastHit.remove(entity.getId());
            return false;
        }
    }
    
    private String makeKillMessage(String victim) {
        String phrase = KILL_MESSAGES[messageIndex];
        messageIndex = (messageIndex + 1) % KILL_MESSAGES.length;
        return "! " + victim + " " + phrase;
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        lastHit.clear();
        messageIndex = 0;
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        lastHit.clear();
    }
}
