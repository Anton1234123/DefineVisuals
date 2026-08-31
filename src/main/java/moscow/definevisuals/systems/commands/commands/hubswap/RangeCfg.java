package moscow.definevisuals.systems.commands.commands.hubswap;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;

public final class RangeCfg {
   private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("DefineVisuals-hubswap-ranges.txt");
   private final List<RangeEntry> ranges = new ArrayList();
   private int total = 51;

   public RangeCfg() {
      this.resetDefaults();
      this.load();
   }

   private void resetDefaults() {
      this.ranges.clear();
      this.ranges.add(new RangeEntry("solo", "", 1, 12));
      this.ranges.add(new RangeEntry("duo", "", 13, 26));
      this.ranges.add(new RangeEntry("trio", "", 27, 39));
      this.ranges.add(new RangeEntry("clans", "", 40, 51));
      this.total = 51;
   }

   public List<RangeEntry> getRanges() {
      return Collections.unmodifiableList(this.ranges);
   }

   public int getTotal() {
      return this.total;
   }

   public void setRange(List<RangeEntry> newRanges, int newTotal) {
      if (newTotal < 1) {
         newTotal = 1;
      }

      this.total = newTotal;
      List<RangeEntry> sorted = new ArrayList(newRanges);
      sorted.sort(Comparator.comparingInt(RangeEntry::min));
      this.ranges.clear();

      for(RangeEntry e : sorted) {
         int min = Math.max(1, Math.min(e.min(), this.total));
         int max = Math.max(min, Math.min(e.max(), this.total));
         if (!this.ranges.isEmpty()) {
            RangeEntry prev = (RangeEntry)this.ranges.get(this.ranges.size() - 1);
            if (min <= prev.max()) {
               min = prev.max() + 1;
            }

            if (max < min) {
               max = min;
            }
         }

         this.ranges.add(new RangeEntry(e.key(), e.name(), min, max));
      }

      if (!this.ranges.isEmpty()) {
         RangeEntry last = (RangeEntry)this.ranges.get(this.ranges.size() - 1);
         if (last.max() < this.total) {
            this.ranges.set(this.ranges.size() - 1, new RangeEntry(last.key(), last.name(), last.min(), this.total));
         }
      }

      this.save();
   }

   public RangeEntry find(int number) {
      for(RangeEntry entry : this.ranges) {
         if (number >= entry.min() && number <= entry.max()) {
            return entry;
         }
      }

      return null;
   }

   public int getMin() {
      return 1;
   }

   public int getMax() {
      return this.total;
   }

   private void load() {
      try {
         if (Files.exists(FILE, new LinkOption[0])) {
            List<String> lines = Files.readAllLines(FILE, StandardCharsets.UTF_8);
            int loadedTotal = 51;
            List<RangeEntry> loaded = new ArrayList();

            for(String line : lines) {
               line = line.trim();
               if (!line.isEmpty() && !line.startsWith("#")) {
                  if (line.startsWith("total=")) {
                     try {
                        loadedTotal = Integer.parseInt(line.substring(6).trim());
                     } catch (NumberFormatException var11) {
                     }
                  } else {
                     String[] parts = line.split("=");
                     if (parts.length == 2) {
                        String[] rangeParts = parts[1].split("-");
                        if (rangeParts.length == 2) {
                           try {
                              int min = Integer.parseInt(rangeParts[0].trim());
                              int max = Integer.parseInt(rangeParts[1].trim());
                              loaded.add(new RangeEntry(parts[0].trim(), name(parts[0].trim()), min, max));
                           } catch (NumberFormatException var10) {
                           }
                        }
                     }
                  }
               }
            }

            if (!loaded.isEmpty()) {
               this.total = loadedTotal;
               this.ranges.clear();
               this.ranges.addAll(loaded);
            }
         }
      } catch (IOException var12) {
      }

   }

   public void save() {
      try {
         Files.createDirectories(FILE.getParent());
         List<String> lines = new ArrayList();
         lines.add("total=" + this.total);

         for(RangeEntry e : this.ranges) {
            String var10001 = e.key();
            lines.add(var10001 + "=" + e.min() + "-" + e.max());
         }

         Files.write(FILE, lines, StandardCharsets.UTF_8);
      } catch (IOException var4) {
      }

   }

   private static String name(String key) {
      String var10000;
      switch (key) {
         case "solo" -> var10000 = "";
         case "duo" -> var10000 = "";
         case "trio" -> var10000 = "";
         case "clans" -> var10000 = "";
         default -> var10000 = key;
      }

      return var10000;
   }

   public static record RangeEntry(String key, String name, int min, int max) {
   }
}
