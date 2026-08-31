package moscow.definevisuals.systems.commands.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.commands.Command;
import moscow.definevisuals.systems.commands.CommandBuilder;
import moscow.definevisuals.systems.commands.CommandContext;
import moscow.definevisuals.systems.commands.ParameterValidator;
import moscow.definevisuals.systems.commands.ValidationResult;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.render.ItemTooltipEvent;
import moscow.definevisuals.utility.game.MessageUtility;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;

public final class NbtCommand {
   private static boolean enabled = false;
   private static EventListener<ItemTooltipEvent> tooltipListener;
   
   private static final ParameterValidator<String> ACTION = ValidationResult::ok;

   public Command command() {
      return CommandBuilder.begin(
            "nbt",
            b -> b.aliases("nbttags", "нбт")
                  .desc("Показывает NBT теги предмета при наведении")
                  .param(
                        "action",
                        p -> p.validator(
                              text -> NbtCommand.Action.from(text)
                                    .map(a -> (ValidationResult) ValidationResult.ok(a))
                                    .orElseGet(() -> ValidationResult.error("Invalid action. Use: on/off")))
                              .suggests(NbtCommand.Action.allNames()))
                  .handler(this::handle))
            .build();
   }

   private void handle(CommandContext ctx) {
      NbtCommand.Action action = (NbtCommand.Action) ctx.arguments().get(0);
      action.createHandler().accept(null);
   }

   public static boolean isEnabled() {
      return enabled;
   }

   public static void init() {
      tooltipListener = event -> {
         if (enabled && event.getItemStack() != null && !event.getItemStack().isEmpty()) {
            addNbtToTooltip(event.getItemStack(), event.getTooltip());
         }
      };
      DefineVisuals.getInstance().getEventManager().subscribe(tooltipListener);
   }

   private static void addNbtToTooltip(ItemStack stack, List<Text> tooltip) {
      NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
      if (customData == null) {
         return;
      }
      
      NbtCompound nbt = customData.getNbt();
      if (nbt == null || nbt.isEmpty()) {
         return;
      }
      
      tooltip.add(Text.of(""));
      tooltip.add(Text.of("§a§lNBT Data:"));
      formatNbtToTooltip(nbt, tooltip, 0);
   }

   private static void formatNbtToTooltip(NbtElement nbt, List<Text> tooltip, int indent) {
      String prefix = "  ".repeat(indent);
      
      if (nbt instanceof NbtCompound compound) {
         for (String key : compound.getKeys()) {
            NbtElement element = compound.get(key);
            if (element instanceof NbtCompound nestedCompound) {
               tooltip.add(Text.of(prefix + "§e" + key + ":"));
               formatNbtToTooltip(nestedCompound, tooltip, indent + 1);
            } else {
               String value = element.asString();
               tooltip.add(Text.of(prefix + "§7" + key + ": §f" + value));
            }
         }
      } else {
         tooltip.add(Text.of(prefix + "§f" + nbt.asString()));
      }
   }

   private static enum Action {
      ON("on", "enable", "включить"),
      OFF("off", "disable", "выключить");

      private final List<String> names;

      private Action(String... names) {
         this.names = Arrays.stream(names).map(String::toLowerCase).collect(Collectors.toList());
      }

      private java.util.function.Consumer<String> createHandler() {
         return switch (this) {
            case ON -> s -> {
               enabled = true;
               MessageUtility.info(Text.of("§aNBT display enabled"));
            };
            case OFF -> s -> {
               enabled = false;
               MessageUtility.info(Text.of("§cNBT display disabled"));
            };
         };
      }

      static Optional<NbtCommand.Action> from(String input) {
         String key = input.toLowerCase();
         return Arrays.stream(values()).filter(a -> a.names.contains(key)).findFirst();
      }

      static List<String> allNames() {
         return Arrays.stream(values()).map(a -> a.names.getFirst()).collect(Collectors.toList());
      }
   }
}
