package org.helico;

import org.helico.util.WordReader;
import org.helico.util.WordReaderResult;

import java.io.FileInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: ddreval
 * Date: 05.06.14
 * Time: 14:06
 * To change this template use File | Settings | File Templates.
 */
public class WordReaderTest {

    public static void main (String[] args) throws Exception {

        System.out.println("Started");

        FileInputStream fileInputStream = new FileInputStream("C:\\grim\\SnowQueen.txt.1401456453702");
        WordReader wordReader = new WordReader(fileInputStream);
        while (wordReader.ready()) {
            WordReaderResult result = wordReader.readWord();
            if (result.isWord())
            System.out.println("Word is " + result.getResult());
            else
            System.out.println("Text is \'" + result.getResult()+"\'");
        }

        System.out.println("Done");

    }

}
