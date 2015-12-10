package org.helico.util;

import org.apache.log4j.Logger;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: ddreval
 * Date: 05.06.14
 * Time: 13:38
 * To change this template use File | Settings | File Templates.
 */
public class WordReader extends Reader {

    private static final Logger LOG = Logger.getLogger(WordReader.class);

    private Reader reader;

    private static final String UTF8_ENC = "utf-8";

    private boolean reading = false;

    private boolean readingWord = false;

    private StringBuilder sb = new StringBuilder();

    private int counter = 0;

    public WordReader(Reader aReader) {
            reader = aReader;
    }

    public WordReader(InputStream is) {
        try {
            reader = new InputStreamReader(is, UTF8_ENC);
        } catch (UnsupportedEncodingException e) {
            LOG.error("UTF-8 is not supported? Incredible!");
        }
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return reader.read(cbuf, off, len);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean ready() throws  IOException {
        return reader.ready();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    public WordReaderResult readWord() throws Exception {

        WordReaderResult result = null;

        int ch = 0;

        while (reader.ready() && (ch > -1)) {

            ch = reader.read();

//            if (ch == -1) {
//                break;
//            }

            counter++;

            if (!Character.isLetter(ch) && readingWord) {
                LOG.trace("WORD:"+sb.toString());
                result = new WordReaderResult(sb.toString(), true);
                sb.delete(0, sb.length());
                try {
                    sb.append(Character.toChars(ch));
                } catch (IllegalArgumentException e) {
                    LOG.error("Failed to parse " + ch + " HEX " + Integer.toHexString(ch));
                }
                readingWord = false;
                break;
            } else if (!Character.isLetter(ch) && !readingWord) {
                try {
                    sb.append(Character.toChars(ch));
                } catch (IllegalArgumentException e) {
                    LOG.error("Failed to convert " + ch);
                }
            } else if (Character.isLetter(ch) && readingWord) {
                sb.append(Character.toChars(ch));
            } else if (Character.isLetter(ch) && !readingWord) {
                result = new WordReaderResult(sb.toString(), false);
                sb.delete(0, sb.length());
                sb.append(Character.toChars(ch));
                readingWord = true;
                break;
            }

        }

        return result;

    }

    public int getCounter() {
        return counter;
    }

}