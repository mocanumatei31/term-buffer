package org.example;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class TerminalBuffer {
    private TerminalSize terminalSize;
    private int maxScrollback;

    private int cursorRow;
    private int cursorCol;

    private List<Line> screen;
    private Deque<Line> scrollback;

    public TerminalBuffer(int width, int height, int maxScrollback) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Dimensions must be positive");
        }

        if (maxScrollback < 0) {
            throw new IllegalArgumentException("Scrollback size cannot be negative");
        }

        this.terminalSize = new TerminalSize(width, height);
        this.maxScrollback = maxScrollback;

        this.cursorCol = this.cursorRow = 0;

        this.screen = new ArrayList<>(height);
        this.scrollback = new ArrayDeque<>(maxScrollback);

        for (int i = 0; i < height; i++) {
            screen.add(new Line(width));
        }
    }

}
