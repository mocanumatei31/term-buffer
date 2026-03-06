package org.example;

import java.util.Objects;

public class Cell {
    private final String content;
    private final CellStyle style;
    private final CellType type;

    public Cell(String content, CellStyle style, CellType type) {
        if (content == null) throw new IllegalArgumentException("Content cannot be null");
        if (style == null) throw new IllegalArgumentException("Style cannot be null");
        if (type == null) throw new IllegalArgumentException("Type cannot be null");

        this.content = content;
        this.style = style;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public CellStyle getStyle() {
        return style;
    }

    public CellType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return Objects.equals(content, cell.content) && Objects.equals(style, cell.style) && Objects.equals(type, cell.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, style, type);
    }
}
