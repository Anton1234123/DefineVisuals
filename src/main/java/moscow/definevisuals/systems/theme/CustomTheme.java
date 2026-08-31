package moscow.definevisuals.systems.theme;

import moscow.definevisuals.utility.colors.ColorRGBA;

public interface CustomTheme {
   ColorRGBA getTextColor();

   ColorRGBA getBackgroundColor();

   ColorRGBA getAdditionalColor();

   ColorRGBA getOutlineColor();

   ColorRGBA getFlatColor();

   ColorRGBA getAccentColor();

   ColorRGBA getIconsColor();

   ColorRGBA getEnabledModulesColor();

   boolean isDark();

   boolean isSeparators();

   float getBlurStrength();

   float getGlassOpacity();

   float getMinimalismOpacity();

   float getEnabledOffset();

   float getHudRounding();

   float getBlurOffset();

   float getHudAlpha();

   float getDisableAlphaGlass();

   float getGlassPower();

   float getGlassStrength();

   float getPadding();

   float getSplitters();

   float getAlbumColor();
}
