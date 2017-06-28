package com.example.piglatintranslator;

import org.testng.annotations.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.regex.Pattern;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Unit test for command line facade
 */
public class AppTest {

    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private int index = 0;

    private final Pattern separatorPattern = Pattern.compile("\r\n");


    /**
     * Testing multiple string inputs
     */
    @Test(dataProvider = "testData")
    public void testCommandLineCall(final String[] input, final String expected) {
        App.main(input);
        assertTrue(errContent.size() == 0);
        String output = separatorPattern.matcher(outContent.toString()).replaceAll("\n");
        assertEquals(output.substring(index), expected);
        index = output.length();
    }


    @BeforeSuite
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterSuite
    public void cleanUpStreams() throws IOException {
        outContent.close();
        errContent.close();
        System.setOut(null);
        System.setErr(null);
    }


    @DataProvider
    public static Object[][] testData() {
        return new Object[][] {
                {new String[] {},
                 "usage: average s1 [s2 ...]\n        s1, s2, etc.: strings to be translated into Pig Latin\n"
                },
                {new String[] {""}, "\n"},
                {new String[] {"This thing is overengineered as hell -_-"},
                 "Histay hingtay isway overengineeredway asway ellhay -_-\n"
                },
                {new String[] {"This","thing","is","overengineered","as","hell","-_-"},
                 "Histay\nhingtay\nisway\noverengineeredway\nasway\nellhay\n-_-\n"
                }
        };
    }


    /**Writes to nowhere*/
    public class NullOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {
            // /dev/null
        }
    }

}
