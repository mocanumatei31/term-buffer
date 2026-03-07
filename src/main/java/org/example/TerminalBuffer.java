package org.example;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class TerminalBuffer {
    private TerminalSize terminalSize;
    private int maxScrollback;
    private CellStyle currentStyle;

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

        this.currentStyle = CellStyle.defaultStyle();
    }

    public int getCursorRow() {
        return cursorRow;
    }

    public int getCursorCol() {
        return cursorCol;
    }

    public void setCursorRow(int cursorRow) {
        if(cursorRow < 0 || cursorRow >= terminalSize.getHeight()) {
            throw new IllegalArgumentException("Cursor row must be within the bounds of the terminal");
        }
        this.cursorRow = cursorRow;
    }

    public void setCursorCol(int cursorCol) {
        if(cursorCol < 0 || cursorCol >= terminalSize.getWidth()) {
            throw new IllegalArgumentException("Cursor column must be within the bounds of the terminal");
        }
        this.cursorCol = cursorCol;
    }

    public void setCursor(int row, int col) {
        setCursorRow(row);
        setCursorCol(col);
    }

    private void checkNonNegative(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Movement amount cannot be negative");
        }
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public void moveCursorUp(int n) {
        checkNonNegative(n);
        cursorRow = clamp(this.cursorRow - n, 0, terminalSize.getHeight() - 1);
    }

    public void moveCursorDown(int n) {
        checkNonNegative(n);
        cursorRow = clamp(this.cursorRow + n, 0, terminalSize.getHeight() - 1);
    }

    public void moveCursorLeft(int n) {
        checkNonNegative(n);
        cursorCol = clamp(this.cursorCol - n, 0, terminalSize.getWidth() - 1);
    }

    public void moveCursorRight(int n) {
        checkNonNegative(n);
        cursorCol = clamp(this.cursorCol + n, 0, terminalSize.getWidth() - 1);
    }

    public CellStyle getCurrentStyle() {
        return currentStyle;
    }

    public void setCurrentStyle(CellStyle currentStyle) {
        if(currentStyle == null) {
            throw new IllegalArgumentException("Terminal Style cannot be null");
        }
        this.currentStyle = currentStyle;
    }

    public void writeText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            if (ch == '\n') {
                newline();
                continue;
            }

            Cell newCell = new Cell(String.valueOf(ch), currentStyle, CellType.BASIC);
            screen.get(cursorRow).setCell(cursorCol, newCell);

            moveCursorRight(1);
        }
    }

    public void fillLine(int row, String content, CellStyle style) {
        if(row < 0 || row >= terminalSize.getHeight()) {
            throw new IllegalArgumentException("Filled row must be within the bounds of the terminal");
        }

        if(content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }

        if(style == null) {
            throw new IllegalArgumentException("Style cannot be null");
        }

        screen.get(row).fill(content, style);
    }

    public void clearScreen() {
        for (int i = 0; i < terminalSize.getHeight(); i++) {
            screen.set(i, new Line(terminalSize.getWidth()));
        }
        cursorCol = cursorRow = 0;
    }

    public void clearScreenAndScrollback() {
        clearScreen();
        scrollback.clear();
    }

    public String getLineAsString(int row) {
        if(row < 0 || row >= terminalSize.getHeight()) {
            throw new IllegalArgumentException("Filled row must be within the bounds of the terminal");
        }
        return screen.get(row).toString();
    }

    public String getScreenAsString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < terminalSize.getHeight(); i++) {
            sb.append(screen.get(i).toString());

            if (i < terminalSize.getHeight() - 1) {
                sb.append('\n');
            }
        }

        return sb.toString();
    }

    public String getCellContentAt(int row, int col) {
        if(row < 0 || row >= terminalSize.getHeight()) {
            throw new IllegalArgumentException("Row must be within the bounds of the terminal");
        }

        if(col < 0 || col >= terminalSize.getWidth()) {
            throw new IllegalArgumentException("Column must be within the bounds of the terminal");
        }

        return screen.get(row).getCell(col).getContent();
    }

    public CellStyle getStyleAt(int row, int col) {
        if(row < 0 || row >= terminalSize.getHeight()) {
            throw new IllegalArgumentException("Row must be within the bounds of the terminal");
        }

        if(col < 0 || col >= terminalSize.getWidth()) {
            throw new IllegalArgumentException("Column must be within the bounds of the terminal");
        }

        return screen.get(row).getCell(col).getStyle();
    }

    private void addLineToScrollback(Line line) {
        scrollback.addLast(line);

        if(scrollback.size() > maxScrollback) {
            scrollback.removeFirst();
        }
    }

    public void insertEmptyLineAtBottom() {
        Line newLine = new Line(terminalSize.getWidth());
        screen.add(newLine);

        if (screen.size() > terminalSize.getHeight()) {
            Line removed = screen.removeFirst();
            addLineToScrollback(removed);
        }
    }

    public int getScrollbackSize() {
        return scrollback.size();
    }

    public String getScreenAndScrollbackAsString() {
        StringBuilder sb = new StringBuilder();

        boolean first = true;

        for (Line line : scrollback) {
            if (!first) {
                sb.append('\n');
            }
            sb.append(line.toString());
            first = false;
        }

        for (Line line : screen) {
            if (!first) {
                sb.append('\n');
            }
            sb.append(line.toString());
            first = false;
        }

        return sb.toString();
    }

    public void insertText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        int inserted = screen.get(cursorRow).insertText(cursorCol, text, currentStyle);
        moveCursorRight(inserted);
    }

    public void newline() {
        cursorCol = 0;

        if (cursorRow == terminalSize.getHeight() - 1) {
            insertEmptyLineAtBottom();
        } else {
            cursorRow++;
        }
    }

}
