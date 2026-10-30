package moscow.definevisuals.systems.modules;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.player.ClientPlayerTickEvent;
import moscow.definevisuals.systems.event.impl.render.HudRenderEvent;
import moscow.definevisuals.systems.event.impl.window.KeyPressEvent;
import moscow.definevisuals.systems.event.impl.window.MouseEvent;
import moscow.definevisuals.systems.modules.exception.UnknownModuleException;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.modules.modules.other.SRPSpoof;
import moscow.definevisuals.systems.modules.modules.other.*;
import moscow.definevisuals.systems.modules.modules.player.AutoSprint;
import moscow.definevisuals.systems.modules.modules.player.AutoRespawn;
import moscow.definevisuals.systems.modules.modules.player.ClickFriend;
import moscow.definevisuals.systems.modules.modules.player.ItemScroller;
import moscow.definevisuals.systems.modules.modules.player.MineHelper;
import moscow.definevisuals.systems.modules.modules.player.NoDelay;
import moscow.definevisuals.systems.modules.modules.player.NoFriendDamage;
import moscow.definevisuals.systems.modules.modules.player.ClanUpgrade;
import moscow.definevisuals.systems.modules.modules.player.HwHelper;
import moscow.definevisuals.systems.modules.modules.player.AutoAuth;
import moscow.definevisuals.systems.modules.modules.player.MacroMenu;
import moscow.definevisuals.systems.modules.modules.player.AntiLud;
import moscow.definevisuals.systems.modules.modules.player.AutoGG;
import moscow.definevisuals.systems.modules.modules.player.ShulkerSafe;
import moscow.definevisuals.systems.modules.modules.player.FreeLook;
import moscow.definevisuals.systems.modules.modules.visuals.Ambience;
import moscow.definevisuals.systems.modules.modules.visuals.AspectRatio;
import moscow.definevisuals.systems.modules.modules.visuals.CustomFog;
import moscow.definevisuals.systems.modules.modules.visuals.FriendMarkers;
import moscow.definevisuals.systems.modules.modules.visuals.Interface;
import moscow.definevisuals.systems.modules.modules.visuals.KillEffects;
import moscow.definevisuals.systems.modules.modules.visuals.MenuModule;
import moscow.definevisuals.systems.modules.modules.visuals.Prediction;
import moscow.definevisuals.systems.modules.modules.visuals.Removals;
import moscow.definevisuals.systems.modules.modules.visuals.SwingAnimation;
import moscow.definevisuals.systems.modules.modules.visuals.TargetESP;
import moscow.definevisuals.systems.modules.modules.visuals.ViewModel;
import moscow.definevisuals.systems.modules.modules.visuals.World;
import moscow.definevisuals.systems.modules.modules.visuals.Beautifully;
import moscow.definevisuals.systems.modules.modules.visuals.AutoWaypoints;
import moscow.definevisuals.systems.modules.modules.visuals.BlockOverlay;
import moscow.definevisuals.systems.modules.modules.visuals.Crosshair;
import moscow.definevisuals.systems.modules.modules.visuals.DonateEffects;
import moscow.definevisuals.systems.modules.modules.visuals.HitColor;
import moscow.definevisuals.systems.modules.modules.visuals.SelfTag;
import moscow.definevisuals.systems.modules.modules.visuals.ChinaHat;
import moscow.definevisuals.systems.modules.modules.visuals.ItemPhysics;
import moscow.definevisuals.systems.modules.modules.visuals.JumpCircle;
import moscow.definevisuals.systems.modules.modules.visuals.ShaderHand;
import moscow.definevisuals.systems.modules.modules.visuals.Lyrics;
import moscow.definevisuals.systems.modules.modules.visuals.Cosmetics;
import moscow.definevisuals.systems.modules.modules.visuals.Zoom;
import net.minecraft.client.MinecraftClient;

