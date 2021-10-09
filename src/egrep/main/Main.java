package egrep.main;

import egrep.main.exceptions.AutomatonException;
import egrep.main.exceptions.ParsingException;
import egrep.main.search_engine.SearchEngine;
import egrep.main.utils.Pair;

import java.io.IOException;
import java.util.List;

/**
 * The program entry point, welcome :)
 *
 * @author Emilie SIAU
 * @author Hugo GUERRIER
 * @version 0.5
 */
public class Main {

    // ----- Utils attributes -----

    public static boolean verboseFlag = false;

    private static final String version = "0.5";

    private static boolean helpFlag = false;
    private static boolean benchmarkFlag = false;
    private static boolean validCall = true;

    private static String regex = null;
    private static String fileRelativePath = null;

    // ----- Utils functions -----

    private static void unknownArg(String arg) {
        System.out.println("Unknown argument : \"" + arg + "\"");
        System.out.println("Use -h to show help");
        validCall = false;
    }

    private static void displayHelp() {
        System.out.println("Egrep Java clone : v" + version);
        System.out.println("Authors : Emilie SIAU and Hugo GUERRIER");
        System.out.println("Licence : MIT\n");

        System.out.println("Usage : egrep [options] \"<REGEX>\" \"<INPUT_FILE>\"");
        System.out.println("Available options :");
        System.out.println("\t-b (--benchmark) = Run all benchmark tests. No need of regex or input file in this mode.");
        System.out.println("\t-h (--help) = Display this help message.");
        System.out.println("\t-v (--verbose) = Display a lot of information about the regex and file processing.");
    }

    private static void benchmark() {
        // Display some info
        System.out.println("===== Entering the benchmark mode =====\n");

        // Prepare the benchmark variables
        String testBankDir = "test_bank";
        String[] testFileNames = {
                "HOB_1.txt",
                "HOB_50.txt",
                "HOB_100.txt",
                "HOB_150.txt",
                "HOB_200.txt",
                "HOB_500.txt",
                "HOB_1000.txt",
                "HOB_1500.txt",
                "HOB_2000.txt",
                "HOB_5000.txt",
                "HOB_10000.txt",
                "HOB_original.txt",
                "HOB_x2.txt"
        };
        int[] sizes = {
                1,
                50,
                100,
                150,
                200,
                500,
                1000,
                1500,
                2000,
                5000,
                10000,
                13308,
                13308 * 2
        };

        // Get all the test files full name
        String[] testFiles = new String[testFileNames.length];
        for(int i = 0 ; i < testFiles.length ; i++) {
            testFiles[i] = testBankDir + "/" + testFileNames[i];
        }


        // === Define the test regex
        String regex = "Babylon";

        System.out.println("=== For the regular expression " + regex + " ===");

        // Test the naive strategy and show the result
        System.out.println("\n== Naive strategy benchmark on file size\n");
        for(int i = 0 ; i < testFiles.length ; i++) {
            String testFile = testFiles[i];
            int size = sizes[i];

            try {

                SearchEngine engine = new SearchEngine(regex, testFile, SearchEngine.Strategy.NAIVE);
                long startTime = System.currentTimeMillis();
                List<Pair<Integer, String>> res = engine.searchLines();
                long endTime = System.currentTimeMillis();

                System.out.println(testFile + " (" + size + " lines)  matched result=" + res.size() + "  |  search duration=" + (endTime - startTime) + " ms");

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

        }

        // Test the KMP strategy and show the result
        System.out.println("\n== KMP strategy benchmark on file size\n");
        for(int i = 0 ; i < testFiles.length ; i++) {
            String testFile = testFiles[i];
            int size = sizes[i];

            try {

                SearchEngine engine = new SearchEngine(regex, testFile, SearchEngine.Strategy.KMP);
                long startTime = System.currentTimeMillis();
                List<Pair<Integer, String>> res = engine.searchLines();
                long endTime = System.currentTimeMillis();

                System.out.println(testFile + " (" + size + " lines)  matched result=" + res.size() + "  |  search duration=" + (endTime - startTime) + " ms");

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        // Test the java native strategy and show the result
        System.out.println("\n== Java Native strategy benchmark on file size\n");
        for(int i = 0 ; i < testFiles.length ; i++) {
            String testFile = testFiles[i];
            int size = sizes[i];

            try {

                SearchEngine engine = new SearchEngine(regex, testFile, SearchEngine.Strategy.JAVA_NATIVE);
                long startTime = System.currentTimeMillis();
                List<Pair<Integer, String>> res = engine.searchLines();
                long endTime = System.currentTimeMillis();

                System.out.println(testFile + " (" + size + " lines)  matched result=" + res.size() + "  |  search duration=" + (endTime - startTime) + " ms");

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private static void run() {
        // Verify that there is all needed arguments
        if(regex != null && fileRelativePath != null) {

            // Prepare the research result list
            List<Pair<Integer, String>> results;

            try {

                // Create the search engine and do the research
                SearchEngine engine = new SearchEngine(regex, fileRelativePath);
                results = engine.searchLines();

                // Presenting the result
                for(Pair<Integer, String> line : results) {
                    System.out.println(line.getKey() + " : " + line.getValue());
                }

            } catch(ParsingException e) {
                System.err.println("The regex cannot be parsed. Message : " + e.getMessage());
                if(verboseFlag) e.printStackTrace();
            } catch(AutomatonException e) {
                System.err.println("The automaton is not deterministic");
                if(verboseFlag) e.printStackTrace();
            } catch(IOException e) {
                System.err.println("Error during the input file reading");
                if(verboseFlag) e.printStackTrace();
            }

        } else {
            if(regex == null) System.out.println("Regex not provided !");
            if(fileRelativePath == null) System.out.println("Input file note provided !");
            System.out.println("Cannot run the command, use -h to show help");
        }
    }

    // ----- The main function -----

    public static void main(String[] args) {
        // Parse the arguments
        for(String arg : args) {
            switch (arg) {

                case "--help":
                case "-h":
                    helpFlag = true;
                    break;

                case "--benchmark":
                case "-b":
                    benchmarkFlag = true;
                    break;

                case "--verbose":
                case "-v":
                    verboseFlag = true;
                    break;

                default:
                    if(regex == null) regex = arg;
                    else if(fileRelativePath == null) fileRelativePath = arg;
                    else unknownArg(arg);

            }
        }

        // If the help flag is here, just display the help message
        if(helpFlag) {
            displayHelp();
        } else {

            // Verify that the call is valid
            if(validCall) {
                if(benchmarkFlag) benchmark();
                else run();
            }
        }
    }
}

