package moscow.definevisuals.utility.game.cursor;

import org.lwjgl.glfw.GLFW;

public enum CursorType {
    DEFAULT(GLFW.GLFW_ARROW_CURSOR),
    HAND(GLFW.GLFW_HAND_CURSOR),
    IBEAM(GLFW.GLFW_IBEAM_CURSOR),
    TEXT(GLFW.GLFW_IBEAM_CURSOR),
    CROSSHAIR(GLFW.GLFW_CROSSHAIR_CURSOR),
    HRESIZE(GLFW.GLFW_HRESIZE_CURSOR),
    ARROW_HORIZONTAL(GLFW.GLFW_HRESIZE_CURSOR),
    VRESIZE(GLFW.GLFW_VRESIZE_CURSOR),
    ARROW_VERTICAL(GLFW.GLFW_VRESIZE_CURSOR);

    private final int shape;
    private long cursorHandle;

    CursorType(int shape) {
        this.shape = shape;
        this.cursorHandle = 0;
    }

    public int getShape() {
        return shape;
    }

    public long getCursorHandle() {
        if (cursorHandle == 0) {
            cursorHandle = GLFW.glfwCreateStandardCursor(shape);
        }
        return cursorHandle;
    }
}
