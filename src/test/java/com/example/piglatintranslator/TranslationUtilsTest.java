package com.example.piglatintranslator;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Unit tests for {@link TranslationUtils}.
 */
public class TranslationUtilsTest {

    /**
     * Test of translateText() logic
     */
    @Test(dataProvider = "testData")
    public void testTranslateText(final String original, final String expected) {
        assertEquals(TranslationUtils.translateText(original), expected);
    }


    @DataProvider
    public static Object[][] testData() {
        return new Object[][] {
                {null, null},
                {"", ""},
                {"Hello", "Ellohay"},
                {"apple", "appleway"},
                {"stairway", "stairway"},
                {"can’t", "antca’y"},
                {"end.", "endway."},
                {"this-thing", "histay-hingtay"},
                {"Beach", "Eachbay"},
                {"McCloud", "CcLoudmay"},
                {"c\":a7n’t.", "a7n\":tca’y."},
                {"\tHello apple\tstairway can’t\nend. ― this-thing Beach McCloud\tc\":a7n’t.\n",
                 //TODO preserve original whitespaces
                 " Ellohay appleway stairway antca’y endway. ― histay-hingtay Eachbay CcLoudmay a7n\":tca’y. "
                },
                {"‐this-thing‐sucks-", "‐histay-hingtay‐uckssay-"},
                {"‐this- ‐-‒–—― thing‐sucks-", "‐histay- ‐-‒–—― hingtay‐uckssay-"},
                {" ‐-‒–—― ", " ‐-‒–—― "}
        };
    }

}
