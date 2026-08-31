package moscow.definevisuals.systems.theme;

import lombok.Generated;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.utility.colors.ColorRGBA;

public class ThemeManager {
   private Theme currentTheme = Theme.DARK;

   public void switchTheme() {
      this.currentTheme = this.currentTheme == Theme.DARK ? Theme.LIGHT : Theme.DARK;
   }

   public Theme getCurrentTheme() {
      return this.currentTheme;
   }

   public ColorRGBA getTextColor() {
      return this.getCurrentTheme().getTextColor();
   }

   public ColorRGBA getBackgroundColor() {
      return this.getCurrentTheme().getBackgroundColor();
   }

   public ColorRGBA getAdditionalColor() {
      return this.getCurrentTheme().getAdditionalColor();
   }

   public ColorRGBA getOutlineColor() {
      return this.getCurrentTheme().getOutlineColor();
   }

   public ColorRGBA getFlatColor() {
      return this.getCurrentTheme().getFlatColor();
   }

   public ColorRGBA getAccentColor() {
      return this.getCurrentTheme().getAccentColor();
   }

   public ColorRGBA getIconsColor() {
      return this.getCurrentTheme().getIconsColor();
   }

   public ColorRGBA getEnabledModulesColor() {
      return this.getCurrentTheme().getEnabledModulesColor();
   }

   public void setTextColor(ColorRGBA color) {
      this.getCurrentTheme().setTextColor(color);
   }

   public void setBackgroundColor(ColorRGBA color) {
      this.getCurrentTheme().setBackgroundColor(color);
   }

   public void setAdditionalColor(ColorRGBA color) {
      this.getCurrentTheme().setAdditionalColor(color);
   }

   public void setAccentColor(ColorRGBA color) {
      this.getCurrentTheme().setAccentColor(color);
   }

   public void flushSave() {
      try {
         DefineVisuals.getInstance().getFileManager().writeFile("client");
      } catch (Exception ignored) {
      }
   }

   @Generated
   public void setCurrentTheme(Theme currentTheme) {
      this.currentTheme = currentTheme;
   }
}
