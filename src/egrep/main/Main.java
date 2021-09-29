package egrep.main;

import egrep.main.parser.RegExParser;

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
            System.out.println(parser.parse());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

