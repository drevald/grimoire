package org.helico.sm.handler;

import org.helico.service.DictService;
import org.helico.service.JobService;
import org.helico.sm.StateMachine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CleanPdfTextTest {

    private StoreHandler storeHandler = new StoreHandler(null, null, null);

    @Test
    void mergesBrokenLine() {
        assertEquals("один два\n", storeHandler.cleanPdfText("один\nдва"));
    }

    @Test
    void keepsSentenceBreak() {
        assertEquals("Конец.\nНачало\n", storeHandler.cleanPdfText("Конец.\nНачало"));
    }
    @Test
    void joinsHyphenatedWord() {
        assertEquals("слово\n", storeHandler.cleanPdfText("сло-\nво"));
    }

    @Test
    void keepsParagraph() {
        assertEquals("а\n\nб\n",    storeHandler.cleanPdfText("а\n\nб"));
    }

    @Test
    void questionMarkEndsSentence() {
        assertEquals("Да?\nНет\n",  storeHandler.cleanPdfText("Да?\nНет"));
    }

    @Test
    void emptyStaysEmpty() {
        assertEquals("", storeHandler.cleanPdfText(""));
    }
}
