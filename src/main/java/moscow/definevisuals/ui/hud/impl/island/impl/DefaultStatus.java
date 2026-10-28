/*
 * Decompiled with CFR 0.152.
 */
package moscow.definevisuals.ui.hud.impl.island.impl;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.CustomDrawContext;
import moscow.definevisuals.framework.msdf.Fonts;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.systems.setting.settings.SelectSetting;
import moscow.definevisuals.ui.hud.impl.island.DynamicIsland;
import moscow.definevisuals.ui.hud.impl.island.IslandStatus;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;

public class DefaultStatus
extends IslandStatus {
    public DefaultStatus(SelectSetting setting) {
        super(setting, "default");
    }

    @Override
    public void draw(CustomDrawContext context) {
        DynamicIsland island = DefineVisuals.getInstance().getHud().getIsland();
        float x = sr.getScaledWidth() / 2.0f - island.getSize().width / 2.0f;
        float y = 7.0f;
        float width = this.size.width = 20.0f + Fonts.MEDIUM.getFont(7.0f).width("Define");
        this.size.height = 15.0f;
        float height = 15.0f;
        context.drawRoundedRect(x - 6.0f + 10.0f * this.animation.getValue(), y + 4.0f, 7.0f, 7.0f, BorderRadius.all(3.0f), new ColorRGBA(115.0f, 0.0f, 255.0f));
        context.drawText(Fonts.MEDIUM.getFont(7.0f), "Define", x + 25.0f - 10.0f * this.animation.getValue(), y + 5.0f, Colors.getTextColor());
    }

    @Override
    public boolean canShow() {
        return true;
    }
}

