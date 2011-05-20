package org.helico.dao;

import org.helico.domain.Word;

/**
 * Created by Wiley Europe, Training & Educational Systems Russia,
 * Created by Wiley Europe, Training & Educational Systems Russia,
 *
 * @author <a href="mailto:dedreval@wiley.com">ddreval</a>
 * @version 17.05.11
 */
public interface WordDAO {

    public Word store(String word, Long langId);

}
