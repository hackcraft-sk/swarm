package sk.hackcraft.als.utils.components;

import java.io.PrintStream;

public class PrintStreamBareLog implements BareLog {

    private final String tag;
    private final PrintStream output;

    public PrintStreamBareLog(String tag, PrintStream output) {
        this.tag = tag;
        this.output = output;
    }

    @Override
    public void m(String message) {
        printMessageHeader();
        output.println(message);
    }

    @Override
    public void m(String format, Object... params) {
        printMessageHeader();
        output.printf(format, params);
        output.println();
    }

    private void printMessageHeader() {
        output.append("M/").append(tag);
    }

    @Override
    public void e(String message) {
        printErrorHeader();
        output.println(message);
    }

    @Override
    public void e(String format, Object... params) {
        printErrorHeader();
        output.printf(format, params);
        output.println();
    }

    @Override
    public void e(String message, Throwable throwable) {
        printErrorHeader();
        output.println(message);
        throwable.printStackTrace(output);
    }

    private void printErrorHeader() {
        output.append("E/").append(tag);
    }

}
