package egrep;

import egrep.parser.RegExParser;

public class Main {
    public static void main(String[] args) {
        RegExParser parser = new RegExParser("(a|)|bc*");
        try {
            System.out.println(parser.parse());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

