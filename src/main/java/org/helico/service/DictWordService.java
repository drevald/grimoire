package org.helico.service;

import java.util.List;
import org.helico.domain.DictWord;

public interface DictWordService {

    public List<DictWord> getWords(Long dictId);

    public List<DictWord> getWords(Long dictId, Integer offset, Integer num);

    public Long countWords(Long dictId);

}
