package moscow.definevisuals.framework.objects.gradient.impl;

import moscow.definevisuals.framework.objects.gradient.Gradient;
import moscow.definevisuals.utility.colors.ColorRGBA;

public class VerticalGradient extends Gradient {
   public VerticalGradient(ColorRGBA startColor, ColorRGBA endColor) {
      super(startColor, endColor, startColor, endColor);
   }

   public VerticalGradient rotate() {
      return new VerticalGradient(this.bottomRightColor, this.topLeftColor);
   }
}
