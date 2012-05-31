package org.helico.dao;

import org.helico.domain.Dict;
import org.helico.domain.Text;

import java.util.List;

public interface DictDAO {
	
    public long saveDict(Dict dict);

    public long saveText(Text text);

    public List<Dict> listDicts();

    public List<Dict> listDicts(Long userId);
	
    public void removeDict(Long id);

    public Dict findDict(Long id, Long userId);

    public Dict findDict(Long id);

    public void fixStatus();

}
