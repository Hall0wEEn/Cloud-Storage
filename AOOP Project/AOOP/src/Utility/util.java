package Utility;

import java.io.File;

public class util {
	public static long folderSize(File directory) {
		long length = 0;
		if (directory.isDirectory()) {
			for (File file : directory.listFiles()) {
				length += folderSize(file);
			}
		} else {
			return directory.length();
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
