package org.helico.sm.handler;

import org.helico.domain.Dict;
import org.helico.domain.Job;
import org.helico.domain.Text;
import org.helico.service.DictService;
import org.helico.service.JobService;
import org.helico.service.WordService;
import org.helico.sm.StateMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParseHandlerTest {

    @Mock StateMachine stateMachine;
    @Mock WordService wordService;
    @Mock JobService jobService;
    @Mock DictService dictService;

    ParseHandler handler;

    @TempDir
    Path tempDir;

    private final Job job = new Job();

    @BeforeEach
    void setUp() {
        handler = new ParseHandler(stateMachine, wordService, jobService, dictService);
        job.setId(1L);
        job.setDictId(10L);
    }

    @Test
    void countWordsIgnoringCaseAndPunctuation() throws Exception {
        Map<String, Integer> stored = parse("Кот и кот. И пёс!\nПёс, кот?");
        assertEquals(Map.of("кот", 3, "и", 2, "пёс", 2), stored);
    }

    private Map<String, Integer> parse(String content) throws Exception {
        Path file = tempDir.resolve("text.txt");
        Files.writeString(file, content);
        Text text = new Text();
        text.setUtfPath(file.toString());
        Dict dict = new Dict();
        dict.setId(10L);
        dict.setLangId("ru");
        dict.setText(text);
        when(dictService.findDict(10L)).thenReturn(dict);
        Map<String, Integer> stored = new HashMap<>();
        lenient().doAnswer(inv -> {
            stored.merge(inv.getArgument(0), ((Number) inv.getArgument(3)).intValue(), Integer::sum);
            return null;
        }).when(wordService).store(anyString(), anyString(), anyLong(), anyInt());
        handler.process(null, job);
        return stored;
    }
}