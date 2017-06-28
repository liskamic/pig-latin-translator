package com.example.piglatintranslator;

//TODO hashmap dictionary, possibly loaded from file

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Hello world!
 *
 */
public class App {

    // English language definition of Consonant and Vowel
    private static final String CONSONANTS = "bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ";
    private static final String CONSONANT_SUFFIX = "ay";
    private static final String VOWELS = "aeiouAEIOU";
    private static final String VOWEL_SUFFIX = "way";
//    private static final String HYPHENS = "‐-‒–—―";
//    private static final String HYPHENS = "‐-";

    // UNICODE_CHARACTER_CLASS flag set to find all possible punctuation marks
    private static Pattern punctuationPattern = Pattern.compile("\\p{Punct}", Pattern.UNICODE_CHARACTER_CLASS);

    public static void main(String[] args) {
        String multichar = "\uD841\uDF0E";
        System.out.println("multichar: " + multichar);
        System.out.println("multichar.substring(0,1)): " + multichar.substring(0,1));
        System.out.println("multichar.charAt(0)): " + multichar.charAt(0));
        char test = multichar.charAt(0);
        System.out.println("test: " + test);
        System.out.println("____________________________________________");
        System.out.println();
        String regextest = "ľščť-žýáíéúäňô‘’“”''\"\";/?-‐‹›«».!…‒–—―,،、:[](){}⟨⟩’'ĺÉŔÝÚÍÓÁŚĹŹĆŃĚŘŤŠĎĽŽČŇ1234567890";
        //TODO change comparisons to Pattern.UNICODE_CASE
        Pattern p = Pattern.compile("\\p{Punct}", Pattern.UNICODE_CHARACTER_CLASS);
        System.out.println(regextest.replaceAll("\\p{Punct}", ""));
        System.out.println(p.matcher(regextest).replaceAll(""));
        System.out.println(Character.toUpperCase('ô'));
        System.out.println("____________________________________________");
        System.out.println();
        System.out.println("Hello -> " + translateWord("Hello"));
        System.out.println("Hello -> " + "Ellohay");
        System.out.println("apple -> " + translateWord("apple"));
        System.out.println("apple -> " + "appleway");
        System.out.println("stairway -> " + translateWord("stairway"));
        System.out.println("stairway -> " + "stairway");
        System.out.println("can’t -> " + translateWord("can’t"));
        System.out.println("can’t -> " + "antca’y");
        System.out.println("end. -> " + translateWord("end."));
        System.out.println("end. -> " + "endway.");
        System.out.println("this-thing -> " + translateText("this-thing"));
        System.out.println("this-thing -> " + "histay-hingtay");
        System.out.println("Beach -> " + translateWord("Beach"));
        System.out.println("Beach -> " + "Eachbay");
        System.out.println("McCloud -> " + translateWord("McCloud"));
        System.out.println("McCloud -> " + "CcLoudmay");
        System.out.println("c\":a7n’t. -> " + translateWord("c\":a7n’t."));
        System.out.println("c\":a7n’t. -> " + "a7nt\":ca’y.");
        System.out.println(translateText("\tHello apple\tstairway can’t\nend. this-thing Beach McCloud\tc\":a7n’t.\n"));
        System.out.println("Ellohay appleway stairway antca’y endway. histay-hingtay Eachbay CcLoudmay a7nt\":ca’y.");
        System.out.println(translateText("‐this-thing‐sucks-"));
        System.out.println("‐histay-hingtay‐uckssay-");
    }


    private static String translateText(String originalText) {
        //TODO preserve the original whitespaces
        // Either split manually in a stream or use multiple splits for different types of whitespace
//        // (or possibly same as with hyphens)
        List<String> originalWords = Arrays.asList(originalText.split("\\p{Space}"));
        StringBuilder translatedTextBuilder = new StringBuilder();
        for (String word : originalWords) {
            translatedTextBuilder.append(treatHyphens(word)).append(" ");
        }
        translatedTextBuilder.deleteCharAt(translatedTextBuilder.length() - 1);

        return translatedTextBuilder.toString();
    }

