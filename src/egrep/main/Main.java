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
 * @version 0.3
 */
public class Main {
    public static void main(String[] args) {
        // Define and print the regex string
        String regex = "couu*couu*";
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

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

