package io.ph.bot.scheduler;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class JobScheduler {
	
	public static Scheduler scheduler;
	
	public static void initializeScheduler() {
		if(scheduler != null)
			return;
		try {
			scheduler = new StdSchedulerFactory("resources/config/quartz.properties").getScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	public static void initializeEventSchedule() {
		
	}
}
