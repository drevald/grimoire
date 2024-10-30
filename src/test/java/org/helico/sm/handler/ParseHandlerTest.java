package org.helico.sm.handler;

import org.helico.domain.Dict;
import org.helico.domain.Job;
import org.helico.domain.Text;
import org.helico.service.DictService;
import org.helico.service.JobService;
import org.helico.service.WordService;
import org.helico.util.WordReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ParseHandler.class)
class ParseHandlerTest {

    @Mock
    private WordService wordService;

    @Mock
    private JobService jobService;

    @InjectMocks
    private ParseHandler parseHandler;

    @Mock
    private DictService dictService;

    Job job;

    Map<String, Integer> wordMap;

    @BeforeEach
    void setUp() throws Exception {

        wordMap = new HashMap<>();

        MockitoAnnotations.openMocks(this);
        Dict dict = new Dict();
        dict.setLangId("en");
        dict.setId(1L);
        Text text = new Text();
        job = new Job();
        job.setDictId(1L);
        text.setUtfPath("src/test/resources/counted_text_300.txt");
        dict.setText(text);

        doAnswer(invocation -> {
            String word = invocation.getArgument(0);
            int counter = wordMap.get(word) == null ? 1 : wordMap.get(word) + 1;
            wordMap.put(word, counter);
            return null;
        }).when(wordService).store(anyString(), anyString(), anyLong());

        when(dictService.findDict(1L)).thenReturn(dict);
        injectWordService(parseHandler, wordService);
        injectDictService(parseHandler, dictService);
    }

    private void injectDictService(Object target, DictService dictService) throws Exception {
        Field field = target.getClass().getDeclaredField("dictService");
        field.setAccessible(true);
        field.set(target, dictService);
    }

    private void injectWordService(Object target, WordService wordService) throws Exception {
        Field field = target.getClass().getDeclaredField("wordService");
        field.setAccessible(true);
        field.set(target, wordService);
    }

    @Test
    void testProcess() throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("sample text".getBytes(StandardCharsets.UTF_8));
        WordReader mockReader = new WordReader(inputStream);
        parseHandler.process(new Object(), job);
        assert(wordMap.get("threehundred") == 300);
        assert(wordMap.get("eleven") == 11);
        assert(wordMap.get("seventeen") == 17);
    }
}

