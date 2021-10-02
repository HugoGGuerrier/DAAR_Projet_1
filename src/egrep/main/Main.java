package egrep.main;

import egrep.main.automaton.Automaton;
import egrep.main.parser.RegExParser;
import egrep.main.parser.RegExTree;

/**
 * The program entry point, welcome :)
 *
 * @author Emilie SIAU
 * @author Hugo GUERRIER
 * @version 0.2
 */
public class Main {
    public static void main(String[] args) {
        // Define and print the regex string
        String regex = "a|bc*";
        System.out.println("The regex : " + regex + "\n");

        try {

            // Parse and show the parsing result and duration
            RegExParser parser = new RegExParser(regex);

            long parseStartTime = System.currentTimeMillis();
            RegExTree tree = parser.parse();
            long parseEndTime = System.currentTimeMillis();

            System.out.println("The parsing result : " + tree.toString());
            System.out.println("Parsing duration : " + (parseEndTime - parseStartTime) + "ms\n");

            try {

                // Prepare the automaton for the regex tree
                Automaton automaton = new Automaton(tree);

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
                System.out.println("Deterministic Automaton creation duration : " + (deterAutoEndTime - deterAutoStartTime) + "ms");

            } catch(Exception e) {
                e.printStackTrace();
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

