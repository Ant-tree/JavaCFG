package com.anttree.flow.graph.options;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ConsoleUtils {

    private static final char CARRIAGE_RETURN = '\r';
    private static final char ESCAPE_CHAR = '\u001b';
    private static final String barIndicator = "█";
    private static final String emptyBarIndicator = " ";
    private static final int DEFAULT_TERMINAL_WIDTH = 80;

    private static Terminal terminal = null;
    private static boolean cursorMovementSupported = false;

    synchronized static int getTerminalWidth() {
        Terminal terminal = getTerminal();
        int width = terminal.getWidth();

        // Workaround for issue #23 under IntelliJ
        return (width >= 10) ? width : DEFAULT_TERMINAL_WIDTH;
    }

    static boolean hasCursorMovementSupport() {
        if (terminal == null)
            terminal = getTerminal();
        return cursorMovementSupported;
    }

    synchronized static void closeTerminal() {
        try {
            if (terminal != null) {
                terminal.close();
                terminal = null;
            }
        } catch (IOException ignored) { /* noop */ }
    }

    /**
     * <ul>
     *     <li>Creating terminal is relatively expensive, usually takes between 5-10ms.
     *         <ul>
     *             <li>If updateInterval is set under 10ms creating new terminal for on every re-render of progress bar could be a problem.</li>
     *             <li>Especially when multiple progress bars are running in parallel.</li>
     *         </ul>
     *     </li>
     *     <li>Another problem with {@link Terminal} is that once created you can create another instance (say from different thread), but this instance will be
     *     "dumb". Until previously created terminal will be closed.
     *     </li>
     * </ul>
     */
    static Terminal getTerminal() {
        if (terminal == null) {
            try {
                // Issue #42
                // Defaulting to a dumb terminal when a supported terminal can not be correctly created
                // see https://github.com/jline/jline3/issues/291
                terminal = TerminalBuilder.builder().dumb(true).build();
                cursorMovementSupported = (
                        terminal.getStringCapability(InfoCmp.Capability.cursor_up) != null &&
                                terminal.getStringCapability(InfoCmp.Capability.cursor_down) != null
                );
            } catch (IOException e) {
                throw new RuntimeException("This should never happen! Dumb terminal should have been created.");
            }
        }
        return terminal;
    }

    public static String formatBox(String title, boolean center, List<String> lines) {
        return formatBox(title, center, lines, true);
    }

    public static String formatBox(String title, boolean center, List<String> lines, boolean useColumn) {
        int width = 10;
        if (title != null) {
            width = title.length() + 4;
        }
        List<String> linesOrganized = lines.stream()
                .map(line -> line.replace("\t", "    "))
                .collect(Collectors.toList());

        for (String line : linesOrganized) {
            int lineWidth = (useColumn
                    ? line.length()
                    : (int)(line.length() * 1.5)
            ) + 2;
            if (lineWidth > width) width = lineWidth;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("+");
        if (title == null) {
            addTimes(sb, width, "-");
        } else {
            centerString(sb, "[ " + title + " ]", "-", width);
        }
        sb.append("+");
        sb.append("\n");

        for (String line : linesOrganized) {
            if (useColumn) {
                sb.append("|");
            }

            if (center) {
                centerString(sb, line, " ", width);
            } else {
                sb.append(" ").append(line);
                addTimes(sb, width - line.length() - 1, " ");
            }

            if (useColumn) {
                sb.append("|");
            }

            sb.append("\n");
        }


        sb.append("+");
        addTimes(sb, width, "-");
        sb.append("+");

        return sb.toString();
    }

    private static void centerString(StringBuilder stringBuilder, String stringToCenter, String fillChar, int width) {
        int sideOffset = width - stringToCenter.length();

        addTimes(stringBuilder, sideOffset / 2, fillChar);

        stringBuilder.append(stringToCenter);

        addTimes(stringBuilder, sideOffset - sideOffset / 2, fillChar);
    }

    private static void addTimes(StringBuilder sb, int times, String s) {
        for (int index = 0; index < Math.max(0, times); index++) {
            sb.append(s);
        }
    }

    public enum COLOR {
        RED,
        GREEN,
        OLIVE,
        CYAN,
        PURPLE,
        BLUE,
        GRAY
    }

    public static String intoColoredString(COLOR color, String content) {
        String colorString = null;
        switch (color) {
            case RED    : colorString = "\u001b[31m%s\u001b[0m"; break;
            case GREEN  : colorString = "\u001b[32m%s\u001b[0m"; break;
            case OLIVE  : colorString = "\u001b[33m%s\u001b[0m"; break;
            case BLUE   : colorString = "\u001b[34m%s\u001b[0m"; break;
            case PURPLE : colorString = "\u001b[35m%s\u001b[0m"; break;
            case CYAN   : colorString = "\u001b[36m%s\u001b[0m"; break;
            case GRAY   : colorString = "\u001b[37m%s\u001b[0m"; break;
        }
        return String.format(colorString, content);
    }

    public static String getProgressMessage(String tag, int processed, int size) {
        return tag + " : " + getProgressBar(
                processed,
                size
        ) + String.format(" %04d/%04d [%.2f%%]",
                processed,
                size,
                (double) processed / size * 100
        );
    }

    public static String getProgressBar(int progress, int size){
        StringBuilder progressBar = new StringBuilder("\u001b[33m│");
//        StringBuilder progressBar = new StringBuilder("|");

        int maxSlot = Math.max(getTerminalWidth() / 2, 15);

        double progressPercent = ((double) progress / size * 100.0);

        int mod = (int) Math.round((progressPercent / 100.0) * maxSlot);

        for (int index = 0; index < maxSlot; index++) {
            progressBar.append((index <= mod)
                    ? barIndicator
                    : emptyBarIndicator);
        }
        progressBar.append("│\u001b[0m");
//        progressBar.append("|");
        return progressBar.toString();
    }
}
