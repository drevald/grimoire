package org.helico.service;

import org.helico.domain.Dict;
import org.helico.domain.Job;
import org.helico.domain.Text;

import java.io.InputStream;
import java.util.List;

public interface DictService {

    Dict findDict(Long id, Long accountId);

    Dict findDict(Long id);

    long saveOriginal(Long accountId, String langId, InputStream is, String name, String storage);

    String getPreview(Long id, String encoding);

    void saveDict(Dict dict);

    List<Dict> listDicts();

    void removeDict(Long id);

    void setStatus(Long id, Dict.Status status);

    void fixStatus();

    List<DictWithLastJob> listDictsWithLastJob(Long accountId);

}

