package moscow.definevisuals.systems.modules.modules.other;

import java.util.ArrayList;
import java.util.List;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.BooleanSetting;
import moscow.definevisuals.systems.setting.settings.ButtonSetting;
import moscow.definevisuals.ui.holyevents.EventListScreen;

@ModuleInfo(name = "HolyEvents", category = ModuleCategory.EVENTS, desc = "Список активных ивентов HolyWorld")
public class HolyEvents extends BaseModule {
    public final List<String> favorites = new ArrayList<>();

    public final ButtonSetting openMenu = new ButtonSetting(this, "Открыть список")
            .action(() -> mc.setScreen(new EventListScreen(this)));

    public final BooleanSetting showAnarchy120 = new BooleanSetting(this, "1.20 Анархия").enabled(true);
    public final BooleanSetting showAnarchy116 = new BooleanSetting(this, "1.16 Анархия").enabled(true);
    public final BooleanSetting showSoloLight = new BooleanSetting(this, "Соло Лайт (1-12)").enabled(true);
    public final BooleanSetting showDuoLight = new BooleanSetting(this, "Дуо Лайт (13-26)").enabled(true);
    public final BooleanSetting showTrioLight = new BooleanSetting(this, "Трио Лайт (27-39)").enabled(true);
    public final BooleanSetting showClanLight = new BooleanSetting(this, "Клан Лайт (40-51)").enabled(true);

    public HolyEvents() {
    }

    public boolean isFavorite(String eventName) {
        return favorites.contains(eventName);
    }

    public void toggleFavorite(String eventName) {
        if (favorites.contains(eventName)) {
            favorites.remove(eventName);
        } else {
            favorites.add(eventName);
        }
    }
}
