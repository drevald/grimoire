package org.helico.bean;

import java.io.InputStream;

public interface Counter {

	public void count();
	
	public void load(InputStream is);
	
}
