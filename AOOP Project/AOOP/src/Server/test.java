package Server;

import java.io.File;
import java.io.IOException;
import java.lang.management.*;

/**
 * Created by Mon on 12/14/13.
 */
public class test {
	public static void main(String[] args) throws IOException {
		final OperatingSystemMXBean myOsBean = ManagementFactory.getOperatingSystemMXBean();
//		while (true) {
//			double load = myOsBean.getSystemLoadAverage();
//			System.out.println(load / 8 * 100);
//		}
		System.out.println(Runtime.getRuntime().availableProcessors());

//		File[] roots = File.listRoots();
//
//		for (File root : roots) {
//			System.out.println("File system root: " + root.getAbsolutePath());
//			System.out.println("Total space (bytes): " + root.getTotalSpace() / 999962804.306);
//			System.out.println("Free space (bytes): " + root.getFreeSpace() / 999962804.306);
//			System.out.println("Usable space (bytes): " + root.getUsableSpace() / 999962804.306);
//		}


		System.out.println(Runtime.getRuntime().totalMemory() / 1000000);
	}

	private void test () {
	}
}
