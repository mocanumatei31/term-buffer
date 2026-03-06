package org.example;

public class Line {
    private final Cell[] cells;
    public Line(int width) {
        if (width <= 0) {
            throw new IllegalArgumentException("width must be positive");
        }

        this.cells = new Cell[width];
        for (int i = 0; i < width; i++) {
            cells[i] = new Cell(" ", CellStyle.defaultStyle(), CellType.BLANK);
        }
    }

    public void setCell(int col, Cell cell) {
        checkColumn(col);
        if (cell == null) {
            throw new IllegalArgumentException("Cell cannot be null");
        }
        cells[col] = cell;
    }

    private void checkColumn(int col) {
        if (col < 0 || col >= cells.length) {
            throw new IndexOutOfBoundsException("Invalid column: " + col);
        }
    }

    public Cell getCell(int col) {
        checkColumn(col);
        return cells[col];
    }
}
