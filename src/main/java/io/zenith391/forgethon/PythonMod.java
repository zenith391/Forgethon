package io.zenith391.forgethon;

import java.util.zip.ZipFile;

import org.python.util.PythonInterpreter;

public class PythonMod {

	protected String modId, displayName, version;
	protected String mainCode;
	protected ZipFile zip;
	protected PythonInterpreter intr;
	
	public PythonMod(String modId, String ver, String displayName, String mainCode, ZipFile zip) {
		this.modId = modId;
		this.displayName = displayName;
		version = ver;
		this.zip = zip;
		this.mainCode = mainCode;
	}
	
	public String getId() {
		return modId;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getMainCode() {
		return mainCode;
	}
	
	public PythonInterpreter getInterpreter() {
		return intr;
	}
	
	public void construct() {
		intr = new PythonInterpreter();
		intr.set("logger", Forgethon.LOGGER);
		intr.exec(mainCode);
	}
	
}
