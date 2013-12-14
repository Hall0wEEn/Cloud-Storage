package Utility;

import java.io.File;

public class util {
	public static long folderSize(File directory) {
		long length = 0;
		for (File file : directory.listFiles()) {
			if (file.isFile())
				length += file.length();
			else
				length += folderSize(file);
		}
		return length;
	}

	public static boolean isWin() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	public static String getExt(File file) {
		String extension = "";

		int i = file.toString().lastIndexOf('.');
		if (i > 0) {
			extension = file.toString().substring(i + 1);
		}
		return extension;
	}
}
