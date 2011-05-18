package org.helico.service;

import org.helico.domain.Lang;
import java.util.List;

public interface LangService {

    public List<Lang> list();

    public String[] getEncodings(Long id);

}