package org.trident.util;


/**
 * A simple timing utility.
 *
 * @author blakeman8192
 * @author lare96
 */
public class Stopwatch {

	/** The cached time. */
	private long time = System.currentTimeMillis();

	/**
	 * Resets with a head start option.
	 *
	 * @param startAt
	 * the head start value.
	 */
	public Stopwatch headStart(long startAt) {
		time = System.currentTimeMillis() - startAt;
		return this;
	}

	/**
	 * Resets this stopwatch.
	 *
	 * @return this stopwatch.
	 */
	public Stopwatch reset() {
		time = System.currentTimeMillis();
		return this;
	}

	/**
	 * Returns the amount of time elapsed since this object was initialized, or
	 * since the last call to the <code>reset()</code> method.
	 *
	 * @return the elapsed time, in milliseconds.
	 */
	public long elapsed() {
		return System.currentTimeMillis() - time;
	}
}