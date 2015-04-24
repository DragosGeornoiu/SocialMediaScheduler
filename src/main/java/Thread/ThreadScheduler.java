package Thread;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;

import org.apache.log4j.pattern.IntegerPatternConverter;

import ro.project.scheduler.Scheduler;
import ro.project.servlet.ServletToScheduler;

public class ThreadScheduler extends Thread {

	private volatile boolean isStopped = false;

	private final int intervalToCheckToPost = 15;
	private int startHour;
	private int endHour;
	private int startMinutes;
	private int endMinutes;
	private String pathToFile;

	private String path2;
	private String radios;
	private String where;
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
	private ServletToScheduler servletToScheduler;

	public ThreadScheduler() {
	}

	public ThreadScheduler(String pathToFile, Scheduler scheduler) {
		this.pathToFile = pathToFile;
		servletToScheduler = new ServletToScheduler(scheduler);
	}

	@Override
	public synchronized void start() {
		super.start();

		System.out.println("Start method of thread called");
		while (!isStopped()) {
			System.out.println("trying read from file");
			readFromFile();
			System.out.println("finished reading from file");
			Calendar now = Calendar.getInstance();
			int hour = now.get(Calendar.HOUR_OF_DAY);
			int minute = now.get(Calendar.MINUTE);
			int t = 0;

			System.out.println(hour);
			System.out.println(minute);

			System.out.println("startHour: " + startHour);
			System.out.println("endHour: " + endHour);
			System.out.println("startMinutes: " + startMinutes);
			System.out.println("endMinutes " + endMinutes);

			// TREBUIE TESTAT SI CU MINUTELE
			if (hour >= startHour && hour <= endHour) {
				System.out.println("if condition passed. trying to schedule post");
				schedulePosts(hour, minute);
				System.out.println("finished scheduling post");
				int h = Integer.parseInt(hourDropDown2) - Integer.parseInt(hourDropDown);
				int m = Integer.parseInt(minuteDropDown2) - Integer.parseInt(minuteDropDown);
				if (m < 0) {
					t = (h - 1) * 60 + 60 - m + 10; // 10 adaugat de siguranta
				} else {
					t = h * 60 + m + 10;
				}

				try {
					synchronized (this) {
						Thread.sleep(t * 60 * 1000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				try {
					synchronized (this) {
						Thread.sleep(intervalToCheckToPost * 60 * 1000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

	}

	private void readFromFile() {
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(pathToFile + "config.properties");

			prop.load(input);

			path2 = prop.getProperty("path");
			radios = prop.getProperty("radios");
			where = prop.getProperty("where");
			yearDropDown = prop.getProperty("yearDropDown");
			monthDropDown = prop.getProperty("monthDropDown");
			dayDropDown = prop.getProperty("dayDropDown");
			hourDropDown = prop.getProperty("hourDropDown");
			minuteDropDown = prop.getProperty("minuteDropDown");
			gmtDropDown = prop.getProperty("gmtDropDown");
			dayDropDown2 = prop.getProperty("dayDropDown2");
			hourDropDown2 = prop.getProperty("hourDropDown2");
			minuteDropDown2 = prop.getProperty("minuteDropDown2");
			numberofQuotes = prop.getProperty("numberofQuotes");

			startHour = Integer.parseInt(hourDropDown);
			endHour = Integer.parseInt(hourDropDown2);
			startMinutes = Integer.parseInt(minuteDropDown);
			endMinutes = Integer.parseInt(minuteDropDown2);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void schedulePosts(int hour, int minute) {
		System.out.println("THREAD-SCHEDULER: schedulePosts");
		servletToScheduler.postToSocialMedia(path2, radios, where, yearDropDown, monthDropDown, dayDropDown,
				hourDropDown, minuteDropDown, gmtDropDown, dayDropDown2, hourDropDown2, minuteDropDown2,
				numberofQuotes, hour, minute);
	}

	public synchronized void doStop() {
		isStopped = true;
		try {
			this.interrupt(); // break pool thread out of dequeue() call.
		} catch (Exception e) {
			e.printStackTrace();
		}
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

}
