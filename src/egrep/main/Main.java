package egrep.main;

import egrep.main.automaton.Automaton;
import egrep.main.exceptions.AutomatonException;
import egrep.main.exceptions.CharacterException;
import egrep.main.exceptions.ParsingException;
import egrep.main.parser.RegExParser;
import egrep.main.parser.RegExTree;
import egrep.main.search_engine.NaiveStrategy;
import egrep.main.search_engine.SearchEngine;
import egrep.main.utils.Pair;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * The program entry point, welcome :)
 *
 * @author Emilie SIAU
 * @author Hugo GUERRIER
 * @version 0.4
 */
public class Main {

    // ----- Utils attributes -----

    private static final String version = "0.4";

    private static boolean helpFlag = false;
    private static boolean benchmarkFlag = false;
    private static boolean verboseFlag = false;
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
        System.out.println("BENCHMARK MODE");
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


            } catch(ParsingException e) {
                System.out.println("The regex cannot be parsed. Message : " + e.getMessage());
                if(verboseFlag) e.printStackTrace();
            } catch(AutomatonException e) {
                System.out.println("The automaton is not deterministic");
                if(verboseFlag) e.printStackTrace();
            } catch(CharacterException e) {
                System.out.println("There is a non ASCII character in the text");
                if(verboseFlag) e.printStackTrace();
            } catch(IOException e) {
                System.out.println("Error during the input file reading");
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

