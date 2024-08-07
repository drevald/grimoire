package org.helico.dao;

import org.helico.domain.Dict;
import org.helico.domain.Text;

import java.util.List;


/**
 * written by ${name}
 */
public interface DictDAO {

    public long saveDict(Dict dict);

    public void saveText(Text text);

    public List<Dict> listDicts();

    public List<Dict> listDicts(Long accountId);

    public void removeDict(Long id);

    public Dict findDict(Long id, Long accountId);

    public Dict findDict(Long id);

    public List<Dict> findDictByStatus(String state);

}
