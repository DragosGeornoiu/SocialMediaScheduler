package ro.project.thread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Properties;

import org.apache.log4j.Logger;

import ro.project.Constants;
import ro.project.scheduler.Scheduler;
import ro.project.servlet.ServletToScheduler;

public class SchedulerThread implements Runnable {
	final static Logger logger = Logger.getLogger(SchedulerThread.class);
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
	private Integer[] posted;
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
	private String when;
	private int whereSize;

	private String calendarYear;
	private String calendarMonth;
	private String calendarDay;

	private static SchedulerThread instance = null;

	protected SchedulerThread() {
	}

	public static SchedulerThread getInstance() {
		if (instance == null) {
			instance = new SchedulerThread();
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
			String size = prop.getProperty(Constants.WHERE_SIZE);
			whereSize = Integer.parseInt(size);
			where = new String[whereSize];

			numbers = new Integer[whereSize];
			posted = new Integer[whereSize];

			for (int i = 0; i < whereSize; i++) {
				where[i] = prop.getProperty(Constants.WHERE + i);
				String post = prop.getProperty("posted." + where[i]);
				if (post != null) {
					posted[i] = Integer.parseInt(post);
				} else {
					posted[i] = 0;
				}
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

			String date = prop.getProperty(Constants.CALENDAR_DATE);
			if (date != null) {
				calendarYear = date.split(" - ")[0];
				calendarMonth = date.split(" - ")[1];
				calendarDay = date.split(" - ")[2];
			}

			when = prop.getProperty(Constants.WHEN);
			myFile = prop.getProperty(Constants.MYFILE);
			startHour = Integer.parseInt(hourDropDown);
			endHour = Integer.parseInt(hourDropDown2);
			startMinutes = Integer.parseInt(minuteDropDown);
			endMinutes = Integer.parseInt(minuteDropDown2);
			whereSize = Integer.parseInt(prop.getProperty(Constants.WHERE_SIZE));

		} catch (Exception ex) {
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

	private String schedulePosts(int hour, int minute) {
		Integer[] finals = new Integer[whereSize];
		if (calendarYear != null && calendarMonth != null && calendarDay != null) {
			Calendar now = Calendar.getInstance();
			if (Integer.parseInt(calendarDay) == now.get(Calendar.DAY_OF_MONTH)
					&& Integer.parseInt(calendarMonth) == now.get(Calendar.MONTH)
					&& Integer.parseInt(calendarYear) == now.get(Calendar.YEAR)) {
				for (int i = 0; i < whereSize; i++) {
					if (posted[i] == null) {
						posted[i] = 0;
					}
					finals[i] = numbers[i] - posted[i];
				}

				return servletToScheduler.postToSocialMedia(path2, radios, where, yearDropDown, monthDropDown,
						dayDropDown, hourDropDown, minuteDropDown, gmtDropDown, dayDropDown2, hourDropDown2,
						minuteDropDown2, numberofQuotes, hour, minute, myFile, finals);

			} else {
				emptyPostedQuotes();
				return servletToScheduler.postToSocialMedia(path2, radios, where, yearDropDown, monthDropDown,
						dayDropDown, hourDropDown, minuteDropDown, gmtDropDown, dayDropDown2, hourDropDown2,
						minuteDropDown2, numberofQuotes, hour, minute, myFile, numbers);
			}

		} else {
			return servletToScheduler.postToSocialMedia(path2, radios, where, yearDropDown, monthDropDown, dayDropDown,
					hourDropDown, minuteDropDown, gmtDropDown, dayDropDown2, hourDropDown2, minuteDropDown2,
					numberofQuotes, hour, minute, myFile, numbers);
		}
	}

	private void emptyPostedQuotes() {
		Properties prop = new Properties();
		OutputStream output = null;
		InputStream input = null;

		try {
			File file = new File(path2 + Constants.CONFIG_PROPERTIES);
			input = new FileInputStream(file);
			prop.load(input);

			int size = Integer.parseInt(prop.getProperty(Constants.PROFILESIZES));
			String[] temp = new String[size];
			for (int i = 0; i < size; i++) {
				temp[i] = prop.getProperty(Constants.PROFILES + i);
			}

			for (int i = 0; i < size; i++) {
				if ((prop.getProperty(temp[i])) != null) {
					prop.setProperty(Constants.POSTED + temp[i], "0");
					prop.setProperty(temp[i], "0");
				}
			}

			output = new FileOutputStream(file);
			prop.store(output, null);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

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

				int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
				if (when != null) {
					if (when.equals(Constants.Workdays)) {
						if (dayOfWeek == 1 || dayOfWeek == 7) {
							test = false;
						}
					}
				}

				if (test) {
					String result = schedulePosts(hour, minute);
					PrintWriter out = null;
					if (!result.trim().isEmpty()) {
						try {
							out = new PrintWriter(pathToFile + "/" + Constants.RESPONSE + Constants.TXT);
							out.println(now.getTime() + " : \n " + result);
							out.close();
						} catch (Exception e) {
							logger.error(e.getMessage());
						} finally {
							try {
								if (out != null) {
									out.close();
								}
							} catch (Exception e) {
								logger.error(e.getMessage());
							}
						}
					}

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