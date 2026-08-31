package moscow.definevisuals.systems.theme;

import lombok.Generated;
import moscow.definevisuals.utility.colors.ColorRGBA;

public enum Theme implements CustomTheme {
   DARK(
      new ColorRGBA(255.0F, 255.0F, 255.0F),
      new ColorRGBA(12.0F, 12.0F, 12.0F),
      new ColorRGBA(24.0F, 24.0F, 27.0F),
      new ColorRGBA(32.0F, 32.0F, 32.0F),
      ColorRGBA.BLACK
   ),
   LIGHT(
      new ColorRGBA(10.0F, 10.0F, 10.0F),
      new ColorRGBA(229.0F, 229.0F, 229.0F),
      new ColorRGBA(255.0F, 255.0F, 255.0F),
      new ColorRGBA(32.0F, 32.0F, 32.0F),
      ColorRGBA.WHITE
   );

   private ColorRGBA textColor;
   private ColorRGBA backgroundColor;
   private ColorRGBA additionalColor;
   private ColorRGBA outlineColor;
   private ColorRGBA flatColor;
   private ColorRGBA accentColor = new ColorRGBA(151.0F, 71.0F, 255.0F);
   private ColorRGBA iconsColor = ColorRGBA.WHITE;
   private ColorRGBA enabledModulesColor = ColorRGBA.WHITE;
   private final boolean separators = true;
   private final float blurStrength = 4.0F;
   private final float glassOpacity = 20.0F;
   private final float minimalismOpacity = 80.0F;
   private final float enabledOffset = 2.0F;
   private final float hudRounding = 7.0F;
   private final float blurOffset = 0.5F;
   private final float hudAlpha = 0.8F;
   private final float disableAlphaGlass = 0.2F;
   private final float glassPower = 25.0F;
   private final float glassStrength = 0.08F;
   private final float padding = 2.0F;
   private final float splitters = 1.0F;
   private final float albumColor = 1.0F;

   @Generated
   public ColorRGBA getTextColor() {
      return this.textColor;
   }

   public void setTextColor(ColorRGBA textColor) {
      this.textColor = textColor;
   }

   @Generated
   public ColorRGBA getBackgroundColor() {
      return this.backgroundColor;
   }

   public void setBackgroundColor(ColorRGBA backgroundColor) {
      this.backgroundColor = backgroundColor;
   }

   @Generated
   public ColorRGBA getAdditionalColor() {
      return this.additionalColor;
   }

   public void setAdditionalColor(ColorRGBA additionalColor) {
      this.additionalColor = additionalColor;
   }

   @Generated
   public ColorRGBA getOutlineColor() {
      return this.outlineColor;
   }

   public void setOutlineColor(ColorRGBA outlineColor) {
      this.outlineColor = outlineColor;
   }

   @Generated
   public ColorRGBA getFlatColor() {
      return this.flatColor;
   }

   public void setFlatColor(ColorRGBA flatColor) {
      this.flatColor = flatColor;
   }

   public ColorRGBA getAccentColor() {
      return this.accentColor;
   }

   public void setAccentColor(ColorRGBA accentColor) {
      this.accentColor = accentColor;
   }

   public ColorRGBA getIconsColor() {
      return this.iconsColor;
   }

   public void setIconsColor(ColorRGBA iconsColor) {
      this.iconsColor = iconsColor;
   }

   public ColorRGBA getEnabledModulesColor() {
      return this.enabledModulesColor;
   }

   public void setEnabledModulesColor(ColorRGBA enabledModulesColor) {
      this.enabledModulesColor = enabledModulesColor;
   }

   public boolean isDark() {
      return this == DARK;
   }

   public boolean isSeparators() {
      return this.separators;
   }

   public float getBlurStrength() {
      return this.blurStrength;
   }

   public float getGlassOpacity() {
      return this.glassOpacity;
   }

   public float getMinimalismOpacity() {
      return this.minimalismOpacity;
   }

   public float getEnabledOffset() {
      return this.enabledOffset;
   }

   public float getHudRounding() {
      return this.hudRounding;
   }

   public float getBlurOffset() {
      return this.blurOffset;
   }

   public float getHudAlpha() {
      return this.hudAlpha;
   }

   public float getDisableAlphaGlass() {
      return this.disableAlphaGlass;
   }

   public float getGlassPower() {
      return this.glassPower;
   }

   public float getGlassStrength() {
      return this.glassStrength;
   }

   public float getPadding() {
      return this.padding;
   }

   public float getSplitters() {
      return this.splitters;
   }

   public float getAlbumColor() {
      return this.albumColor;
   }

   @Generated
   private Theme(
      final ColorRGBA textColor, final ColorRGBA backgroundColor, final ColorRGBA additionalColor, final ColorRGBA outlineColor, final ColorRGBA flatColor
   ) {
      this.textColor = textColor;
      this.backgroundColor = backgroundColor;
      this.additionalColor = additionalColor;
      this.outlineColor = outlineColor;
      this.flatColor = flatColor;
   }
}
