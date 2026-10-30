package moscow.definevisuals.framework.style;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.CustomDrawContext;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.framework.objects.gradient.impl.VerticalGradient;
import moscow.definevisuals.systems.modules.modules.visuals.Interface;
import moscow.definevisuals.systems.theme.Theme;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;

/**
 * Shared painter for the visual Interface modes.
 *
 * <p>Every mode owns an independent animation weight. Drawing the weighted
 * layers here keeps transitions smooth and prevents ClickGUI, HUD and popup
 * surfaces from slowly drifting into different styles.</p>
 */
public final class InterfaceStyleRenderer {
    private static final float EPSILON = 0.001f;

    private InterfaceStyleRenderer() {
    }

    public static void drawSurface(
        CustomDrawContext context,
        float x,
        float y,
        float width,
        float height,
        float squirt,
        BorderRadius radius,
        float alpha,
        float dragAnimation
    ) {
        drawSurface(context, x, y, width, height, squirt, radius, alpha, dragAnimation, ColorRGBA.WHITE);
    }

    public static void drawSurface(
        CustomDrawContext context,
        float x,
        float y,
        float width,
        float height,
        float squirt,
        BorderRadius radius,
        float alpha,
        float dragAnimation,
        ColorRGBA liquidTint
    ) {
        if (alpha <= EPSILON || width <= 0.0f || height <= 0.0f) {
            return;
        }

        boolean dark = DefineVisuals.getInstance().getThemeManager().getCurrentTheme() == Theme.DARK;
        Interface.StyleWeights weights = Interface.styleWeights();
        float liquid = weights.liquidGlass();
        float minimal = weights.minimalism();
        float modern = weights.modernMinimalism();
        float blurGlass = weights.blurGlass();

        if (liquid > EPSILON) {
            context.drawLiquidGlass(
                x,
                y,
                width,
                height,
                squirt,
                0.08f - 0.07f * dragAnimation,
                radius,
                liquidTint.withAlpha(255.0f * alpha * liquid)
            );
            context.drawSquircle(
                x,
                y,
                width,
                height,
                squirt,
                radius,
                Colors.getBackgroundColor().withAlpha(255.0f * alpha * (dark ? 0.22f : 0.58f) * liquid)
            );
        }

        if (blurGlass > EPSILON) {
            context.drawBlurredRect(
                x,
                y,
                width,
                height,
                45.0f,
                squirt,
                radius,
                ColorRGBA.WHITE.withAlpha(255.0f * alpha * blurGlass)
            );

            ColorRGBA glassTop = (dark
                ? Colors.getBackgroundColor().mix(Colors.getAdditionalColor(), 0.35f)
                : ColorRGBA.WHITE.mix(Colors.getAdditionalColor(), 0.18f))
                .withAlpha(255.0f * alpha * (dark ? 0.52f : 0.42f) * blurGlass);
            ColorRGBA glassBottom = (dark
                ? ColorRGBA.BLACK.mix(Colors.getAdditionalColor(), 0.18f)
                : Colors.getAdditionalColor().mix(ColorRGBA.WHITE, 0.40f))
                .withAlpha(255.0f * alpha * (dark ? 0.38f : 0.34f) * blurGlass);
            context.drawRoundedRect(x, y, width, height, radius, new VerticalGradient(glassTop, glassBottom));
        }

        if (modern > EPSILON) {
            ColorRGBA modernTop = Colors.getAdditionalColor()
                .mix(Colors.getTextColor(), dark ? 0.10f : 0.05f)
                .withAlpha(255.0f * alpha * modern);
            ColorRGBA modernBottom = Colors.getBackgroundColor()
                .mix(Colors.getAdditionalColor(), dark ? 0.10f : 0.0f)
                .withAlpha(255.0f * alpha * modern);
            context.drawRoundedRect(x, y, width, height, radius, new VerticalGradient(modernTop, modernBottom));
            context.drawRoundedBorder(
                x,
                y,
                width,
                height,
                0.8f,
                radius,
                Colors.getAccent().withAlpha(255.0f * alpha * 0.20f * modern)
            );
        }

        if (minimal > EPSILON) {
            ColorRGBA blackTop = new ColorRGBA(10.0f, 10.0f, 12.0f)
                .withAlpha(255.0f * alpha * 0.97f * minimal);
            ColorRGBA blackBottom = ColorRGBA.BLACK
                .withAlpha(255.0f * alpha * 0.99f * minimal);
            context.drawRoundedRect(x, y, width, height, radius, new VerticalGradient(blackTop, blackBottom));
            context.drawRoundedBorder(
                x,
                y,
                width,
                height,
                0.7f,
                radius,
                ColorRGBA.WHITE.withAlpha(255.0f * alpha * 0.08f * minimal)
            );
        }
    }

