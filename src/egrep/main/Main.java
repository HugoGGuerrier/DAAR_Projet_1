package egrep.main;

import egrep.main.automaton.Automaton;
import egrep.main.parser.RegExParser;
import egrep.main.parser.RegExTree;
import egrep.main.search_engine.NaiveStrategy;

/**
 * The program entry point, welcome :)
 *
 * @author Emilie SIAU
 * @author Hugo GUERRIER
 * @version 0.4
 */
public class Main {
    public static void main(String[] args) {
        // Prepare the flags and variables
        boolean helpFlag = false;
        boolean benchmarkFlag = false;
        String regex = null;
        String fileRelativePath = null;

        // Parse the arguments
        for(String arg : args) {
            switch (arg) {

                case "--help":
                case "-h":
                    helpFlag = true;
                    break;

                default:
                    if(regex == null) regex = arg;
                    else if(fileRelativePath == null) fileRelativePath = arg;

            }
        }

        // Define and print the regex string
        System.out.println("The regex : " + regex + "\n");

        try {

            // Parse and show the parsing result and duration
            RegExParser parser = new RegExParser(regex);

            long parseStartTime = System.currentTimeMillis();
            RegExTree tree = parser.parse();
            long parseEndTime = System.currentTimeMillis();

            System.out.println("The parsing result : " + tree.toString());
            System.out.println("Parsing duration : " + (parseEndTime - parseStartTime) + "ms\n");

            // Prepare the automaton for the regex tree
            Automaton automaton = new Automaton(tree, false);

            // Create the display the NDFA
            long autoStartTime = System.currentTimeMillis();
            automaton.createNDFA();
            long autoEndTime = System.currentTimeMillis();

            System.out.println(automaton);
            System.out.println("Non-Deterministic Automaton creation duration : " + (autoEndTime - autoStartTime) + "ms\n");

            // Create and display the DFA
            long deterAutoStartTime = System.currentTimeMillis();
            automaton.createDFA();
            long deterAutoEndTime = System.currentTimeMillis();

            System.out.println(automaton);
            System.out.println("Deterministic Automaton creation duration : " + (deterAutoEndTime - deterAutoStartTime) + "ms\n");

            NaiveStrategy strategy = new NaiveStrategy();
            if(strategy.isMatching(automaton, "salutccoucou bisou")) {
                System.out.println("Matching OK");
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

