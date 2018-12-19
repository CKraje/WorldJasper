package it.objectmethod.worldjasper.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class Scheduler {

	@Value("${job.fixedrate.enable}")
	boolean enableFixedRateJob;


	@Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}", initialDelayString 
			= "${initialDelay}")
	public void scheduleFixedDelayTask() {
		System.out.println(
				"Fixed delay task - " + System.currentTimeMillis() / 1000);
	}

	@Scheduled(fixedRateString = "${fixedRate.in.milliseconds}")
	public void scheduleFixedRateTask() {
		if (enableFixedRateJob) {
			System.out.println("Fixed rate task - " + System.currentTimeMillis() / 1000);
		}
	}
	
	@Scheduled(cron="${cron.expression}")
	public void scheduleTaskUsingCronExpression() {
		System.out.println("Cron Expression Task - " + System.currentTimeMillis() / 1000);
	}
}
