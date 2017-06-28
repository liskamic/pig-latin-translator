package com.example.piglatintranslator;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Utility class providing static
 * TODO hashmap dictionary, possibly loaded from file
 * TODO refactor, create facade
 * TODO use icu4j for working with unicode characters
 * TODO JavaDoc
 *
 */
public class TranslationUtils {

    //TODO move constants to separate class, load from configuration in the future
    // English language definition of Consonant and Vowel
    private static final String CONSONANTS = "bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ";
    private static final String CONSONANT_SUFFIX = "ay";
    private static final String VOWELS = "aeiouAEIOU";
    private static final String VOWEL_SUFFIX = "way";
    private static final String HYPHENS = "\u002D\u2010\u2011\u2012\u2013\u2014\u2015";

    // UNICODE_CHARACTER_CLASS flag set to find all possible punctuation marks
    private static final Pattern punctuationPattern = Pattern.compile("\\p{Punct}",Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern hyphenPattern = Pattern.compile("[" + HYPHENS + "]+", Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern nonHyphenPattern = Pattern.compile("[^" + HYPHENS + "]+", Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern whiteSpacePattern = Pattern.compile("\\p{Space}");


    public static String translateText(String originalText) {
        if (isInvalidInput(originalText)) {
            return originalText;
        }
        //TODO preserve the original whitespaces
            // Either split manually in a stream or use multiple splits for different types of whitespace
        List<String> originalWords = Arrays.asList(originalText.split("\\p{Space}"));
        StringBuilder translatedTextBuilder = new StringBuilder();
        for (String word : originalWords) {
            translatedTextBuilder.append(treatHyphens(word)).append(" ");
        }
        if (!whiteSpacePattern.matcher(originalText.substring(originalText.length() - 1)).find()) {
            translatedTextBuilder.deleteCharAt(translatedTextBuilder.length() - 1);
        }

        return translatedTextBuilder.toString();
    }

    private static String treatHyphens(String originalWord) {
        // No need to continue if there are no hyphens or only hyphens
        if (!hyphenPattern.matcher(originalWord).find() || !nonHyphenPattern.matcher(originalWord).find()) {
            return translateWord(originalWord);
        }
        StringBuilder translatedTextBuilder = new StringBuilder();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < originalWord.length(); i++) {
            if(HYPHENS.contains(String.valueOf(originalWord.charAt(i)))) {
                translatedTextBuilder.append(translateWord(buffer.toString()));
                translatedTextBuilder.append(originalWord.charAt(i));
                buffer.setLength(0);
            } else {
                buffer.append(originalWord.charAt(i));
            }
        }
        translatedTextBuilder.append(translateWord(buffer.toString()));
        
        return translatedTextBuilder.toString();
    }

    private static String translateWord(String original) {
        if (isInvalidInput(original) ||
                // Words that end in "way" are not modified.
                (original.length() > 2 && original.substring(original.length() - 3).equals(VOWEL_SUFFIX))) {
            return original;
        }
        // Words that start with a vowel have the letters "way" added to the end.
        String translation = isVowel(original.substring(0, 1)) ? original + VOWEL_SUFFIX : modifyForConsonant(original);

        return fixPunctuation(original, translation);
    }

    private static boolean isInvalidInput(String original) {
        return original == null || original.isEmpty();
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
