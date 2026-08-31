package moscow.definevisuals.systems.file.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.config.ConfigFile;
import moscow.definevisuals.systems.file.ClientFile;
import moscow.definevisuals.systems.file.FileManager;
import moscow.definevisuals.systems.file.api.FileInfo;
import moscow.definevisuals.systems.modules.constructions.swinganim.SwingManager;
import moscow.definevisuals.systems.modules.constructions.swinganim.SwingPhase;
import moscow.definevisuals.systems.modules.constructions.swinganim.presets.SwingPreset;
import moscow.definevisuals.systems.modules.constructions.swinganim.presets.SwingPresetManager;
import moscow.definevisuals.systems.modules.modules.player.AutoAuth;
import moscow.definevisuals.systems.setting.Setting;
import moscow.definevisuals.systems.theme.Theme;
import moscow.definevisuals.ui.components.ColorPicker;
import moscow.definevisuals.ui.hud.HudElement;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.Session.AccountType;

@FileInfo(name = "client")
public class ClientDataFile extends ClientFile implements IMinecraft {
   @Override
   public void write() {
      JsonObject json = new JsonObject();
      json.addProperty("username", mc.getSession().getUsername());
      json.addProperty("theme", DefineVisuals.getInstance().getThemeManager().getCurrentTheme().name());
      json.add("themes", this.getThemesJsonObject());
      json.addProperty("swing", DefineVisuals.getInstance().getSwingManager().getCurrent());
      json.add("hudElements", this.getHudElementsJsonArray());
      json.add("friends", this.getFriendsJsonArray());
      json.add("colorPickerPresets", this.getColorPickerPresetsJsonArray());
      json.add("password", this.getPassword());
      json.add("waypoints", this.getWaypointsJsonArray());
      json.addProperty("backgroundIndex", DefineVisuals.getInstance().getBackgroundManager().getCurrentIndex());
      ConfigFile currentConfig = DefineVisuals.getInstance().getConfigManager().getCurrent();
      if (currentConfig != null) {
         json.addProperty("lastConfig", currentConfig.getFileName());
      }

      try (FileWriter writer = new FileWriter(this.file)) {
         writer.write(FileManager.GSON.toJson(json));
      } catch (Exception var8) {
         var8.printStackTrace();
      }
   }

   @Override
   public void read() {
      try (FileReader reader = new FileReader(this.getFile())) {
         JsonObject object = (JsonObject)FileManager.GSON.fromJson(reader, JsonObject.class);
         if (object.has("username")) {
            String username = object.get("username").getAsString();
            new Session(username, UUID.randomUUID(), "", Optional.empty(), Optional.empty(), AccountType.MOJANG);
         }

         if (object.has("password")) {
            this.loadPass(object.getAsJsonArray("password"));
         }

         if (object.has("swing")) {
            String swing = object.get("swing").getAsString();
            SwingManager swingManager = DefineVisuals.getInstance().getSwingManager();
            SwingPresetManager manager = DefineVisuals.getInstance().getSwingPresetManager();

            for (SwingPreset value : DefineVisuals.getInstance().getSwingManager().getPresets()) {
               if (value.getName().equals(swing)) {
                  swingManager.getBezier().start(value.getBezierStart()).end(value.getBezierEnd());
                  swingManager.getBack().enabled(value.isSwingBack());
                  swingManager.getSpeed().setCurrentValue(value.getSpeed());
                  SwingPhase start = swingManager.getStartPhase();
                  start.getAnchorX().setCurrentValue(value.getFrom().anchorX());
                  start.getAnchorY().setCurrentValue(value.getFrom().anchorY());
                  start.getAnchorZ().setCurrentValue(value.getFrom().anchorZ());
                  start.getMoveX().setCurrentValue(value.getFrom().moveX());
                  start.getMoveY().setCurrentValue(value.getFrom().moveY());
                  start.getMoveZ().setCurrentValue(value.getFrom().moveZ());
                  start.getRotateX().setCurrentValue(value.getFrom().rotateX());
                  start.getRotateY().setCurrentValue(value.getFrom().rotateY());
                  start.getRotateZ().setCurrentValue(value.getFrom().rotateZ());
                  SwingPhase end = swingManager.getEndPhase();
                  end.getAnchorX().setCurrentValue(value.getTo().anchorX());
                  end.getAnchorY().setCurrentValue(value.getTo().anchorY());
                  end.getAnchorZ().setCurrentValue(value.getTo().anchorZ());
                  end.getMoveX().setCurrentValue(value.getTo().moveX());
                  end.getMoveY().setCurrentValue(value.getTo().moveY());
                  end.getMoveZ().setCurrentValue(value.getTo().moveZ());
                  end.getRotateX().setCurrentValue(value.getTo().rotateX());
                  end.getRotateY().setCurrentValue(value.getTo().rotateY());
                  end.getRotateZ().setCurrentValue(value.getTo().rotateZ());
                  swingManager.setCurrent(swing);
               }
            }
         }

         if (object.has("theme")) {
            String themeName = object.get("theme").getAsString();

            try {
               Theme theme = Theme.valueOf(themeName);
               DefineVisuals.getInstance().getThemeManager().setCurrentTheme(theme);
            } catch (IllegalArgumentException var16) {
               DefineVisuals.getInstance().getThemeManager().setCurrentTheme(Theme.DARK);
            }
         }

         if (object.has("themes") && object.get("themes").isJsonObject()) {
            this.loadThemes(object.getAsJsonObject("themes"));
         }

         if (object.has("friends")) {
            JsonArray friendsArray = object.getAsJsonArray("friends");
            DefineVisuals.getInstance().getFriendManager().clear();

            for (JsonElement friendElement : friendsArray) {
               DefineVisuals.getInstance().getFriendManager().add(friendElement.getAsString());
            }
         }

         if (object.has("waypoints")) {
            this.loadWaypoints(object.getAsJsonArray("waypoints"));
         }

         if (object.has("colorPickerPresets")) {
            this.loadColorPickerPresets(object.getAsJsonArray("colorPickerPresets"));
         }

         if (object.has("hudElements")) {
            for (JsonElement elemObj : object.getAsJsonArray("hudElements")) {
               JsonObject elementObject = elemObj.getAsJsonObject();
               String name = elementObject.get("name").getAsString();
               float x = elementObject.get("x").getAsFloat();
               float y = elementObject.get("y").getAsFloat();
               boolean showing = elementObject.get("showing").getAsBoolean();
               HudElement element = DefineVisuals.getInstance().getHud().getElementByName(name);
               if (element != null) {
                  element.setX(x);
                  element.setY(y);
                  element.setShowing(showing);
                  if (elementObject.has("settings")) {
                     JsonObject settingsObject = elementObject.getAsJsonObject("settings");

                     for (Setting setting : element.getSettings()) {
                        if (settingsObject.has(setting.getName())) {
                           setting.load(settingsObject.get(setting.getName()));
                        }
                     }
                  }
               }
            }
         }

         if (object.has("lastConfig")) {
            String configName = object.get("lastConfig").getAsString();
            ConfigFile config = DefineVisuals.getInstance().getConfigManager().getConfig(configName);
            if (config != null) {
               config.load();
            }
         }

         if (object.has("backgroundIndex")) {
            int backgroundIndex = object.get("backgroundIndex").getAsInt();
            DefineVisuals.getInstance().getBackgroundManager().setBackground(backgroundIndex);
         }
      } catch (Exception var18) {
         var18.printStackTrace();
      }
   }