public class ModuleManager {
   private final List<Module> modules = new ArrayList<>();
   private final EventListener<ClientPlayerTickEvent> tickListener;
   private final EventListener<HudRenderEvent> moduleWidgetRenderer;
   private final EventListener<KeyPressEvent> onKeyPress = event -> {
      if (MinecraftClient.getInstance().currentScreen == null) {
         for (Module module : this.getModules()) {
            if (module.getKey() == event.getKey() && module.getKey() != -1 && event.getAction() == 1) {
               module.toggle();
            }
         }
      }
   };
   private final EventListener<MouseEvent> onMouseButtonPress = event -> {
      if (MinecraftClient.getInstance().currentScreen == null) {
         for (Module module : this.getModules()) {
            if (module.getKey() == event.getButton() && module.getKey() != -1 && event.getAction() == 1) {
               module.toggle();
            }
         }
      }
   };

   public ModuleManager(EventListener<ClientPlayerTickEvent> tickListener, EventListener<HudRenderEvent> moduleWidgetRenderer) {
      this.tickListener = tickListener;
      this.moduleWidgetRenderer = moduleWidgetRenderer;
      DefineVisuals.getInstance().getEventManager().subscribe(this);
   }
   public void registerModules() {
      this.register(new AutoSprint());
      this.register(new MenuModule());
      this.register(new Removals());
      this.register(new Zoom());
      this.register(new Ambience());
      this.register(new SwingAnimation());
      this.register(new FriendMarkers());
      this.register(new SpawnerTooltip());
      this.register(new ViewModel());
      this.register(new Interface());
      this.register(new TargetESP());
      this.register(new AuctionPrice());
      this.register(new CustomFog());
      this.register(new World());
      this.register(new KillEffects());
      this.register(new Prediction());
      this.register(new MineHelper());
      this.register(new HwHelper());
      this.register(new ClickFriend());
      this.register(new NoDelay());
      this.register(new NoFriendDamage());
      this.register(new AutoRespawn());
      this.register(new NameProtect());
      this.register(new NoCommands());
      this.register(new AutoAuth());
      this.register(new Sounds());
      this.register(new Beautifully());
      this.register(new AutoWaypoints());
      this.register(new BetterTab());
      this.register(new BlockOverlay());
      this.register(new Crosshair());
      this.register(new DonateEffects());
      this.register(new HolyEvents());
      this.register(new ClanUpgrade());
      this.register(new MacroMenu());
      this.register(new CompassTooltip());
      this.register(new HealthIndicator());
      this.register(new DeathCoords());
      this.register(new Optimization());
      this.register(new HitColor());
      this.register(new SelfTag());
      this.register(new SeeInvisibles());
      this.register(new ChinaHat());
      this.register(new ItemPhysics());
      this.register(new ShaderHand());
      this.register(new JumpCircle());
      this.register(new AntiLud());
      this.register(new AutoGG());
      this.register(new ShulkerSafe());
      this.register(new AspectRatio());
      this.register(new ItemScroller());
      this.register(new SRPSpoof());
      this.register(new WindowTitle());
      this.register(new FreeLook());
      this.register(new Lyrics());
      this.register(new Cosmetics());
   }
   public void enableModules() {
      for (Module module : this.modules) {
         // Only enable if not already enabled by config loading
         if (!module.isEnabled() && module.getInfo().enabledByDefault()) {
            module.enable();
         }
      }
   }

   public void register(BaseModule module) {
      this.modules.add(module);
   }

   public <T extends Module> T getModule(String name) {
      return (T)this.modules
         .stream()
         .filter(module -> module.getName().replace(" ", "").equalsIgnoreCase(name) || module.getName().equalsIgnoreCase(name))
         .findFirst()
         .orElseThrow(() -> new UnknownModuleException(name));
   }

   public <T extends Module> T getModule(Class<T> clazz) {
      return (T)this.modules
         .stream()
         .filter(module -> module.getClass().equals(clazz) || module.getClass().getName().equals(clazz.getName()))
         .findFirst()
         .orElseThrow(() -> new UnknownModuleException(clazz.getSimpleName()));
   }

   @Generated
   public List<Module> getModules() {
      return this.modules;
   }

   @Generated
   public EventListener<ClientPlayerTickEvent> getTickListener() {
      return this.tickListener;
   }

   @Generated
   public EventListener<HudRenderEvent> getModuleWidgetRenderer() {
      return this.moduleWidgetRenderer;
   }

   @Generated
   public EventListener<KeyPressEvent> getOnKeyPress() {
      return this.onKeyPress;
   }

   @Generated
   public EventListener<MouseEvent> getOnMouseButtonPress() {
      return this.onMouseButtonPress;
   }
}

