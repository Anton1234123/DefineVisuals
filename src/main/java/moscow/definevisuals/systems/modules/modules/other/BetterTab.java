package moscow.definevisuals.systems.modules.modules.other;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import moscow.definevisuals.mixin.accessors.PlayerListHudAccessor;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.BooleanSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

@ModuleInfo(name = "Better Tab", category = ModuleCategory.OTHER, desc = "Показывает всех игроков в табе")
public class BetterTab extends BaseModule {
   public final BooleanSetting showHeads = new BooleanSetting(this, "Головы игроков", "Показывать головы игроков в табе");
   public final BooleanSetting showAllPlayers = new BooleanSetting(this, "Все игроки", "Показывать всех игроков на сервере");
   private static final Comparator<PlayerListEntry> ENTRY_ORDERING = Comparator.comparingInt((PlayerListEntry entry) -> -entry.getLatency()).thenComparingInt((PlayerListEntry entry) -> entry.getGameMode() == GameMode.SPECTATOR ? 1 : 0).thenComparing((PlayerListEntry entry) -> entry.getScoreboardTeam() != null ? entry.getScoreboardTeam().getName() : "").thenComparing((PlayerListEntry entry) -> entry.getProfile().getName(), String::compareToIgnoreCase);

   public BetterTab() {
      this.showHeads.setEnabled(true);
      this.showAllPlayers.setEnabled(true);
   }

   public void renderPlayerList(DrawContext context, int scaledWidth, int scaledHeight, Scoreboard scoreboard, ScoreboardObjective objective, PlayerListHud playerListHud) {
      if (mc.player != null) {
         ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
         if (networkHandler != null) {
            List<PlayerListEntry> playerList = new ArrayList(networkHandler.getPlayerList());
            playerList.sort(ENTRY_ORDERING);
            if (!playerList.isEmpty()) {
               int maxPlayers = playerList.size();
               int entryHeight = 9;
               PlayerListHudAccessor accessor = (PlayerListHudAccessor)playerListHud;
               Text header = accessor.getHeader();
               Text footer = accessor.getFooter();
               List<OrderedText> headerLines = null;
               List<OrderedText> footerLines = null;
               int headerTotalHeight = 0;
               int footerTotalHeight = 0;
               int maxTextWidth = 0;
               if (header != null) {
                  headerLines = mc.textRenderer.wrapLines(header, scaledWidth - 50);
                  headerTotalHeight = headerLines.size() * 9;

                  for(OrderedText line : headerLines) {
                     maxTextWidth = Math.max(maxTextWidth, mc.textRenderer.getWidth(line));
                  }
               }

               if (footer != null) {
                  footerLines = mc.textRenderer.wrapLines(footer, scaledWidth - 50);
                  footerTotalHeight = footerLines.size() * 9;

                  for(OrderedText line : footerLines) {
                     maxTextWidth = Math.max(maxTextWidth, mc.textRenderer.getWidth(line));
                  }
               }

               int availableHeight = scaledHeight - 20;
               if (headerLines != null) {
                  availableHeight -= headerTotalHeight + 2;
               }

               if (footerLines != null) {
                  availableHeight -= footerTotalHeight + 2;
               }

               int maxRows = Math.max(1, availableHeight / entryHeight);
               int entryWidth = this.calculateEntryWidth(playerList, maxPlayers);
               int maxColumnsPerWidth = Math.max(1, (scaledWidth - 20) / (entryWidth + 5));
               int columns;
               int rows;
               if (maxPlayers <= maxRows) {
                  columns = 1;
                  rows = maxPlayers;
               } else {
                  columns = Math.min(maxColumnsPerWidth, (maxPlayers + maxRows - 1) / maxRows);
                  rows = (maxPlayers + columns - 1) / columns;
                  if (rows > maxRows) {
                     rows = maxRows;
                     maxPlayers = maxRows * columns;
                  }
               }

               int playerListWidth = entryWidth * columns + (columns - 1) * 5;
               int playerListHeight = rows * entryHeight;
               maxTextWidth = Math.max(maxTextWidth, playerListWidth);
               int totalHeight = playerListHeight;
               if (headerLines != null) {
                  totalHeight = playerListHeight + headerTotalHeight + 2;
               }

               if (footerLines != null) {
                  totalHeight += footerTotalHeight + 2;
               }

               int bgX = scaledWidth / 2 - maxTextWidth / 2 - 3;
               int bgY = 7;
               int bgWidth = maxTextWidth + 6;
               int bgHeight = totalHeight + 6;
               context.fill(bgX, bgY, bgX + bgWidth, bgY + bgHeight, Integer.MIN_VALUE);
               int currentY = 10;
               if (headerLines != null) {
                  for(OrderedText line : headerLines) {
                     int lineWidth = mc.textRenderer.getWidth(line);
                     context.drawText(mc.textRenderer, line, scaledWidth / 2 - lineWidth / 2, currentY, -1, false);
                     currentY += 9;
                  }

                  currentY += 2;
               }

               int playerStartX = scaledWidth / 2 - playerListWidth / 2;
               int playerStartY = currentY;
               int index = 0;

               for(int col = 0; col < columns && index < maxPlayers; ++col) {
                  for(int row = 0; row < rows && index < maxPlayers && index < playerList.size(); ++row) {
                     PlayerListEntry entry = playerList.get(index);
                     int x = playerStartX + col * (entryWidth + 5);
                     int y = playerStartY + row * entryHeight;
                     this.renderPlayerEntry(context, entry, x, y, entryWidth);
                     ++index;
                  }
               }

               currentY = playerStartY + playerListHeight;
               if (footerLines != null) {
                  currentY += 2;

                  for(OrderedText line : footerLines) {
                     int lineWidth = mc.textRenderer.getWidth(line);
                     context.drawText(mc.textRenderer, line, scaledWidth / 2 - lineWidth / 2, currentY, -1, false);
                     currentY += 9;
                  }
               }

            }
         }
      }
   }