   private JsonArray getHudElementsJsonArray() {
      JsonArray hudElementsArray = new JsonArray();

      for (HudElement element : DefineVisuals.getInstance().getHud().getElements()) {
         JsonObject elementObject = new JsonObject();
         elementObject.addProperty("name", element.getName());
         elementObject.addProperty("x", element.getX());
         elementObject.addProperty("y", element.getY());
         elementObject.addProperty("showing", element.isShowing());
         elementObject.add("settings", this.getSettingsJsonObject(element));
         hudElementsArray.add(elementObject);
      }

      return hudElementsArray;
   }

   private JsonObject getSettingsJsonObject(HudElement element) {
      JsonObject settingsObject = new JsonObject();

      for (Setting setting : element.getSettings()) {
         settingsObject.add(setting.getName(), setting.save());
      }

      return settingsObject;
   }

   private JsonArray getFriendsJsonArray() {
      JsonArray friendsJsonArray = new JsonArray();

      for (String friendsName : DefineVisuals.getInstance().getFriendManager().listFriends()) {
         friendsJsonArray.add(friendsName);
      }

      return friendsJsonArray;
   }

   private JsonArray getWaypointsJsonArray() {
      JsonArray waypointsJsonArray = new JsonArray();

      for (java.util.Map.Entry<String, net.minecraft.util.math.Vec3d> entry : DefineVisuals.getInstance().getWayPointsManager().getEntries()) {
         JsonObject waypointObject = new JsonObject();
         waypointObject.addProperty("name", entry.getKey());
         waypointObject.addProperty("x", entry.getValue().x);
         waypointObject.addProperty("y", entry.getValue().y);
         waypointObject.addProperty("z", entry.getValue().z);
         waypointsJsonArray.add(waypointObject);
      }

      return waypointsJsonArray;
   }

   private JsonArray getPassword() {
      JsonArray passwordJsonArray = new JsonArray();

      for (Entry<String, String> pass : DefineVisuals.getInstance().getModuleManager().getModule(AutoAuth.class).listPassword().entrySet()) {
         JsonObject passObject = new JsonObject();
         passObject.addProperty("nick", pass.getValue());
         passObject.addProperty("pass", pass.getKey());
         passwordJsonArray.add(passObject);
      }

      return passwordJsonArray;
   }

