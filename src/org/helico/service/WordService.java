package org.helico.service;

import org.helico.domain.DictWord;

import java.util.List;

/**
 * Created by Wiley Europe, Training & Educational Systems Russia,
 * Created by Wiley Europe, Training & Educational Systems Russia,
 *
 * @author <a href="mailto:dedreval@wiley.com">ddreval</a>
 * @version 17.05.11
 */
public interface WordService {

    public void store(String word, String langId, Long dictId);

    public List<DictWord> getWords(Long dictId);

}
