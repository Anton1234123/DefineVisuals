package moscow.definevisuals.systems.modules.modules.other;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.render.ItemTooltipEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.utility.game.ItemUtility;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ModuleInfo(name = "Spawner Tooltip", category = ModuleCategory.OTHER, desc = "Показывает информацию о спавнерах")
public class SpawnerTooltip extends BaseModule {
   private static final Map<String, Integer> HIGHLIGHT_CACHE = Collections.synchronizedMap(new LinkedHashMap<String, Integer>(256, 0.75F, true) {
      protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
         return this.size() > 256;
      }
   });

   private static final String[] ROMAN = new String[]{"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XIII", "XIX", "XX"};

   private final EventListener<ItemTooltipEvent> onTooltip = event -> {
      ItemStack stack = event.getItemStack();
      NbtCompound nbt = ItemUtility.getNBT(stack);
      if (nbt != null && nbt.contains("gs_marker")) {
         List<Text> lines = parseSpawnerData(nbt);
         for (Text line : lines) {
            event.getTooltip().add(line);
         }
      }
   };

   private List<Text> parseSpawnerData(NbtCompound nbt) {
      List<Text> lines = new ArrayList<>();
      
      lines.add(Text.literal("                               ").formatted(Formatting.STRIKETHROUGH).withColor(0x555555));
      lines.add(Text.literal("⚙ Данные спавнера").withColor(0x5596DF).formatted(Formatting.BOLD));

      // Parse sword data
      if (nbt.contains("gs_sword_item")) {
         String rawJson = nbt.getString("gs_sword_item");
         if (rawJson != null && !rawJson.trim().isEmpty()) {
            try {
               JsonObject json = JsonParser.parseString(rawJson).getAsJsonObject();
               String name = null;
               if (json.has("name")) {
                  name = stripBrokenHex(json.get("name").getAsString());
               }
               String type = json.has("type") ? json.get("type").getAsString() : null;
               
               lines.add(Text.literal(""));
               String swordName = name != null ? name : (type != null ? translateSword(type) : "Неизвестный меч");
               lines.add(Text.literal("⚔ Меч: ").withColor(0x555555).append(Text.literal(swordName).withColor(0x5596DF)));

               if (json.has("enchants") && json.get("enchants").isJsonObject()) {
                  lines.add(Text.literal("  Чары:").withColor(0xAAAAAA));
                  JsonObject enchants = json.getAsJsonObject("enchants");
                  for (Map.Entry<String, JsonElement> entry : enchants.entrySet()) {
                     int level = entry.getValue().getAsInt();
                     String enchantId = entry.getKey();
                     int colon = enchantId.indexOf(':');
                     String path = colon >= 0 ? enchantId.substring(colon + 1) : enchantId;
                     
                     int color = "mob-farmer-enchant".equals(path) ? 0x55D955 : 0xD5D5FF;
                     String enchantName = translateEnchant(enchantId);
                     lines.add(Text.literal("   • ").withColor(0x555555)
                        .append(Text.literal(enchantName).withColor(color))
                        .append(Text.literal(" " + toRoman(level)).withColor(0xB2B2FF)));
                  }
               }
            } catch (Exception e) {
               lines.add(Text.literal(""));
               lines.add(Text.literal("Ошибка чтения данных меча").withColor(0xFF3333));
            }
         }
      }

      // Parse stats
      if (nbt.contains("gs_kills_total")) {
         long killsTotal = nbt.getLong("gs_kills_total");
         long killsRemaining = nbt.contains("gs_kills_remaining") ? nbt.getLong("gs_kills_remaining") : 0L;
         long killed = Math.max(0L, Math.min(killsTotal, killsTotal - killsRemaining));
         int percent = killsTotal <= 0L ? 0 : (int)Math.round((double)killed * 100.0 / (double)killsTotal);
         
         lines.add(Text.literal(""));
         lines.add(Text.literal("▍ Убито мобов: ").withColor(0xAAAAAA)
            .append(Text.literal(formatNumber(killed) + " / " + formatNumber(killsTotal) + "  (" + percent + "%)").withColor(0xFFD700)));
      }

      if (nbt.contains("gs_mob_type")) {
         String mobType = nbt.getString("gs_mob_type");
         lines.add(Text.literal("▍ Тип моба: ").withColor(0xAAAAAA)
            .append(Text.literal(translateMob(mobType)).withColor(0xFFD700)));
      }

      if (nbt.contains("gs_total_eggs")) {
         int totalEggs = nbt.getInt("gs_total_eggs");
         lines.add(Text.literal("▍ Вставлено яиц: ").withColor(0xAAAAAA)
            .append(Text.literal(String.valueOf(totalEggs)).withColor(0xFFD700)));
      }

      lines.add(Text.literal("                               ").formatted(Formatting.STRIKETHROUGH).withColor(0x555555));
      
      return lines;
   }

   private String stripBrokenHex(String s) {
      if (s == null) return "";
      return s.replaceAll("§x(?:§[0-9a-fA-F]){0,5}(§[0-9a-fA-F])?(?!§[0-9a-fA-F])", "").trim();
   }

   private String toRoman(int number) {
      return number > 0 && number < ROMAN.length ? ROMAN[number] : String.valueOf(number);
   }

   private String formatNumber(long n) {
      return String.valueOf(n);
   }

   private String translateMob(String rawId) {
      Map<String, String> mobs = new HashMap<>();
      mobs.put("MAGMA_CUBE", "Магма-куб");
      mobs.put("ZOMBIE", "Зомби");
      mobs.put("SKELETON", "Скелет");
      mobs.put("CREEPER", "Крипер");
      mobs.put("SPIDER", "Паук");
      mobs.put("ENDERMAN", "Эндермен");
      mobs.put("BLAZE", "Блейз");
      mobs.put("WITCH", "Ведьма");
      mobs.put("PIGLIN", "Пиглин");
      mobs.put("WITHER_SKELETON", "Визер-скелет");
      mobs.put("SLIME", "Слизень");
      mobs.put("PHANTOM", "Фантом");
      mobs.put("CHICKEN", "Курица");
      mobs.put("PIG", "Свинья");
      mobs.put("HOGLIN", "Хоглин");
      mobs.put("EVOKER", "Вызыватель");
      mobs.put("GHAST", "Гаст");
      mobs.put("COW", "Корова");
      mobs.put("VINDICATOR", "Поборник");
      mobs.put("PIGLIN_BRUTE", "Брутальный пиглин");
      mobs.put("CAVE_SPIDER", "Пещерный паук");
      mobs.put("DROWNED", "Утопленник");
      return mobs.getOrDefault(rawId, rawId);
   }

   private String translateSword(String type) {
      Map<String, String> swords = Map.of(
         "minecraft:diamond_sword", "Алмазный меч",
         "minecraft:netherite_sword", "Незеритовый меч",
         "minecraft:iron_sword", "Железный меч",
         "minecraft:stone_sword", "Каменный меч",
         "minecraft:wooden_sword", "Деревянный меч",
         "minecraft:golden_sword", "Золотой меч"
      );
      return swords.getOrDefault(type, "Меч");
   }

   private String translateEnchant(String id) {
      int colon = id.indexOf(':');
      String path = colon >= 0 ? id.substring(colon + 1) : id;
      
      Map<String, String> enchants = new HashMap<>();
      enchants.put("sharpness", "Острота");
      enchants.put("smite", "Небесная кара");
      enchants.put("bane_of_arthropods", "Бич членистоногих");
      enchants.put("knockback", "Отдача");
      enchants.put("fire_aspect", "Заговор огня");
      enchants.put("looting", "Добыча");
      enchants.put("sweeping", "Разящий клинок");
      enchants.put("sweeping_edge", "Разящий клинок");
      enchants.put("unbreaking", "Прочность");
      enchants.put("mending", "Починка");
      enchants.put("vanishing_curse", "Проклятие утраты");
      enchants.put("binding_curse", "Проклятие несъёмности");
      enchants.put("efficiency", "Эффективность");
      enchants.put("fortune", "Удача");
      enchants.put("silk_touch", "Шёлковое касание");
      enchants.put("protection", "Защита");
      enchants.put("fire_protection", "Огнеупорность");
      enchants.put("blast_protection", "Взрывоустойчивость");
      enchants.put("projectile_protection", "Защита от снарядов");
      enchants.put("feather_falling", "Невесомость");
      enchants.put("respiration", "Подводное дыхание");
      enchants.put("aqua_affinity", "Подводник");
      enchants.put("depth_strider", "Подводная ходьба");
      enchants.put("frost_walker", "Ледоход");
      enchants.put("power", "Сила");
      enchants.put("punch", "Ударная волна");
      enchants.put("flame", "Воспламенение");
      enchants.put("infinity", "Бесконечность");
      enchants.put("luck_of_the_sea", "Везучий рыбак");
      enchants.put("lure", "Приманка");
      enchants.put("critical-enchant-custom", "Критический");
      enchants.put("destroyer-enchant-custom", "Разрушитель");
      enchants.put("rich-enchant-custom", "Богач");
      enchants.put("mob-farmer-enchant", "Фармер");
      
      String translated = enchants.get(path);
      if (translated != null) return translated;
      
      String pretty = path.replace("-enchant-custom", "").replace('-', ' ').replace('_', ' ');
      return pretty.isEmpty() ? path : Character.toUpperCase(pretty.charAt(0)) + pretty.substring(1);
   }
}
