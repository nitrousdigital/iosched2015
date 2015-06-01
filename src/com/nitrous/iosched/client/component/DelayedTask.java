package com.nitrous.iosched.client.component;

import com.google.gwt.user.client.Timer;

/**
 * Defer execution by a specified amount of time. Each time the task is
 * scheduled the previous schedule is cancelled. This can be used to ensure a
 * task is not executed too frequently (e.g. hanlding window resize events)
 * 
 * @author nitrousdigital
 *
 */
public abstract class DelayedTask {
	private Timer timer;

	public DelayedTask() {
		timer = new Timer() {
			@Override
			public void run() {
				DelayedTask.this.run();
			}
		};
	}

	public void schedule(int millis) {
		timer.cancel();
		timer.schedule(millis);
	}

	public void cancel() {
		timer.cancel();
	}

	public abstract void run();

}
