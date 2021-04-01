package com.luxoft.parallel;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ParallelTest {

	static class MutableNonSafeInt {
	    private int i = 0;

	    public void increase() {
	        i++;
	    }

	    public int get() {
	        return i;
	    }
	}
	
	private static long tickTime = 0;
	public static void nextTick() {
		long now = new Date().getTime();
		long diff = now - tickTime;
		if (tickTime!=0) {
			System.out.println("time passed: "+diff);
		}
		tickTime = now;
	}
	static int ITERATIONS =  1_000_000;

	public static void main(String[] args) {
		nextTick();
		
		MutableNonSafeInt integer = new MutableNonSafeInt();
		IntStream.range(0, ITERATIONS)
		        .forEach(i -> integer.increase());
		System.out.println(integer.get());

		nextTick();
		
		// This will print 1000000 as expected no matter what happens, 
		// even though it depends on the previous state.

		// Now let's parallelize the stream:
		MutableNonSafeInt integer2 = new MutableNonSafeInt();
		int j=0;
		IntStream.range(0, ITERATIONS)
		        .parallel()
		        .unordered()
		        .forEach(i -> integer2.increase());
		System.out.println(integer2.get());

		nextTick();
		
		/*
		 * Now it prints different integers, like 199205, or 249165, 
		 * because other threads are not always seeing the changes that 
		 * different threads have made, because there is no synchronization.
		 * But say that we now get rid of our dummy class and use the AtomicInteger, 
		 * which is thread-safe, we get the following:
		 */
		AtomicInteger integer3 = new AtomicInteger(0);
		IntStream.range(0, ITERATIONS)
		        .parallel()
		        .unordered()
		        .forEach(i -> integer3.incrementAndGet());
		System.out.println(integer3.get());

		nextTick();

		// Now it correctly prints 1000000 again.
		
		//Synchronization is costly however, and we have lost nearly all 
		// benefits of parallellization here.
	}
}