    private static String treatHyphens(String originalWord) {
        if (!originalWord.contains("-")) {
            return translateWord(originalWord);
        }
//        List<String> splitWords = Arrays.asList(originalWord.split("[" + HYPHENS + "]"));
        List<String> splitWords = Arrays.asList(originalWord.split("-"));
//        Iterator<String> hyphensIterator = Arrays.asList(originalWord.split("[^" + HYPHENS + "]+")).iterator();
        StringBuilder translatedTextBuilder = new StringBuilder();
        for (String word : splitWords) {
            translatedTextBuilder//.append(hyphensIterator.hasNext() ? hyphensIterator.next() : "")
                    .append(translateWord(word))
                    .append("-");
        }
        translatedTextBuilder.deleteCharAt(translatedTextBuilder.length() - 1);

        return translatedTextBuilder.toString();
    }

    private static String translateWord(String original) {
        // Input check
        if (original == null || original.isEmpty() ||
                // Words that end in "way" are not modified.
                (original.length() > 2 && original.substring(original.length() - 3).equals(VOWEL_SUFFIX))) {
            return original;
        }
        // Words that start with a vowel have the letters "way" added to the end.
        String translation = isVowel(original.substring(0, 1)) ? original + VOWEL_SUFFIX : modifyForConsonant(original);

        return fixPunctuation(original, translation);
    }

    private static boolean isConsonant(String character) {
        return CONSONANTS.contains(character);
    }

    private static boolean isVowel(String character) {
        return VOWELS.contains(character);
    }

    private static String modifyForConsonant(String original) {
        // Already checked for null, empty and vowel before but the 'word' might start with an unexpected character
        if (!isConsonant(original.substring(0, 1))) {
            return original;
        }
        // Words that start with a consonant have their first letter moved to the end of the word
        // and the letters "ay" added to the end.
        if (original.length() == 1) {
            // No need to move a lone letter
            return original + CONSONANT_SUFFIX;
        }
        String translation = original.substring(1) + original.substring(0, 1);

        // Only words starting with a consonant may get their letters moved resulting in broken capitalization
        return fixCapitalization(original, translation) + CONSONANT_SUFFIX;
    }

    private static String fixCapitalization(String original, String translation) {
        String lowercaseTranslation = translation.toLowerCase();
        StringBuilder fixedTranslationBuilder = new StringBuilder();
        for (int i = 0; i < original.length(); i++) {
            // Capitalization must remain in the same place.
            if (Character.isUpperCase(original.charAt(i))) {
                //TODO change to Unicode uppercase
                fixedTranslationBuilder.append(Character.toUpperCase(lowercaseTranslation.charAt(i)));
            } else {
                fixedTranslationBuilder.append(lowercaseTranslation.charAt(i));
            }
        }

        return fixedTranslationBuilder.toString();
    }

    private static String fixPunctuation(String original, String translation) {
        // No need to fix punctuation if no modifications were made to the original word
        if (original.equals(translation)) {
            return original;
        }
        Matcher translationMatcher = punctuationPattern.matcher(translation);
        // No need to fix punctuation if there is none to begin with
        if (!translationMatcher.find()) {
            return translation;
        }
        String strippedTranslation = translationMatcher.replaceAll("");
        // Punctuation must remain in the same relative place from the end of the word.
        String reversedOriginal = new StringBuilder(original).reverse().toString();
        String reversedTranslation = new StringBuilder(strippedTranslation).reverse().toString();
        Matcher punctuationMatcher = punctuationPattern.matcher(reversedOriginal);

        return restorePunctuation(reversedOriginal, reversedTranslation, punctuationMatcher).reverse().toString();
    }

    private static StringBuilder restorePunctuation(String original, String translation, Matcher punctuationMatcher) {
        StringBuilder fixedPunctuationBuilder = new StringBuilder();
        // Need to keep an offset count after stripping the translation of punctuation characters
        int offset = 0;
        int lastFind = 0;
        while (punctuationMatcher.find()) {
            // Appending translation before the matched character
            fixedPunctuationBuilder.append(translation.substring(lastFind, punctuationMatcher.start() - offset));
            // Appending the matched punctuation character
            fixedPunctuationBuilder.append(original.charAt(punctuationMatcher.start()));
            lastFind = punctuationMatcher.end() - ++offset;
        }
        // Appending rest of the translation after last found punctuation character
        fixedPunctuationBuilder.append(translation.substring(lastFind, translation.length()));

        return fixedPunctuationBuilder;
    }

}
