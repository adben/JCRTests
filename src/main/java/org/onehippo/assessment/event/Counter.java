package org.onehippo.assessment.event;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * our counter
 */
public class Counter {

	AtomicInteger atomicCount = new AtomicInteger(0);


	public int getCountAtomically() {
		return atomicCount.incrementAndGet();
	}
}