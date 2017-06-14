package io.github.pwener.jarecord.activerecord;

import io.github.pwener.jarecord.activerecord.finder.Finder;

/**
 * This Singleton is only to keep one static reference of Finder, 
 * to be possible uses object into static {@link ActiveRecord} methods 
 */
public class FinderSingleton {
	private static Finder finder;

	public static Finder get() {
		return finder;
	}

	public static void push(Finder finder) {
		FinderSingleton.finder = finder;
	}
}
