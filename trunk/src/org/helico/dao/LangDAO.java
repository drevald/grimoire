package org.helico.dao;

import java.util.List;
import org.helico.domain.Lang;

public interface LangDAO {

    public List<Lang> list();

    public Lang find(String id);

}