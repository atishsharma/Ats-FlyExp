package com.atsexp.fly.fileobserver;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public final class FileObserverCache {

	private static FileObserverCache instance;

	public static FileObserverCache getInstance() {
		if (instance == null) {
			instance = new FileObserverCache();
		}
		return instance;
	}

	private final Map<String, WeakReference<MultiFileObserver>> cache;

	private FileObserverCache() {
		this.cache = new HashMap<String, WeakReference<MultiFileObserver>>();
	}

	public void clear() {
		this.cache.clear();
	}

	public MultiFileObserver getOrCreate(final String path) {
		final WeakReference<MultiFileObserver> reference = cache.get(path);
		MultiFileObserver observer;
		if (reference != null && (observer = reference.get()) != null) {
			return observer;
		} else {
			observer = new MultiFileObserver(path);
			this.cache
					.put(path, new WeakReference<MultiFileObserver>(observer));
		}
		return observer;
	}
}
