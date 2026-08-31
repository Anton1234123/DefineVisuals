package moscow.definevisuals.systems.background;

import java.util.Arrays;
import java.util.List;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;

public class BackgroundManager {
    private static final List<String> BACKGROUNDS = Arrays.asList(
            "image/mainmenu/day2.png",
            "image/mainmenu/day1.png",
            "image/mainmenu/day3.png",
            "image/mainmenu/day4.png",
            "image/mainmenu/day5.png",
            "image/mainmenu/day6.png",
            "image/mainmenu/night1.png",
            "image/mainmenu/night2.png",
            "image/mainmenu/night3.png",
            "image/mainmenu/background.png"
    );

    private int currentIndex = 0;
    private int previousIndex = 0;
    private final Animation transitionAnim = new Animation(500L, 1.0f, Easing.FIGMA_EASE_IN_OUT);

    public String getCurrentBackground() {
        if (this.currentIndex < 0 || this.currentIndex >= BACKGROUNDS.size()) {
            this.currentIndex = 0;
        }
        return BACKGROUNDS.get(this.currentIndex);
    }

    public String getPreviousBackground() {
        if (this.previousIndex < 0 || this.previousIndex >= BACKGROUNDS.size()) {
            this.previousIndex = 0;
        }
        return BACKGROUNDS.get(this.previousIndex);
    }

    public void nextBackground() {
        this.previousIndex = this.currentIndex;
        this.currentIndex = (this.currentIndex + 1) % BACKGROUNDS.size();
        this.transitionAnim.reset();
        this.save();
    }

    public void previousBackground() {
        this.previousIndex = this.currentIndex;
        this.currentIndex = (this.currentIndex - 1 + BACKGROUNDS.size()) % BACKGROUNDS.size();
        this.transitionAnim.reset();
        this.save();
    }

    public void setBackground(int index) {
        if (index >= 0 && index < BACKGROUNDS.size() && index != this.currentIndex) {
            this.previousIndex = this.currentIndex;
            this.currentIndex = index;
            this.transitionAnim.reset();
            this.save();
        }
    }

    public int getCurrentIndex() {
        return this.currentIndex;
    }

    public List<String> getBackgrounds() {
        return BACKGROUNDS;
    }

    public Animation getTransitionAnim() {
        return this.transitionAnim;
    }

    public void save() {
        try {
            DefineVisuals.getInstance().getFileManager().writeFile("client");
        } catch (Exception ignored) {
        }
    }
}