   private int calculateEntryWidth(List<PlayerListEntry> playerList, int maxPlayers) {
      int maxWidth = 50;
      int headOffset = this.showHeads.isEnabled() ? 10 : 0;
      int count = Math.min(maxPlayers, playerList.size());

      for(int i = 0; i < count; ++i) {
         PlayerListEntry entry = playerList.get(i);
         Text displayName = this.getDisplayName(entry);
         int nameWidth = mc.textRenderer.getWidth(displayName) + headOffset + 4;
         maxWidth = Math.max(maxWidth, nameWidth);
      }

      return Math.min(maxWidth, 200);
   }

   private void renderPlayerEntry(DrawContext context, PlayerListEntry entry, int x, int y, int width) {
      int backgroundColor = mc.options.getTextBackgroundColor(0.5f);
      context.fill(x, y, x + width, y + 8, backgroundColor);
      Text displayName = this.getDisplayName(entry);
      int textX = x;
      if (this.showHeads.isEnabled()) {
         // Head rendering temporarily disabled due to API changes
         textX = x + 10;
      }

      context.drawText(mc.textRenderer, displayName, textX, y, this.getNameColor(entry), false);
   }

   private Text getDisplayName(PlayerListEntry entry) {
      Text displayName = entry.getDisplayName();
      if (displayName != null) {
         return entry.getGameMode() == GameMode.SPECTATOR ? displayName.copy().append(Text.literal(" (Spectator)").withColor(0x9E9E9E)) : displayName;
      } else {
         Team team = entry.getScoreboardTeam();
         Text name = Text.literal(entry.getProfile().getName());
         if (team != null) {
            Text decorated = Team.decorateName(team, name);
            return entry.getGameMode() == GameMode.SPECTATOR ? decorated.copy().append(Text.literal(" (Spectator)").withColor(0x9E9E9E)) : decorated;
         } else {
            return entry.getGameMode() == GameMode.SPECTATOR ? name.copy().append(Text.literal(" (Spectator)").withColor(0x9E9E9E)) : name;
         }
      }
   }

   private int getNameColor(PlayerListEntry entry) {
      return entry.getGameMode() == GameMode.SPECTATOR ? 0x9E9E9E : -1;
   }
}
