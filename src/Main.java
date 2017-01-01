import java.io.IOException;
import java.util.ArrayList;

import org.koturn.brainfuck.Brainfuck;
import org.koturn.util.ArgumentParser;
import org.koturn.util.ArgumentParserException;
import org.koturn.util.OptionItem;

/**
 * Entry point class
 */
public class Main {
    /**
     * The entry point of this program
     * @param args  Command-line arguments
     * @throws ArgumentParserException  Throw if argument parsing is failed
     * @throws IOException  Throw if IO error is occured
     */
    public static void main(String[] args) throws ArgumentParserException, IOException {
        ArgumentParser ap = new ArgumentParser();
        ap.setOption(new OptionItem('h', "help", "Show help and exit this program"));
        ap.setOption(new OptionItem('t', "time", "Show execution time"));
        ap.setOption(new OptionItem('O', "optimize", OptionItem.OptionType.RequreidArgument, "Sepecify optimize level"
                + ArgumentParser.newline() + "  0: No optimize"
                + ArgumentParser.newline() + "  1: Compile to IR-code",
                "LEVEL", 1));
        ap.setOption(new OptionItem('H', "heapsize", OptionItem.OptionType.RequreidArgument, "Specify heap size", "HEAP_SIZE", 65536));
        ap.parse(args);
        if (ap.<Boolean>getValue("help")) {
            ap.showUsage();
            return;
        }
        ArrayList<String> argList = ap.getRemnantArguments();
        int heapSize = ap.<Integer>getValue("heapsize");
        int optLevel = ap.<Integer>getValue("optimize");
        boolean isMeasureTime = ap.<Boolean>getValue("time");
        Brainfuck bf = new Brainfuck();
        if (argList.size() == 0) {
            bf.load(System.in);
            long start = System.nanoTime();
            if (optLevel > 0) {
                bf.compile();
            }
            bf.execute(heapSize);
            if (isMeasureTime) {
                System.out.println("Execution time: " + (System.nanoTime() - start) / 1000 / 1000.0 + " ms");
            }
        } else {
            for (String filepath : argList) {
                bf.load(filepath);
                long start = System.nanoTime();
                if (optLevel > 0) {
                    bf.compile();
                }
                bf.execute(heapSize);
                if (isMeasureTime) {
                    System.out.println("Execution time: " + (System.nanoTime() - start) / 1000 / 1000.0 + " ms");
                }
            }
        }
    }
}
