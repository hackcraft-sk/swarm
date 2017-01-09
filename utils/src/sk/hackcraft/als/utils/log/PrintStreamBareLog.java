package sk.hackcraft.als.utils.log;

import java.io.PrintStream;

public class PrintStreamBareLog implements BareLog {

    private static final String TYPE_MESSAGE = "M";
    private static final String TYPE_ERROR = "E";

    private final String tag;
    private final PrintStream printer;

    /**
     * Constructs new log which is outputting messages to desired output in
     * default format.
     *
     * @param printer Output for messages
     */
    public PrintStreamBareLog(String tag, PrintStream printer) {
        this.tag = tag;
        this.printer = printer;
    }

    @Override
    public void m(String message) {
        printMessage(message, TYPE_MESSAGE);
    }

    @Override
    public void m(String template, Object... params) {
        String message = String.format(template, params);
        printMessage(message, TYPE_MESSAGE);
    }

    @Override
    public void e(String message) {
        printMessage(message, TYPE_ERROR);
    }

    @Override
    public void e(String message, Throwable error) {
        printMessage(message, TYPE_ERROR);
        error.printStackTrace(printer);
    }

    @Override
    public void e(String template, Object... params) {
        String message = String.format(template, params);
        printMessage(message, TYPE_ERROR);
    }

    private void printMessage(String message, String type) {
        printer
                .append(type)
                .append("/")
                .append(tag)
                .append(": ")
                .append(message)
                .println();
    }
}
