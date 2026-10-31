package moscow.definevisuals;

import lombok.Generated;
import moscow.definevisuals.framework.shader.GlProgram;
import moscow.definevisuals.systems.background.BackgroundManager;
import moscow.definevisuals.systems.commands.CommandRegistry;
import moscow.definevisuals.systems.config.ConfigDropHandler;
import moscow.definevisuals.systems.config.ConfigManager;
import moscow.definevisuals.systems.event.EventIntegration;
import moscow.definevisuals.systems.event.EventManager;
import moscow.definevisuals.systems.event.handlers.ServerConnectionHandler;
import moscow.definevisuals.systems.file.FileManager;
import moscow.definevisuals.systems.friends.FriendManager;
import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.systems.modules.ModuleManager;
import moscow.definevisuals.systems.modules.constructions.swinganim.SwingManager;
import moscow.definevisuals.systems.modules.constructions.swinganim.presets.SwingPresetManager;
import moscow.definevisuals.systems.modules.listeners.ModuleTickListener;
import moscow.definevisuals.systems.modules.listeners.ModuleWidgetRenderer;
import moscow.definevisuals.systems.notifications.NotificationManager;
import moscow.definevisuals.systems.poshalko.PoshalkoHandler;
import moscow.definevisuals.systems.target.TargetManager;
import moscow.definevisuals.systems.theme.ThemeManager;
import moscow.definevisuals.systems.waypoints.WayPointsManager;
import moscow.definevisuals.ui.hud.Hud;
import moscow.definevisuals.ui.menu.MenuScreen;
import moscow.definevisuals.utility.game.TitleBarHelper;
import moscow.definevisuals.utility.game.server.TPSHandler;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import moscow.definevisuals.utility.math.calculator.ChatListener;
import moscow.definevisuals.utility.render.DrawUtility;
import moscow.definevisuals.utility.rotations.RotationHandler;
import moscow.definevisuals.utility.rotations.RotationUpdateListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum DefineVisuals implements IMinecraft {
   INSTANCE;

   public static final String NAME = "Define Visuals";
   public static final String BUILD_TYPE = "Alpha";
   public static final String VERSION = "1.1";
   public static final String MOD_ID = "definevisuals";
   public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
   private EventManager eventManager;
   private ThemeManager themeManager;
   private BackgroundManager backgroundManager;
   private ModuleManager moduleManager;
   private CommandRegistry commandManager;
   private FriendManager friendManager;
   private RotationHandler rotationHandler;
   private TargetManager targetManager;
   private FileManager fileManager;
   private NotificationManager notificationManager;
   private ConfigManager configManager;
   private SwingManager swingManager;
   private TPSHandler tpsHandler;
   private Hud hud;
   private ServerConnectionHandler serverConnectionHandler;
   private PoshalkoHandler poshalkoHandler;
   private WayPointsManager wayPointsManager;
   private SwingPresetManager swingPresetManager;
   private MenuScreen menuScreen;
   private ChatListener chatListener;
   private boolean panic;
   public void initialize() {
      LOGGER.info("Initializing {}...", "DefineVisuals");
      this.wayPointsManager = new WayPointsManager();
      this.eventManager = new EventManager();
      this.friendManager = new FriendManager();
      this.themeManager = new ThemeManager();
      this.backgroundManager = new BackgroundManager();
      this.rotationHandler = new RotationHandler(new RotationUpdateListener());
      this.targetManager = new TargetManager();
      this.fileManager = new FileManager();
      this.moduleManager = new ModuleManager(new ModuleTickListener(), new ModuleWidgetRenderer());
      this.hud = new Hud();
      this.tpsHandler = new TPSHandler();
      this.notificationManager = new NotificationManager();
      this.fileManager.registerClientFiles();
      this.moduleManager.registerModules();
      this.configManager = new ConfigManager();
      this.configManager.handle();
      this.moduleManager.enableModules();
      this.commandManager = new CommandRegistry();
      this.commandManager.initCommands();
      this.swingManager = new SwingManager();
      this.swingPresetManager = new SwingPresetManager();
      this.swingPresetManager.handle();
      this.fileManager.loadClientFiles();
      ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
         public Identifier getFabricId() {
            return DefineVisuals.id("after_shader_load");
         }

         public void reload(ResourceManager manager) {
            GlProgram.loadAndSetupPrograms();
         }
      });
      DrawUtility.initializeShaders();
      Localizator.loadTranslations();
      this.chatListener = new ChatListener();
      this.serverConnectionHandler = new ServerConnectionHandler();
      this.poshalkoHandler = new PoshalkoHandler();

      ConfigDropHandler.init();
      TitleBarHelper.setDarkTitleBar();
      TitleBarHelper.setCustomWindowIcon();
      new EventIntegration();
      LOGGER.info("{} initialized", NAME);
   }

   public void shutdown() {
      LOGGER.info("Shutting down...");
      this.fileManager.saveClientFiles();
      if (!this.isPanic()) {
         this.configManager.getAutoSaveConfig().save();
      }

      if (!this.isPanic()) {
         this.swingPresetManager.getAutoSavePreset().save();
      }

      this.setPanic(false);
   }

   public static DefineVisuals getInstance() {
      return INSTANCE;
   }

   public static Logger getLOGGER() {
      return LOGGER;
   }

   public static Identifier id(String path) {
      return Identifier.of(MOD_ID, path);
   }
   @Generated
   public EventManager getEventManager() {
      return this.eventManager;
   }

   @Generated
   public ThemeManager getThemeManager() {
      return this.themeManager;
   }

   @Generated
   public BackgroundManager getBackgroundManager() {
      return this.backgroundManager;
   }

   @Generated
   public ModuleManager getModuleManager() {
      return this.moduleManager;
   }

   @Generated
   public CommandRegistry getCommandManager() {
      return this.commandManager;
   }

   @Generated
   public FriendManager getFriendManager() {
      return this.friendManager;
   }


   @Generated
   public RotationHandler getRotationHandler() {
      return this.rotationHandler;
   }

   @Generated
   public TargetManager getTargetManager() {
      return this.targetManager;
   }

   @Generated
   public FileManager getFileManager() {
      return this.fileManager;
   }

   @Generated
   public NotificationManager getNotificationManager() {
      return this.notificationManager;
   }

   @Generated
   public ConfigManager getConfigManager() {
      return this.configManager;
   }

   @Generated
   public SwingManager getSwingManager() {
      return this.swingManager;
   }

   @Generated
   public TPSHandler getTpsHandler() {
      return this.tpsHandler;
   }

   @Generated
   public Hud getHud() {
      return this.hud;
   }

   @Generated
   public ServerConnectionHandler getServerConnectionHandler() {
      return this.serverConnectionHandler;
   }

   @Generated
   public PoshalkoHandler getPoshalkoHandler() {
      return this.poshalkoHandler;
   }

   @Generated
   public WayPointsManager getWayPointsManager() {
      return this.wayPointsManager;
   }

   @Generated
   public SwingPresetManager getSwingPresetManager() {
      return this.swingPresetManager;
   }

   public MenuScreen getMenuScreen() {
      moscow.definevisuals.systems.modules.modules.visuals.MenuModule menuModule = this.moduleManager != null 
         ? this.moduleManager.getModule(moscow.definevisuals.systems.modules.modules.visuals.MenuModule.class) 
         : null;
      boolean isModern = menuModule != null && menuModule.getModern().isSelected();

      if (isModern) {
         if (this.menuScreen == null || !(this.menuScreen instanceof moscow.definevisuals.ui.menu.modern.ModernScreen)) {
            this.menuScreen = new moscow.definevisuals.ui.menu.modern.ModernScreen();
         }
      } else {
         if (this.menuScreen == null || !(this.menuScreen instanceof moscow.definevisuals.ui.menu.dropdown.DropDownScreen)) {
            this.menuScreen = new moscow.definevisuals.ui.menu.dropdown.DropDownScreen();
         }
      }
      return this.menuScreen;
   }

   @Generated
   public ChatListener getChatListener() {
      return this.chatListener;
   }

   @Generated
   public boolean isPanic() {
      return this.panic;
   }

   @Generated
   public void setMenuScreen(MenuScreen menuScreen) {
      this.menuScreen = menuScreen;
   }

   @Generated
   public void setPanic(boolean panic) {
      this.panic = panic;
   }
}
