package org.helico.dao;

import org.helico.domain.Dict;

import java.util.List;

public interface DictDAO {
	
    public long saveDict(Dict user);
	
    public List<Dict> listDicts();
	
    public void removeDict(Long id);

    public Dict findDict(Long id);
    
}
