package moscow.definevisuals.systems.modules.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Generated;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.localization.Language;
import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.systems.modules.Module;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.modules.other.Sounds;
import moscow.definevisuals.systems.modules.modules.visuals.MenuModule;
import moscow.definevisuals.systems.notifications.NotificationType;
import moscow.definevisuals.systems.setting.Setting;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.game.TextUtility;
import moscow.definevisuals.utility.sounds.ClientSounds;

public abstract class BaseModule implements Module {
   private static final ScheduledExecutorService autoSaveExecutor = Executors.newSingleThreadScheduledExecutor();
   private static CompletableFuture<Void> pendingAutoSave = null;
   private static boolean isLoadingConfig = false;

   private final ModuleInfo info = this.getClass().getAnnotation(ModuleInfo.class);
   private int key;
   private ModuleCategory category;
   private boolean enabled;
   private boolean hidden;
   private String name;
   private List<Setting> settings = new ArrayList<>();
   private final Animation keybindsAnimation = new Animation(300L, 0.0F, Easing.FIGMA_EASE_IN_OUT);

   public BaseModule() {
      this.name = this.info.name();
      this.category = this.info.category();
      this.key = this.info.key();
   }

   @Override
   public void toggle() {
      this.setEnabled(!this.enabled, false);
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   @Override
   public void tick() {
   }

   @Override
   public void disable() {
      this.setEnabled(false, false);
   }

   @Override
   public void enable() {
      this.setEnabled(true, false);
   }

   @Override
   public void setEnabled(boolean newState, boolean silent) {
      if (this.enabled != newState) {
         this.enabled = newState;
         if (!(this instanceof MenuModule) && DefineVisuals.getInstance().getModuleManager().getModule(Sounds.class).isEnabled() && !silent) {
            ClientSounds.MODULE
               .play(DefineVisuals.getInstance().getModuleManager().getModule(Sounds.class).getVolume().getCurrentValue(), this.enabled ? 1.1F : 1.0F);
         }

         if (this.enabled) {
            DefineVisuals.getInstance().getEventManager().subscribe(this);
            if (!silent) {
               DefineVisuals.getInstance()
                  .getNotificationManager()
                  .addNotification(
                     NotificationType.SUCCESS,
                     this.name.replace(" ", "")
                        + " "
                        + Localizator.translate("enabled")
                        + (Localizator.getCurrentLanguage() == Language.RU_RU ? TextUtility.makeGender(this.name) : "")
                  );
            }

            this.onEnable();
         } else {
            DefineVisuals.getInstance().getEventManager().unsubscribe(this);
            if (!silent) {
               DefineVisuals.getInstance()
                  .getNotificationManager()
                  .addNotification(
                     NotificationType.ERROR,
                     this.name.replace(" ", "")
                        + " "
                        + Localizator.translate("disabled")
                        + (Localizator.getCurrentLanguage() == Language.RU_RU ? TextUtility.makeGender(this.name) : "")
                  );
            }

            this.onDisable();
         }

         this.scheduleAutoSave();
      }
   }

   public String getSettingName(String key) {
      return "modules.settings." + this.getName().toLowerCase().replace(" ", "_") + "." + key;
   }

   private void scheduleAutoSave() {
      scheduleAutoSaveStatic();
   }

   public static void scheduleAutoSaveStatic() {
      if (isLoadingConfig) {
         return;
      }
      if (pendingAutoSave != null) {
         pendingAutoSave.cancel(false);
      }
      pendingAutoSave = CompletableFuture.runAsync(() -> {
         try {
            Thread.sleep(2000);
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
         }
         DefineVisuals.getInstance().getConfigManager().getAutoSaveConfig().save();
      }, autoSaveExecutor);
   }

   public static void setLoadingConfig(boolean loading) {
      isLoadingConfig = loading;
   }

   @Generated
   @Override
   public ModuleInfo getInfo() {
      return this.info;
   }

   @Generated
   @Override
   public int getKey() {
      return this.key;
   }

   @Generated
   @Override
   public ModuleCategory getCategory() {
      return this.category;
   }

   @Generated
   @Override
   public boolean isEnabled() {
      return this.enabled;
   }

   @Generated
   @Override
   public boolean isHidden() {
      return this.hidden;
   }

   @Generated
   @Override
   public String getName() {
      return this.name;
   }

   @Generated
   @Override
   public List<Setting> getSettings() {
      return this.settings;
   }

   @Generated
   @Override
   public Animation getKeybindsAnimation() {
      return this.keybindsAnimation;
   }

   @Generated
   @Override
   public void setKey(int key) {
      this.key = key;
      this.scheduleAutoSave();
   }

   @Generated
   public void setCategory(ModuleCategory category) {
      this.category = category;
   }

   @Generated
   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   @Generated
   public void setHidden(boolean hidden) {
      this.hidden = hidden;
   }

   @Generated
   public void setName(String name) {
      this.name = name;
   }

   @Generated
   public void setSettings(List<Setting> settings) {
      this.settings = settings;
   }
}
