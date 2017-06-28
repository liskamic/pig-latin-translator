package com.example.piglatintranslator;

/**
 * Command line facade
 *
 */
public class App {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("usage: average s1 [s2 ...]");
            System.out.println("        s1, s2, etc.: strings to be translated into Pig Latin");
        }
        for (String arg : args) {
            System.out.println(TranslationUtils.translateText(arg));
        }
    }

}
