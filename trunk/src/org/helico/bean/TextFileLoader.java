package org.helico.bean;

import java.io.InputStream;

public interface TextFileLoader {
	
	public void load(Long dictId, InputStream is);

}
