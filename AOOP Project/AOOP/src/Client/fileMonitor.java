package Client;

/**
 * Created with IntelliJ IDEA.
 * User: Touch
 * Date: 11/16/13
 * Time: 8:33 PM
 * To change this template use File | Settings | File Templates.
 */

import Utility.operationCode;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Example to watch a directory (or tree) for changes to files.
 */

public class fileMonitor implements Runnable {

	private final WatchService watcher;
	private final Map<WatchKey, Path> keys;
	private final MenuItem menuItem;
	private boolean trace = false;

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void register(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		if (trace) {
			Path prev = keys.get(key);
			if (prev == null) {
				System.out.format("register: %s\n", dir);
			} else {
				if (!dir.equals(prev)) {
					System.out.format("update: %s -> %s\n", prev, dir);
				}
			}
		}
		keys.put(key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void registerAll(final Path start) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
					throws IOException {
				register(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * Creates a WatchService and registers the given directory
	 */
	fileMonitor(Path dir, MenuItem menuItem) throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, Path>();
		this.menuItem = menuItem;

		System.out.format("Scanning %s ...\n", dir);
		registerAll(dir);
		System.out.println("Done.");
		this.trace = true;
	}

	public static void uploadFiles(File file) {
		Thread t;
		try {
			if (file.isDirectory()) {
				for (File tmp : file.listFiles()) {
					uploadFiles(tmp);
				}
			} else {
				if (file.getName().charAt(0) == '.')
					return;
				t = new Thread(new send(operationCode.UPLOAD, file));
				t.start();
				t.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Process all events for keys queued to the watcher
	 */
	public void run() {
		while (true) {
			// wait for key to be signalled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				System.err.println("WatchKey not recognized!!");
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind kind = event.kind();

				// TBD - provide example of how OVERFLOW event is handled
				if (kind == OVERFLOW) {
					continue;
				}

				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);
				File file = child.toFile();

				// print out event
				System.out.format("%s: %s\n", event.kind().name(), child);

				// if directory is created, and watching recursively, then
				// register it and its sub-directories
				if (file.getName().charAt(0) == '.')
					continue;

				Thread t;
				if (kind == ENTRY_CREATE) {
					try {
						if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
							registerAll(child);
						}
					} catch (IOException x) {
						// ignore to keep sample readbale
					}

					uploadFiles(file);

				} else if (kind == ENTRY_DELETE) {
					t = new Thread(new send(operationCode.DELETE, file.getName(), ""));
					t.start();
					try {
						t.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else if (kind == ENTRY_MODIFY) {
					if (child.toFile().exists())
						t = new Thread(new send(operationCode.UPLOAD, file));
					else
						t = new Thread(new send(operationCode.DELETE, file.getName(), ""));

					t.start();
					try {
						t.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			try {
				send tmp = new send(operationCode.SPACE, "", "");
				Thread t = new Thread(tmp);
				t.start();
				t.join();
				double spaceused = Double.parseDouble(tmp.getUsedSpace());
				menuItem.setLabel("Space Used: " + spaceused / (1000 * 1000) + "MB");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty()) {
					break;
				}
			}
		}
	}
}