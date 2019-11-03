package io.zenith391.forgethon.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlParser;

import io.zenith391.forgethon.Forgethon;
import io.zenith391.forgethon.PythonMod;

public class ModLoader {

	public static List<PythonMod> search(File dir) throws IOException {
		List<PythonMod> modList = new ArrayList<>();
		for (File f : dir.listFiles()) {
			if (f.getName().endsWith(".pma")) { // Python Mod Archive
				ZipFile zip = new ZipFile(f);
				ZipEntry config = zip.getEntry("META-INF/mod.toml");
				ZipEntry main = zip.getEntry("src/main/python/main.py");
				if (config == null) {
					Forgethon.LOGGER.error(f.getName() + " is an invalid PMA. Missing 'META-INF/mod.toml'");
					continue;
				}
				if (main == null) {
					Forgethon.LOGGER.error(f.getName() + " is an invalid PMA. Missing 'src/main/python/main.py'");
					continue;
				}
				InputStream is = zip.getInputStream(config);
				
				TomlParser parser = new TomlParser();
				CommentedConfig cfg = parser.parse(is);
				
				String modLoader = cfg.getOrElse("modLoader", "forgethon");
				String modId = cfg.get("modId");
				String version = cfg.get("version");
				String displayName = cfg.get("displayName");
				if (!modLoader.equals("forgethon")) {
					Forgethon.LOGGER.warn(f.getName() + " is not a forgethon python mod.");
					continue;
				}
				if (modId != null) {
					Forgethon.LOGGER.error(f.getName() + " has an invalid 'mod.toml'. Missing modId");
					continue;
				}
				if (version != null) {
					Forgethon.LOGGER.error(f.getName() + " has an invalid 'mod.toml'. Missing version");
					continue;
				}
				if (displayName != null) {
					Forgethon.LOGGER.error(f.getName() + " has an invalid 'mod.toml'. Missing displayName");
					continue;
				}
				is.close();
				is = zip.getInputStream(main);
				String mainCode = "";
				while (is.available() != 0) {
					mainCode += (char) is.read();
				}
				is.close();
				
				PythonMod mod = new PythonMod(modId, version, displayName, mainCode, zip);
				modList.add(mod);
			} else if (f.getName().endsWith(".py")) {
				InputStream is = new FileInputStream(f);
				String mainCode = "";
				while (is.available() != 0) {
					mainCode += (char) is.read();
				}
				
				PythonMod mod = new PythonMod(f.getName(), "1.0", f.getName(), mainCode, null);
				modList.add(mod);
				is.close();
			}
		}
		return modList;
	}
	
}
