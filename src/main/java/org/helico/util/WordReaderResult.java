package org.helico.util;

/**
 * Created with IntelliJ IDEA.
 * Account: ddreval
 * Date: 05.06.14
 * Time: 13:44
 * To change this template use File | Settings | File Templates.
 */
public class WordReaderResult {

    String result;
    boolean isWord;

    public String getResult() {
        return result;
    }

    public boolean isWord() {
        return isWord;
    }

    public WordReaderResult(String result, boolean word) {
        this.result = result;
        isWord = word;
    }

}
