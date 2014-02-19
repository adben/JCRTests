package org.onehippo.assessment.event;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * our counter
 */
public enum Counter {
	INSTANCE;

	private AtomicInteger atomicCount = new AtomicInteger(0);

	public int increment() {
		return atomicCount.incrementAndGet();
	}

	public int currentValue() {
		return atomicCount.get();
	}
}