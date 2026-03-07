package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LineTest {

    @Test
    void constructorInitializesBlankCells() {
        Line line = new Line(4);

        assertEquals("    ", line.toString());
        for (int i = 0; i < 4; i++) {
            assertEquals(CellType.BLANK, line.getCell(i).getType());
        }
    }

    @Test
    void setCellStoresCellAtGivenColumn() {
        Line line = new Line(3);
        Cell cell = new Cell("A", CellStyle.defaultStyle(), CellType.BASIC);

        line.setCell(1, cell);

        assertEquals(cell, line.getCell(1));
    }

    @Test
    void fillReplacesEntireLine() {
        Line line = new Line(5);

        line.fill("x", CellStyle.defaultStyle());

        assertEquals("xxxxx", line.toString());
        for (int i = 0; i < 5; i++) {
            assertEquals(CellType.BASIC, line.getCell(i).getType());
        }
    }

    @Test
    void clearLineResetsLineToBlanks() {
        Line line = new Line(4);
        line.fill("z", CellStyle.defaultStyle());

        line.clearLine();

        assertEquals("    ", line.toString());
        for (int i = 0; i < 4; i++) {
            assertEquals(CellType.BLANK, line.getCell(i).getType());
        }
    }

    @Test
    void setCellThrowsForInvalidColumn() {
        Line line = new Line(3);
        Cell cell = new Cell("A", CellStyle.defaultStyle(), CellType.BASIC);

        assertThrows(IndexOutOfBoundsException.class, () -> line.setCell(-1, cell));
        assertThrows(IndexOutOfBoundsException.class, () -> line.setCell(3, cell));
    }

    @Test
    void constructorThrowsForNonPositiveWidth() {
        assertThrows(IllegalArgumentException.class, () -> new Line(0));
        assertThrows(IllegalArgumentException.class, () -> new Line(-3));
    }

    @Test
    void getCellThrowsForInvalidColumn() {
        Line line = new Line(3);

        assertThrows(IndexOutOfBoundsException.class, () -> line.getCell(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> line.getCell(3));
    }

    @Test
    void fillThrowsForNullContent() {
        Line line = new Line(4);

        assertThrows(IllegalArgumentException.class,
                () -> line.fill(null, CellStyle.defaultStyle()));
    }

    @Test
    void toStringReflectsManualCellChanges() {
        Line line = new Line(3);

        line.setCell(0, new Cell("A", CellStyle.defaultStyle(), CellType.BASIC));
        line.setCell(1, new Cell("B", CellStyle.defaultStyle(), CellType.BASIC));
        line.setCell(2, new Cell("C", CellStyle.defaultStyle(), CellType.BASIC));

        assertEquals("ABC", line.toString());
    }

    @Test
    void insertTextInBlankLineWorks() {
        Line line = new Line(5);

        int inserted = line.insertText(1, "ab", CellStyle.defaultStyle());

        assertEquals(2, inserted);
        assertEquals(" ab  ", line.toString());
    }

    @Test
    void insertTextShiftsExistingContentRight() {
        Line line = new Line(5);
        line.fill("x", CellStyle.defaultStyle());

        int inserted = line.insertText(2, "ab", CellStyle.defaultStyle());

        assertEquals(2, inserted);
        assertEquals("xxabx", line.toString());
    }

    @Test
    void insertTextTruncatesAtRightEdge() {
        Line line = new Line(5);
        line.setCell(0, new Cell("a", CellStyle.defaultStyle(), CellType.BASIC));
        line.setCell(1, new Cell("b", CellStyle.defaultStyle(), CellType.BASIC));
        line.setCell(2, new Cell("c", CellStyle.defaultStyle(), CellType.BASIC));

        int inserted = line.insertText(4, "xyz", CellStyle.defaultStyle());

        assertEquals(1, inserted);
    }
}