    public static void drawChip(
        CustomDrawContext context,
        float x,
        float y,
        float width,
        float height,
        BorderRadius radius,
        float alpha,
        float accentStrength
    ) {
        if (alpha <= EPSILON) {
            return;
        }

        boolean dark = DefineVisuals.getInstance().getThemeManager().getCurrentTheme() == Theme.DARK;
        ColorRGBA accent = Colors.getAccent();
        ColorRGBA surface = Colors.getAdditionalColor().mix(accent, accentStrength);
        Interface.StyleWeights weights = Interface.styleWeights();
        float liquid = weights.liquidGlass();
        float minimal = weights.minimalism();
        float modern = weights.modernMinimalism();
        float blurGlass = weights.blurGlass();

        if (liquid > EPSILON) {
            ColorRGBA liquidColor = surface.mix(Colors.getBackgroundColor(), dark ? 0.28f : 0.12f)
                .withAlpha(255.0f * alpha * 0.60f * liquid);
            context.drawLiquidGlass(x, y, width, height, 2.0f, 0.08f, radius, liquidColor);
            context.drawRoundedBorder(x, y, width, height, 0.7f, radius, accent.withAlpha(255.0f * alpha * 0.24f * liquid));
        }

        if (blurGlass > EPSILON) {
            context.drawBlurredRect(x, y, width, height, 32.0f, 2.0f, radius, ColorRGBA.WHITE.withAlpha(210.0f * alpha * blurGlass));
            ColorRGBA glassColor = surface.mix(Colors.getBackgroundColor(), dark ? 0.50f : 0.18f)
                .withAlpha(255.0f * alpha * (dark ? 0.45f : 0.35f) * blurGlass);
            context.drawRoundedRect(x, y, width, height, radius, glassColor);
        }

        if (modern > EPSILON) {
            ColorRGBA topTint = dark ? Colors.getTextColor() : Colors.getBackgroundColor();
            ColorRGBA bottomTint = dark ? Colors.getBackgroundColor() : Colors.getTextColor();
            ColorRGBA flatTop = surface.mix(topTint, dark ? 0.22f : 0.25f)
                .withAlpha(255.0f * alpha * (dark ? 0.78f : 0.65f) * modern);
            ColorRGBA flatBottom = surface.mix(bottomTint, dark ? 0.40f : 0.10f)
                .withAlpha(255.0f * alpha * (dark ? 0.52f : 0.45f) * modern);
            context.drawRoundedRect(x, y, width, height, radius, new VerticalGradient(flatTop, flatBottom));
            context.drawRoundedBorder(x, y, width, height, 0.7f, radius, accent.withAlpha(255.0f * alpha * 0.26f * modern));
        }

        if (minimal > EPSILON) {
            ColorRGBA top = new ColorRGBA(18.0f, 18.0f, 20.0f).withAlpha(245.0f * alpha * minimal);
            ColorRGBA bottom = new ColorRGBA(3.0f, 3.0f, 4.0f).withAlpha(250.0f * alpha * minimal);
            context.drawRoundedRect(x, y, width, height, radius, new VerticalGradient(top, bottom));
            context.drawRoundedBorder(x, y, width, height, 0.7f, radius, ColorRGBA.WHITE.withAlpha(18.0f * alpha * minimal));
        }
    }

    public static void drawCard(
        CustomDrawContext context,
        float x,
        float y,
        float width,
        float height,
        float squirt,
        BorderRadius radius,
        float alpha,
        float selected,
        float hovered
    ) {
        boolean dark = DefineVisuals.getInstance().getThemeManager().getCurrentTheme() == Theme.DARK;
        ColorRGBA accent = Colors.getAccent();
        Interface.StyleWeights weights = Interface.styleWeights();
        float liquid = weights.liquidGlass();
        float minimal = weights.minimalism();
        float modern = weights.modernMinimalism();
        float blurGlass = weights.blurGlass();

        if (liquid > EPSILON) {
            ColorRGBA base = (dark ? Colors.getBackgroundColor() : Colors.getAdditionalColor())
                .withAlpha(255.0f * (dark ? 0.16f : 0.12f));
            ColorRGBA card = base
                .mix(accent.withAlpha(255.0f * 0.25f), selected * 0.9f)
                .mix(Colors.getTextColor().withAlpha(20.0f), hovered * 0.35f)
                .mulAlpha(alpha * liquid);
            context.drawSquircle(x, y, width, height, squirt, radius, card);
        }

        if (blurGlass > EPSILON) {
            context.drawBlurredRect(x, y, width, height, 24.0f, squirt, radius, ColorRGBA.WHITE.withAlpha(180.0f * alpha * blurGlass));
            ColorRGBA base = (dark ? Colors.getBackgroundColor() : ColorRGBA.WHITE)
                .mix(Colors.getAdditionalColor(), dark ? 0.30f : 0.16f)
                .withAlpha(255.0f * (dark ? 0.38f : 0.30f));
            ColorRGBA card = base
                .mix(accent.withAlpha(120.0f), selected * 0.72f)
                .mix(Colors.getTextColor().withAlpha(45.0f), hovered * 0.30f)
                .mulAlpha(alpha * blurGlass);
            context.drawSquircle(x, y, width, height, squirt, radius, card);
        }

        if (modern > EPSILON) {
            ColorRGBA base = (dark ? Colors.getBackgroundColor() : Colors.getAdditionalColor())
                .withAlpha(255.0f * (dark ? 0.30f : 0.24f));
            ColorRGBA card = base
                .mix(accent.withAlpha(255.0f * 0.40f), selected * 0.9f)
                .mix(Colors.getTextColor().withAlpha(20.0f), hovered * 0.35f)
                .mulAlpha(alpha * modern);
            context.drawSquircle(x, y, width, height, squirt, radius, card);
        }

        if (minimal > EPSILON) {
            ColorRGBA card = new ColorRGBA(6.0f, 6.0f, 8.0f, 228.0f)
                .mix(new ColorRGBA(26.0f, 26.0f, 30.0f, 238.0f), hovered * 0.55f)
                .mix(accent.withAlpha(105.0f), selected * 0.22f)
                .mulAlpha(alpha * minimal);
            context.drawSquircle(x, y, width, height, squirt, radius, card);
            context.drawRoundedBorder(x, y, width, height, 0.55f, radius, ColorRGBA.WHITE.withAlpha(16.0f * alpha * minimal));
        }
    }
}
