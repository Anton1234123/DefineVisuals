/*
 * Decompiled with CFR 0.152.
 */
package moscow.definevisuals.ui.hud.impl.island;

import moscow.definevisuals.systems.setting.settings.SelectSetting;
import moscow.definevisuals.ui.hud.impl.island.IslandStatus;

public class ExtandableStatus
extends IslandStatus {
    public ExtandableStatus(SelectSetting setting, String name) {
        super(setting, name);
    }

    @Override
    public boolean canShow() {
        return false;
    }
}

