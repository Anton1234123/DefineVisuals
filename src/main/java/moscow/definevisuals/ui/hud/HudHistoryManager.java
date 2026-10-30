/*
 * Decompiled with CFR 0.152.
 */
package moscow.definevisuals.ui.hud;

import java.util.Stack;
import moscow.definevisuals.ui.hud.HudElement;

public class HudHistoryManager {
    private final Stack<MoveAction> undoStack = new Stack();
    private final Stack<MoveAction> redoStack = new Stack();

    public void registerMove(HudElement element, float fromX, float fromY, float toX, float toY) {
        this.undoStack.push(new MoveAction(element, fromX, fromY, toX, toY));
        this.redoStack.clear();
    }

    public boolean undo() {
        if (this.undoStack.isEmpty()) {
            return false;
        }
        MoveAction lastAction = this.undoStack.pop();
        lastAction.element().pos(lastAction.fromX(), lastAction.fromY());
        this.redoStack.push(lastAction);
        return true;
    }

    public boolean redo() {
        if (this.redoStack.isEmpty()) {
            return false;
        }
        MoveAction redoAction = this.redoStack.pop();
        redoAction.element().pos(redoAction.toX(), redoAction.toY());
        this.undoStack.push(redoAction);
        return true;
    }

    private record MoveAction(HudElement element, float fromX, float fromY, float toX, float toY) {
    }
}
