package org.example;

import java.util.EnumSet;

public final class CellStyle {
    private final TerminalColor foreground;
    private final TerminalColor background;
    private final EnumSet<Style> flags;

    public CellStyle(
            TerminalColor foreground,
            TerminalColor background,
            EnumSet<Style> flags
    ) {
        if (foreground == null) throw new IllegalArgumentException("Foreground Color cannot be null");
        if (background == null) throw new IllegalArgumentException("Background Color cannot be null");
        if (flags == null) throw new IllegalArgumentException("Style Flags cannot be null");

        this.foreground = foreground;
        this.background = background;
        this.flags = EnumSet.copyOf(flags);
    }

    public TerminalColor foreground() {
        return foreground;
    }

    public TerminalColor background() {
        return background;
    }

    public EnumSet<Style> flags() {
        return EnumSet.copyOf(flags);
    }

    public boolean isBold() {
        return flags.contains(Style.BOLD);
    }

    public boolean isItalic() {
        return flags.contains(Style.ITALIC);
    }

    public boolean isUnderline() {
        return flags.contains(Style.UNDERLINE);
    }

    public static CellStyle defaultStyle() {
        return new CellStyle(
                TerminalColor.DEFAULT,
                TerminalColor.DEFAULT,
                EnumSet.noneOf(Style.class)
        );
    }

}