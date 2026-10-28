package moscow.definevisuals.systems.modules.modules.visuals;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Generated;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.network.ReceivePacketEvent;
import moscow.definevisuals.systems.event.impl.render.PreHudRenderEvent;
import moscow.definevisuals.systems.setting.settings.BooleanSetting;
import moscow.definevisuals.utility.game.MessageUtility;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(name = "AutoWaypoints", category = ModuleCategory.VISUALS, desc = "Автоматически ставит метки на ивенты и сокровища")
public class AutoWaypoints extends BaseModule {
    private final Map<String, EventWaypoint> eventWaypoints = new HashMap<>();
    
    private final BooleanSetting eventWaypointsEnabled = new BooleanSetting(this, "Вейпоинты ивентов", "Автоматически ставить метки на ивенты из чата").enabled(true);
    private final BooleanSetting treasureWaypointsEnabled = new BooleanSetting(this, "Вейпоинты сокровищ", "Автоматически ставить метки на сокровища от компаса").enabled(true);
    
    private final Pattern coordPattern = Pattern.compile("Координаты:\\s*(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)", Pattern.CASE_INSENSITIVE);
    private final Pattern eventNamePattern = Pattern.compile("▍\\s*([^▍\\n]+)", Pattern.CASE_INSENSITIVE);
    private final Pattern compassPattern = Pattern.compile("(?:Найдено сокровище по координатам:|Стрелка компаса установлена на последний найденный регион по координатам:)\\s*(-?\\d+)\\s*,\\s*(-?\\d+)", Pattern.CASE_INSENSITIVE);
    private final Pattern eventCommandPattern = Pattern.compile("НАХОДИТСЯ НА КООРДИНАТАХ\\s+(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)", Pattern.CASE_INSENSITIVE);
    
    private final EventListener<ReceivePacketEvent> onReceivePacket = event -> {
        if (!this.isEnabled()) return;
        
        try {
            if (event.getPacket() instanceof GameMessageS2CPacket messagePacket) {
                Text message = messagePacket.content();
                String text = message.getString();
                
                // Check for treasure coordinates from compass (format: -1513, -872)
                if (treasureWaypointsEnabled.isEnabled()) {
                    Matcher compassMatcher = compassPattern.matcher(text);
                    if (compassMatcher.find()) {
                        double x = Double.parseDouble(compassMatcher.group(1));
                        double z = Double.parseDouble(compassMatcher.group(2));
                        double y = 70;
                        
                        String waypointName = "sokra";
                        addEventWaypoint(waypointName, x, y, z);
                    }
                }
                
                // Check for /event command output coordinates (format: НАХОДИТСЯ НА КООРДИНАТАХ 2062 66 -1811)
                if (eventWaypointsEnabled.isEnabled()) {
                    Matcher eventCommandMatcher = eventCommandPattern.matcher(text);
                    if (eventCommandMatcher.find()) {
                        double x = Double.parseDouble(eventCommandMatcher.group(1));
                        double y = Double.parseDouble(eventCommandMatcher.group(2));
                        double z = Double.parseDouble(eventCommandMatcher.group(3));

                        String waypointName = "event";
                        addEventWaypoint(waypointName, x, y, z);
                    }
                }

                // Check if this is an event message (contains ▬▬▬) for notification only
                if (eventWaypointsEnabled.isEnabled() && text.contains("▬▬▬")) {
                    // Extract event name for notification
                    Matcher nameMatcher = eventNamePattern.matcher(text);
                    if (nameMatcher.find()) {
                        String potentialName = nameMatcher.group(1).trim();
                        // Skip empty lines and "Тип:" lines
                        if (!potentialName.isEmpty() && !potentialName.startsWith("Тип:")) {
                            // Could show notification here if needed
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    };
    
    private final EventListener<PreHudRenderEvent> onHudRender = event -> {
        if (!this.isEnabled()) return;
        
        try {
            long currentTime = System.currentTimeMillis();
            
            eventWaypoints.entrySet().removeIf(entry -> {
                EventWaypoint wp = entry.getValue();
                if (currentTime - wp.creationTime > 600000L) {
                    return true;
                }
                return false;
            });
        } catch (Exception e) {
        }
    };
    
    private void addEventWaypoint(String name, double x, double y, double z) {
        try {
            // Add to WayPointsManager
            DefineVisuals.getInstance().getWayPointsManager().add(name, (int)x, (int)y, (int)z);
            
            // Debug notification
            MessageUtility.info(Text.of("Added waypoint: " + name + " at " + (int)x + ", " + (int)y + ", " + (int)z));
            
            // Also track in local map for cleanup
            if (eventWaypoints.containsKey(name)) {
                EventWaypoint wp = eventWaypoints.get(name);
                wp.pos = new Vec3d(x, y, z);
                wp.creationTime = System.currentTimeMillis();
            } else {
                eventWaypoints.put(name, new EventWaypoint(name, new Vec3d(x, y, z), System.currentTimeMillis()));
            }
        } catch (Exception e) {
            MessageUtility.error(Text.of("Failed to add waypoint: " + e.getMessage()));
        }
    }
    
    private static class EventWaypoint {
        public String name;
        public Vec3d pos;
        public long creationTime;
        
        public EventWaypoint(String name, Vec3d pos, long creationTime) {
            this.name = name;
            this.pos = pos;
            this.creationTime = creationTime;
        }
    }

    @Generated
    public BooleanSetting getEventWaypointsEnabled() {
        return eventWaypointsEnabled;
    }

    @Generated
    public BooleanSetting getTreasureWaypointsEnabled() {
        return treasureWaypointsEnabled;
    }
}
