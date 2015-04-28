package ro.project.thread;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.pattern.IntegerPatternConverter;

import ro.project.Constants;
import ro.project.scheduler.Scheduler;
import ro.project.servlet.ServletToScheduler;

public class ThreadScheduler implements Runnable{
	final static Logger logger = Logger.getLogger(ThreadScheduler.class);
	private volatile boolean isStopped = false;

	private int intervalToCheckToPost = 1;
	private int startHour;
	private int endHour;
	private int startMinutes;
	private int endMinutes;
	private String pathToFile;
	private String path2;
	private String radios;
	private String[] where;
	private Integer[] numbers;
	private String yearDropDown;
	private String monthDropDown;
	private String dayDropDown;
	private String hourDropDown;
	private String minuteDropDown;
	private String gmtDropDown;
	private String dayDropDown2;
	private String hourDropDown2;
	private String minuteDropDown2;
	private String numberofQuotes;
	private String myFile;
	private ServletToScheduler servletToScheduler;

	private static ThreadScheduler instance = null;

	protected ThreadScheduler() {
	}

	public static ThreadScheduler getInstance() {
		if (instance == null) {
			instance = new ThreadScheduler();
		}
		return instance;
	}

	private void readFromFile() {
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(pathToFile + Constants.CONFIG_PROPERTIES);

			prop.load(input);

			path2 = prop.getProperty(Constants.PATH);
			radios = prop.getProperty(Constants.RADIOS);
			int whereSize = Integer.parseInt(prop.getProperty(Constants.WHERE_SIZE));
			where = new String[whereSize];
			numbers = new Integer[whereSize];
			
			for (int i = 0; i < whereSize; i++) {
				where[i] = prop.getProperty(Constants.WHERE + i);
				System.out.println(where[i]);
				numbers[i] = Integer.parseInt(prop.getProperty(where[i]));
			}
			
			yearDropDown = prop.getProperty(Constants.YEAR_DROP_DOWN);
			monthDropDown = prop.getProperty(Constants.MONTH_DROP_DOWN);
			dayDropDown = prop.getProperty(Constants.DAY_DROP_DOWN);
			hourDropDown = prop.getProperty(Constants.HOUR_DROP_DOWN);
			minuteDropDown = prop.getProperty(Constants.MINUTE_DROP_DOWN);
			gmtDropDown = prop.getProperty(Constants.GMT_DROP_DOWN);
			dayDropDown2 = prop.getProperty(Constants.DAY_DROP_DOWN_2);
			hourDropDown2 = prop.getProperty(Constants.HOUR_DROP_DOWN_2);
			minuteDropDown2 = prop.getProperty(Constants.MINUTE_DROP_DOWN_2);
			numberofQuotes = prop.getProperty(Constants.NUMBER_OF_POSTS);
			myFile = prop.getProperty(Constants.MYFILE);
			startHour = Integer.parseInt(hourDropDown);
			endHour = Integer.parseInt(hourDropDown2);
			startMinutes = Integer.parseInt(minuteDropDown);
			endMinutes = Integer.parseInt(minuteDropDown2);

		} catch (IOException ex) {
			logger.error(ex.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}

	}

	private void schedulePosts(int hour, int minute) {
		servletToScheduler.postToSocialMedia(path2, radios, where, yearDropDown, monthDropDown, dayDropDown,
				hourDropDown, minuteDropDown, gmtDropDown, dayDropDown2, hourDropDown2, minuteDropDown2,
				numberofQuotes, hour, minute, myFile, numbers);
	}

	public synchronized void doStop() {
		isStopped = true;
		notify();
	}

	public synchronized boolean isStopped() {
		return isStopped;
	}

	public void setPath(String path2) {
		this.pathToFile = path2;

	}

	public void setScheduler(Scheduler scheduler) {
		if (servletToScheduler == null) {
			servletToScheduler = new ServletToScheduler(scheduler);
		}
	}

	public void setInterval(int interval) {
		this.intervalToCheckToPost = interval;
	}

	@Override
	public void run() {
		while (!isStopped()) {
			if (pathToFile != null && servletToScheduler != null) {
				readFromFile();

				Calendar now = Calendar.getInstance();
				int hour = now.get(Calendar.HOUR_OF_DAY);
				int minute = now.get(Calendar.MINUTE);
				int t = 0;

				boolean test = false;
				if (hour > startHour && hour < endHour) {
					test = true;
				} else if (hour == startHour && hour < endHour) {
					if ((minute >= startMinutes && minute <= endMinutes) || (minute >= startMinutes && endMinutes >= 0)) {
						test = true;
					} else {
						test = false;
					}
				} else if (hour == startHour && hour == endHour) {
					if (minute >= startMinutes && minute <= endMinutes) {
						test = true;
					} else {
						test = false;
					}

				}

				if (test) {
					schedulePosts(hour, minute);
					int h = Integer.parseInt(hourDropDown2) - Integer.parseInt(hourDropDown);
					int m = Integer.parseInt(minuteDropDown2) - Integer.parseInt(minuteDropDown);
					if (m < 0) {
						t = (h - 1) * 60 + 60 - m + 10;
					} else {
						t = h * 60 + m + 10;
					}

					try {
						synchronized (this) {
							this.wait(t * 60 * 1000);
						}
					} catch (InterruptedException e) {
						logger.error(e.getMessage());
					}
				} else {
					try {
						synchronized (this) {
							this.wait(intervalToCheckToPost * 60 * 1000);
						}
					} catch (InterruptedException e) {
						logger.error(e.getMessage());
					}
				}
			} else {
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						logger.error(e.getMessage());
					}
				}
			}
		}
		
	}

}
