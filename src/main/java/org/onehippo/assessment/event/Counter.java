package org.onehippo.assessment.event;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * our counter
 */
public class Counter {

	AtomicInteger atomicCount = new AtomicInteger(0);


	/*
	 * This method is thread-safe because count is incremented atomically
	 */
	public int getCountAtomically() {
		return atomicCount.incrementAndGet();
	}
}