package moscow.definevisuals.systems.event.impl.render;

import moscow.definevisuals.systems.event.Event;
import net.minecraft.client.network.PlayerListEntry;

import java.util.List;

public class PlayerListRenderEvent extends Event {
   private int maxPlayers;
   private int maxColumns;
   private final List<PlayerListEntry> entries;

   public PlayerListRenderEvent(List<PlayerListEntry> entries) {
      this.entries = entries;
      this.maxPlayers = 80; // Default Minecraft value
      this.maxColumns = 3; // Default Minecraft value
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public void setMaxPlayers(int maxPlayers) {
      this.maxPlayers = maxPlayers;
   }

   public int getMaxColumns() {
      return this.maxColumns;
   }

   public void setMaxColumns(int maxColumns) {
      this.maxColumns = maxColumns;
   }

   public List<PlayerListEntry> getEntries() {
      return this.entries;
   }
}
