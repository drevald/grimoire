package org.helico.util;

import junit.framework.TestCase;

import java.io.InputStream;
import java.io.StringBufferInputStream;

/**
 * Created by ddreval on 11.12.2015.
 */
public class WordReaderTest extends TestCase {

    public void test() throws Exception {

        InputStream is = new StringBufferInputStream("In god we trust");
        WordReader wordReader = new WordReader(is);

        assertTrue(wordReader.ready());

        WordReaderResult resultOne = wordReader.readWord();
        assertTrue(resultOne.isWord());
        assertTrue(resultOne.getResult().equals("In"));

        WordReaderResult resultTwo = wordReader.readWord();
        assertFalse(resultTwo.isWord());
        assertTrue(resultTwo.getResult().equals(" "));

        WordReaderResult resultThree = wordReader.readWord();
        assertTrue(resultThree.isWord());
        assertTrue(resultThree.getResult().equals("god"));

        WordReaderResult resultFour = wordReader.readWord();
        assertFalse(resultFour.isWord());
        assertTrue(resultFour.getResult().equals(" "));

        WordReaderResult resultFive = wordReader.readWord();
        assertTrue(resultFive.isWord());
        assertTrue(resultFive.getResult().equals("we"));

        WordReaderResult resultSix = wordReader.readWord();
        assertFalse(resultSix.isWord());
        assertTrue(resultSix.getResult().equals(" "));

        WordReaderResult resultSeven = wordReader.readWord();
        assertTrue(resultSeven.isWord());
        assertTrue(resultSeven.getResult().equals("trust"));

        assertFalse(wordReader.ready());

    }

}