   private JsonArray getColorPickerPresetsJsonArray() {
      JsonArray presetsArray = new JsonArray();

      for (ColorPicker.Preset preset : ColorPicker.COLOR_PRESETS) {
         if (preset.isShowing()) {
            JsonObject presetObject = new JsonObject();
            ColorRGBA color = preset.getColor();
            presetObject.addProperty("red", color.getRed());
            presetObject.addProperty("green", color.getGreen());
            presetObject.addProperty("blue", color.getBlue());
            presetObject.addProperty("alpha", color.getAlpha());
            presetsArray.add(presetObject);
         }
      }

      return presetsArray;
   }

   private JsonObject getThemesJsonObject() {
      JsonObject themes = new JsonObject();

      for (Theme theme : Theme.values()) {
         JsonObject themeObject = new JsonObject();
         themeObject.add("text", this.colorToJson(theme.getTextColor()));
         themeObject.add("background", this.colorToJson(theme.getBackgroundColor()));
         themeObject.add("additional", this.colorToJson(theme.getAdditionalColor()));
         themeObject.add("outline", this.colorToJson(theme.getOutlineColor()));
         themeObject.add("flat", this.colorToJson(theme.getFlatColor()));
         themeObject.add("accent", this.colorToJson(theme.getAccentColor()));
         themeObject.add("icons", this.colorToJson(theme.getIconsColor()));
         themeObject.add("enabledModules", this.colorToJson(theme.getEnabledModulesColor()));
         themes.add(theme.name(), themeObject);
      }

      return themes;
   }

   private JsonObject colorToJson(ColorRGBA color) {
      JsonObject object = new JsonObject();
      object.addProperty("red", color.getRed());
      object.addProperty("green", color.getGreen());
      object.addProperty("blue", color.getBlue());
      object.addProperty("alpha", color.getAlpha());
      return object;
   }

   private void loadThemes(JsonObject themesObject) {
      for (Theme theme : Theme.values()) {
         if (!themesObject.has(theme.name()) || !themesObject.get(theme.name()).isJsonObject()) {
            continue;
         }

         JsonObject themeObject = themesObject.getAsJsonObject(theme.name());
         theme.setTextColor(this.readColor(themeObject, "text", theme.getTextColor()));
         theme.setBackgroundColor(this.readColor(themeObject, "background", theme.getBackgroundColor()));
         theme.setAdditionalColor(this.readColor(themeObject, "additional", theme.getAdditionalColor()));
         theme.setOutlineColor(this.readColor(themeObject, "outline", theme.getOutlineColor()));
         theme.setFlatColor(this.readColor(themeObject, "flat", theme.getFlatColor()));
         theme.setAccentColor(this.readColor(themeObject, "accent", theme.getAccentColor()));
         theme.setIconsColor(this.readColor(themeObject, "icons", theme.getIconsColor()));
         theme.setEnabledModulesColor(this.readColor(themeObject, "enabledModules", theme.getEnabledModulesColor()));
      }
   }

   private ColorRGBA readColor(JsonObject themeObject, String name, ColorRGBA fallback) {
      if (!themeObject.has(name) || !themeObject.get(name).isJsonObject()) {
         return fallback;
      }

      JsonObject colorObject = themeObject.getAsJsonObject(name);
      if (!colorObject.has("red") || !colorObject.has("green") || !colorObject.has("blue")) {
         return fallback;
      }

      float alpha = colorObject.has("alpha") ? colorObject.get("alpha").getAsFloat() : fallback.getAlpha();
      return new ColorRGBA(
         colorObject.get("red").getAsFloat(),
         colorObject.get("green").getAsFloat(),
         colorObject.get("blue").getAsFloat(),
         alpha
      );
   }

   private void loadColorPickerPresets(JsonArray presetsArray) {
      List<ColorPicker.Preset> loadedPresets = new ArrayList<>();

      for (JsonElement presetElement : presetsArray) {
         JsonObject presetObject = presetElement.getAsJsonObject();
         float red = presetObject.get("red").getAsFloat();
         float green = presetObject.get("green").getAsFloat();
         float blue = presetObject.get("blue").getAsFloat();
         float alpha = presetObject.get("alpha").getAsFloat();
         ColorRGBA color = new ColorRGBA(red, green, blue, alpha);
         loadedPresets.add(new ColorPicker.Preset(color));
      }

      ColorPicker.setColorPresets(loadedPresets);
   }

   private void loadPass(JsonArray password) {
      for (JsonElement passElement : password) {
         JsonObject passObject = passElement.getAsJsonObject();
         String nick = passObject.get("nick").getAsString();
         String pass = passObject.get("pass").getAsString();
         DefineVisuals.getInstance().getModuleManager().getModule(AutoAuth.class).put(nick, pass);
      }
   }

   private void loadWaypoints(JsonArray waypointsArray) {
      DefineVisuals.getInstance().getWayPointsManager().clear();

      for (JsonElement waypointElement : waypointsArray) {
         JsonObject waypointObject = waypointElement.getAsJsonObject();
         String name = waypointObject.get("name").getAsString();
         double x = waypointObject.get("x").getAsDouble();
         double y = waypointObject.get("y").getAsDouble();
         double z = waypointObject.get("z").getAsDouble();
         DefineVisuals.getInstance().getWayPointsManager().add(name, (int)x, (int)y, (int)z);
      }
   }
}
