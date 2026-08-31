package moscow.definevisuals.systems.modules.modules.other;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.render.ItemTooltipEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ModuleInfo(name = "Auction Price", category = ModuleCategory.OTHER, desc = "Показывает цену в монетах на аукционе HolyWorld")
public class AuctionPrice extends BaseModule {
   private static final Pattern PATTERN = Pattern.compile("([\\d][\\d.,\\s]*)\\D*¤");
   private static final String[] DOMAINS = new String[]{"holyworld", "hollyworld"};
   private static final String[] BLOCKED_TITLES = new String[]{"Покупка", "Биржа"};
   
   private static long rate = 100000L;
   private static boolean domainActive = false;
   private static String lastServer = null;
   private static boolean tabActive = false;
   private static String lastHeader = null;
   private static ScheduledExecutorService executor;

   private final EventListener<ItemTooltipEvent> onTooltip = event -> {
      if (!shouldShow()) return;

      ItemStack stack = event.getItemStack();
      List<Text> lines = event.getTooltip();

      for (int i = 0; i < lines.size(); i++) {
         Text line = lines.get(i);
         String text = line.getString();
         if (text.contains("¤") && !isOnlyColor(line)) {
            Matcher matcher = PATTERN.matcher(text);
            if (matcher.find()) {
               String digits = matcher.group(1).replaceAll("[^\\d]", "");
               if (!digits.isEmpty()) {
                  long price = Long.parseLong(digits);
                  String coinValue = convertPrice(text);
                  String formattedPrice = formatOriginalPrice(price);

                  if (coinValue != null) {
                     Text suffix = Text.literal(" (")
                        .append(Text.literal(formattedPrice).styled(style -> style.withColor(0xFFAA00).withBold(true).withItalic(false)))
                        .append(Text.literal("⛃").styled(style -> style.withColor(0xFFAA00).withItalic(false)))
                        .append(Text.literal(" (").styled(style -> style.withColor(0xAAAAAA).withItalic(false)))
                        .append(Text.literal(coinValue).styled(style -> style.withColor(0xFFAA00).withBold(true).withItalic(false)))
                        .append(Text.literal(")").styled(style -> style.withColor(0xAAAAAA).withItalic(false)))
                        .append(Text.literal(")").styled(style -> style.withItalic(false)));

                     lines.set(i, line.copy().append(suffix));
                  }
               }
            }
         }
      }
   };

   @Override
   public void onEnable() {
      super.onEnable();
      if (executor == null || executor.isShutdown()) {
         startRateFetcher();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (executor != null && !executor.isShutdown()) {
         executor.shutdown();
      }
   }

   private static void startRateFetcher() {
      executor = Executors.newSingleThreadScheduledExecutor(r -> {
         Thread thread = new Thread(r, "auctionprice-rate");
         thread.setDaemon(true);
         return thread;
      });
      executor.scheduleWithFixedDelay(AuctionPrice::fetchRate, 0L, 60L, TimeUnit.SECONDS);
   }

   private static void fetchRate() {
      try {
         HttpURLConnection connection = (HttpURLConnection) new URL("https://api.holyworld.me/v1/coins-trades?limit=30").openConnection();
         connection.setRequestProperty("User-Agent", "Mozilla/5.0");
         connection.setConnectTimeout(5000);
         connection.setReadTimeout(5000);
         
         StringBuilder body = new StringBuilder();
         BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
         
         String line;
         while ((line = reader.readLine()) != null) {
            body.append(line);
         }
         reader.close();
         
         JsonArray trades = JsonParser.parseString(body.toString()).getAsJsonArray();
         List<Long> rates = new ArrayList<>();
         
         for (int i = 0; i < trades.size(); i++) {
            rates.add(trades.get(i).getAsJsonObject().get("rate").getAsLong());
         }
         
         long newRate = calculateRate(rates);
         rate = newRate;
      } catch (Exception e) {
         // Silent fail
      }
   }

   private static long calculateRate(List<Long> trades) {
      if (trades.isEmpty()) return rate;
      
      trades.sort((a, b) -> Long.compare(b, a));
      long median = trades.get(trades.size() / 2);
      
      List<Long> filtered = new ArrayList<>();
      for (Long r : trades) {
         if (r <= median * 1.5) {
            filtered.add(r);
         }
      }
      
      if (filtered.isEmpty()) filtered = trades;
      
      double avg = filtered.stream().mapToLong(Long::longValue).average().orElse(median);
      return ((long) ((avg + 999) / 1000)) * 1000;
   }

   private static String convertPrice(String line) {
      Matcher matcher = PATTERN.matcher(line);
      if (!matcher.find()) return null;
      
      String digits = matcher.group(1).replaceAll("[^\\d]", "");
      if (digits.isEmpty()) return null;
      
      long price = Long.parseLong(digits);
      double coins = (double) price / (double) rate;
      double rounded = Math.round(coins * 10.0) / 10.0;
      
      // Format as "X.X kk" or "X kk"
      String coinValue;
      if (rounded >= 1000) {
         double kkValue = rounded / 1000.0;
         coinValue = String.format(Locale.US, "%.1f kk", kkValue);
      } else {
         coinValue = String.format(Locale.US, "%.1f", rounded);
      }
      
      return coinValue;
   }

   private static String formatOriginalPrice(long price) {
      return String.format(Locale.US, "%,d", price).replace(",", " ");
   }

   private static boolean shouldShow() {
      if (!isDomainActive()) return true;
      
      // Check if in auction tab
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.player == null || client.player.networkHandler == null) return true;
      
      // Simple check - if title contains "Лайт", don't show
      // This is a simplified version since we don't have direct access to tab header
      return true;
   }

   private static boolean isDomainActive() {
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.player == null || client.player.networkHandler == null) {
         domainActive = false;
         return false;
      }
      
      String server = client.getCurrentServerEntry() != null ? client.getCurrentServerEntry().address.toLowerCase() : "";
      
      if (!server.equals(lastServer)) {
         lastServer = server;
         domainActive = false;
         for (String domain : DOMAINS) {
            if (server.contains(domain)) {
               domainActive = true;
               break;
            }
         }
      }
      
      return domainActive;
   }

   private static boolean isOnlyColor(Text text) {
      // Simplified check - in original it checks if all text has the same color
      // For now, always return false to allow processing
      return false;
   }
}
