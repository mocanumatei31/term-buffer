package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TerminalBufferTest {

    @Test
    void constructorInitializesCursorAtOrigin() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        assertEquals(0, buffer.getCursorRow());
        assertEquals(0, buffer.getCursorCol());
    }

    @Test
    void moveCursorRightClampsAtLastColumn() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        buffer.moveCursorRight(100);

        assertEquals(4, buffer.getCursorCol());
    }

    @Test
    void moveCursorDownClampsAtLastRow() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        buffer.moveCursorDown(100);

        assertEquals(2, buffer.getCursorRow());
    }

    @Test
    void setCursorThrowsWhenOutOfBounds() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        assertThrows(IllegalArgumentException.class, () -> buffer.setCursor(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> buffer.setCursor(0, 5));
    }

    @Test
    void setCurrentStyleThrowsForNull() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        assertThrows(IllegalArgumentException.class, () -> buffer.setCurrentStyle(null));
    }

    @Test
    void writeTextWritesFromCursorPosition() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        buffer.writeText("abc");

        assertEquals("abc  ", buffer.getLineAsString(0));
        assertEquals(3, buffer.getCursorCol());
    }

    @Test
    void writeTextStopsAtEndOfLine() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);
        buffer.setCursor(0, 3);

        buffer.writeText("abcd");

        assertEquals("   ab", buffer.getLineAsString(0));
        assertEquals(4, buffer.getCursorCol());
    }

    @Test
    void clearScreenResetsContentAndCursor() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);
        buffer.writeText("abc");
        buffer.moveCursorDown(1);
        buffer.moveCursorRight(2);

        buffer.clearScreen();

        assertEquals("     ", buffer.getLineAsString(0));
        assertEquals("     ", buffer.getLineAsString(1));
        assertEquals("     ", buffer.getLineAsString(2));
        assertEquals(0, buffer.getCursorRow());
        assertEquals(0, buffer.getCursorCol());
    }

    @Test
    void writeTextRejectsNull() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        assertThrows(IllegalArgumentException.class, () -> buffer.writeText(null));
    }

    @Test
    void writeTextOverwritesExistingCharacters() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        buffer.writeText("abc");
        buffer.setCursor(0, 1);
        buffer.writeText("Z");

        assertEquals("aZc  ", buffer.getLineAsString(0));
    }

    @Test
    void fillLineOnlyChangesSpecifiedRow() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        buffer.fillLine(1, "x", CellStyle.defaultStyle());

        assertEquals("     ", buffer.getLineAsString(0));
        assertEquals("xxxxx", buffer.getLineAsString(1));
        assertEquals("     ", buffer.getLineAsString(2));
    }

    @Test
    void clearScreenResetsCursorPosition() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        buffer.setCursor(2, 4);
        buffer.clearScreen();

        assertEquals(0, buffer.getCursorRow());
        assertEquals(0, buffer.getCursorCol());
    }

    @Test
    void getScreenAsStringContainsAllRows() {
        TerminalBuffer buffer = new TerminalBuffer(4, 2, 10);

        buffer.writeText("ab");

        assertEquals("ab  \n    ", buffer.getScreenAsString());
    }

    @Test
    void getLineAsStringReturnsRequestedRow() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);
        buffer.writeText("abc");

        assertEquals("abc  ", buffer.getLineAsString(0));
    }

    @Test
    void getLineAsStringThrowsForInvalidRow() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        assertThrows(IllegalArgumentException.class, () -> buffer.getLineAsString(-1));
        assertThrows(IllegalArgumentException.class, () -> buffer.getLineAsString(3));
    }

    @Test
    void getCellContentAtReturnsCellContent() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);
        buffer.writeText("abc");

        assertEquals("a", buffer.getCellContentAt(0, 0));
        assertEquals("b", buffer.getCellContentAt(0, 1));
        assertEquals("c", buffer.getCellContentAt(0, 2));
    }

    @Test
    void getCellContentAtThrowsForInvalidCoordinates() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        assertThrows(IllegalArgumentException.class, () -> buffer.getCellContentAt(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> buffer.getCellContentAt(0, -1));
        assertThrows(IllegalArgumentException.class, () -> buffer.getCellContentAt(3, 0));
        assertThrows(IllegalArgumentException.class, () -> buffer.getCellContentAt(0, 5));
    }

    @Test
    void getStyleAtReturnsWrittenCellStyle() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);
        CellStyle style = new CellStyle(
                TerminalColor.RED,
                TerminalColor.BLUE,
                java.util.EnumSet.of(Style.BOLD)
        );

        buffer.setCurrentStyle(style);
        buffer.writeText("x");

        assertEquals(style, buffer.getStyleAt(0, 0));
    }

    @Test
    void getScreenAsStringReturnsWholeScreen() {
        TerminalBuffer buffer = new TerminalBuffer(4, 2, 10);
        buffer.writeText("ab");
        buffer.setCursor(1, 0);
        buffer.writeText("cd");

        assertEquals("ab  \ncd  ", buffer.getScreenAsString());
    }

    @Test
    void insertEmptyLineAtBottomKeepsScreenHeightUnchanged() {
        TerminalBuffer buffer = new TerminalBuffer(4, 2, 10);

        buffer.insertEmptyLineAtBottom();

        assertEquals("    \n    ", buffer.getScreenAsString());
    }

    @Test
    void insertEmptyLineAtBottomShiftsScreenUpAndAddsBlankBottomLine() {
        TerminalBuffer buffer = new TerminalBuffer(4, 2, 10);

        buffer.writeText("aa");
        buffer.setCursor(1, 0);
        buffer.writeText("bb");

        buffer.insertEmptyLineAtBottom();

        assertEquals("bb  \n    ", buffer.getScreenAsString());
        assertEquals(1, buffer.getScrollbackSize());
    }

    @Test
    void getScrollbackSizeStartsAtZero() {
        TerminalBuffer buffer = new TerminalBuffer(4, 2, 10);

        assertEquals(0, buffer.getScrollbackSize());
    }

    @Test
    void getScreenAndScrollbackAsStringReturnsOnlyScreenWhenScrollbackIsEmpty() {
        TerminalBuffer buffer = new TerminalBuffer(4, 2, 10);
        buffer.writeText("ab");

        assertEquals("ab  \n    ", buffer.getScreenAndScrollbackAsString());
    }

    @Test
    void insertEmptyLineAtBottomDiscardsOldestScrollbackLineWhenMaxExceeded() {
        TerminalBuffer buffer = new TerminalBuffer(4, 2, 1);

        buffer.writeText("aa");
        buffer.setCursor(1, 0);
        buffer.writeText("bb");
        buffer.insertEmptyLineAtBottom();

        buffer.setCursor(1, 0);
        buffer.writeText("cc");
        buffer.insertEmptyLineAtBottom();

        assertEquals(1, buffer.getScrollbackSize());
        assertEquals("bb  \ncc  \n    ", buffer.getScreenAndScrollbackAsString());
    }
}