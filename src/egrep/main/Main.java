package egrep.main;

import egrep.main.automaton.Automaton;
import egrep.main.parser.RegExParser;

import java.util.List;

/**
 * The program entry point, welcome :)
 *
 * @author Emilie SIAU
 * @author Hugo GUERRIER
 * @version 0.1
 */
public class Main {
    public static void main(String[] args) {
        RegExParser parser = new RegExParser("a|bc*");
        try {
            
            Automaton automaton = new Automaton(parser.parse());
            try {
                automaton.create();
                System.out.println(automaton);
            } catch(Exception e) {
                e.printStackTrace();
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

