package ro.project.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

public class SchedulerContextListener implements ServletContextListener {
	final static Logger logger = Logger.getLogger(SchedulerContextListener.class);
	
	private SchedulerThread threadScheduler = null;
	ExecutorService executor = null;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		if ((threadScheduler == null)) {
			threadScheduler = SchedulerThread.getInstance();
			ExecutorService executor = Executors.newFixedThreadPool(1);
			executor.execute(threadScheduler);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		try {
			threadScheduler.doStop();
			executor.shutdownNow();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}
}
