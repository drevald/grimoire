package org.helico.dao;

import java.util.List;
import org.helico.domain.Dict;

public interface DictDAO {
	
    public long saveDict(Dict user);
	
    public List<Dict> listDicts();
	
    public void removeDict(Long id);

    public Dict findDict(Long id);
    
}
