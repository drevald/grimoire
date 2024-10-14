package org.helico.service;

import org.helico.domain.Dict;
import org.helico.domain.Text;

import java.io.InputStream;
import java.util.List;
import java.io.File;

public interface DictService {

    public Dict findDict(Long id, Long accountId);

    public Dict findDict(Long id);

    public Dict loadPreviewFile(Long id, String langId, InputStream is, String name, String storage);

    public long saveOriginal(Long accountId, String langId, InputStream is, String name, String storage);

    public String getPreview(Long id, String encoding);

    public Dict loadPreviewPdfFile(Long id, String langId, InputStream is, String name, String storage);

    public void saveDict(Dict dict);

    public Dict createDict(Long id, String name);

    public List<Dict> listDicts();

    public List<Dict> listDicts(Long accountId);

    public void removeDict(Long id);

    public void setStatus(Long id, Dict.Status status);

    public void setPreview(Long id, byte[] data);

    public void storeDict(Dict dict);

    public void saveText(Text text);

    public void parseText(Long dictId);

    public void fixStatus();

}