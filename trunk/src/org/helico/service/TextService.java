package org.helico.service;

import java.io.InputStream;
import java.io.Reader;

/**
 * Created with IntelliJ IDEA.
 * User: ddreval
 * Date: 05.06.14
 * Time: 17:38
 * To change this template use File | Settings | File Templates.
 */
public interface TextService {

    public Reader getTextReader(Long id)  throws Exception;

    public Reader getTextReader(Long id, int offset, int len) throws Exception;


}
