package org.example;

import static org.quartz.DateBuilder.evenMinuteDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;
import org.example.jobs.HelloJob;
import org.quartz.DateBuilder;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    try {
      final var scheduler = StdSchedulerFactory.getDefaultScheduler();

      scheduler.start();
      logger.info("Scheduler started");

      // define the job and tie it to our HelloJob class
      final var job = newJob(HelloJob.class)
          .withIdentity("job1", "group1")
          .usingJobData("someId", 123)
          .usingJobData("anotherId", "01234567890abcde")
          .build();

      // compute a time that is on the next round minute
      final var runTime = DateBuilder.evenMinuteDateAfterNow();

      // Trigger the job to run on the next round minute
      final var trigger = newTrigger()
          .withIdentity("trigger1", "group1")
          .startAt(runTime)
          .build();

      // Tell quartz to schedule the job using our trigger
      scheduler.scheduleJob(job, trigger);

      try {
        Thread.sleep(5 * 60 * 1000);
      } catch (InterruptedException e) {
        // NOOP here
      } finally {
        scheduler.shutdown();
        logger.info("Scheduler shutdown");
      }

      scheduler.shutdown();
    } catch (SchedulerException e) {
      throw new RuntimeException(e);
    }
  }
}
