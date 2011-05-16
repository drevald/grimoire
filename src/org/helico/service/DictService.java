package org.helico.service;

import org.helico.domain.Dict;

import java.io.InputStream;
import java.util.List;

public interface DictService {
	
    public Dict findDict(Long id);

    public Dict loadPreviewFile(Long id, InputStream is, String name);

    public void saveDict(Dict dict);

    public Dict createDict(Long id, String name);

    public List<Dict> listDicts();
	
    public void removeDict(Long id);
    
    public void setStatus(Long id, Dict.Status status);

    public void setPreview(Long id, byte[] data);

    public void storeDict(Dict dict);

    public void parseText(Long dictId);
    
}