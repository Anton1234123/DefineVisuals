package moscow.definevisuals.utility.game.cursor;

public class CursorUtility {
    private static CursorType currentType = CursorType.DEFAULT;
    private static CursorType prev = CursorType.DEFAULT;

    public static CursorType getCurrentType() {
        return currentType;
    }

    public static void set(CursorType type) {
        currentType = type;
    }

    public static CursorType getPrev() {
        return prev;
    }

    public static void setPrev(CursorType prev) {
        CursorUtility.prev = prev;
    }
}
