package io.zenith391.forgethon;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

public class PythonMod {

	protected String name, description, version;
	protected ZipFile zip;
	
	public PythonMod(String name, String desc, String ver, ZipFile zip) {
		this.name = name;
		description = desc;
		version = ver;
		this.zip = zip;
	}
	
	public InputStream getMainFile() throws IOException {
		return zip.getInputStream(zip.getEntry("main.py"));
	}
	
}
