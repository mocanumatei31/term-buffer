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

    public int length() {
        return cells.length;
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

    public void fill(String content, CellStyle style) {
        if(content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        if(style == null) {
            throw new IllegalArgumentException("Cell Style cannot be null");
        }

        for(int i = 0; i < length(); i++) {
            cells[i] = new Cell(content, style, CellType.BASIC);
        }
    }

    public void clearLine() {
        for(int i = 0; i < length(); i++) {
            cells[i] = new Cell(" ", CellStyle.defaultStyle(), CellType.BLANK);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(cells.length);
        for (Cell cell : cells) {
            if (cell.getType() != CellType.DOUBLE_WIDTH_END) {
                sb.append(cell.getContent());
            }
        }
        return sb.toString();
    }

    public int insertText(int startCol, String text, CellStyle style) {
        checkColumn(startCol);

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (style == null) {
            throw new IllegalArgumentException("Style cannot be null");
        }

        if (text.isEmpty()) {
            return 0;
        }

        int insertCount = Math.min(text.length(), length() - startCol);

        if (insertCount <= 0) {
            return 0;
        }

        for (int i = length() - 1; i >= startCol + insertCount; i--) {
            cells[i] = cells[i - insertCount];
        }
        for (int i = 0; i < insertCount; i++) {
            String ch = text.substring(i, i + 1);
            cells[startCol + i] = new Cell(ch, style, CellType.BASIC);
        }

        return insertCount;
    }
}


