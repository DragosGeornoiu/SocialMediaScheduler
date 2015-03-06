package ro.project.parser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FileManager {
	private final String PATH = "D:\\\\workspace\\\\SocialMediaScheduler\\\\src\\\\main\\\\resources\\\\quotes";

	public String createFileNameFromUrl(String url) {
		List<String> files = splitUrlToFileNames(url);
		String dir = "";

		for (int i = 0; i < files.size(); i++) {
			dir += files.get(i);
		}

		/*
		 * ClassLoader loader = this.getClass().getClassLoader(); String
		 * projectDir = loader.getResource("").toString(); String resourcesDir =
		 * projectDir.split("target")[0] .replace("/", "\\\\") +
		 * "src\\\\main\\\\resources" + dir; String finalDir =
		 * resourcesDir.split(":")[1] + ":" + resourcesDir.split(":")[2];
		 * finalDir = finalDir.replaceFirst("\\\\\\\\", "");
		 */

		return dir;
	}

	/**
	 * Given a String representation of an URL, the method returns a list with
	 * the URL split for an appropriate folder creation for the placement of the
	 * HTML.
	 * 
	 * @param url
	 *            String representation of a URL.
	 * @return a list with each node representing a folder name. Followed in
	 *         order, the nodes represent the path to check for a previous
	 *         version of the URL or where to create a new one
	 */
	private List<String> splitUrlToFileNames(String url) {
		String[] sp = url.split("(/)|(\\.)");

		List<String> splited = new LinkedList<String>(Arrays.asList(sp));

		for (int i = 0; i < splited.size(); i++) {
			splited.set(i, getValidFileName(splited.get(i)));

			if (splited.get(i).trim().isEmpty()) {
				splited.remove(i);
				i--;
			}
		}

		return splited;
	}

	/**
	 * Removes all characters not allowed in a folder name. Also returns an
	 * empty String if the filename is HTTP or WWW, this is done to be able to
	 * create a correct directory for each web site. The URLS
	 * http://stackoverflow.com/, http://wwww.stackoverflow.com/ and
	 * stackoverflow.com/ represent the same web site, so we should be able to
	 * check correctly if a previous version of that web site was already
	 * stored, even though this time it has or not HTTP or WWW in front of it.
	 * 
	 * @param fileName
	 *            String to remove from not allowed folder creation characters,
	 *            if any.
	 * @return a valid folder name.
	 */
	private String getValidFileName(String fileName) {
		String temp = fileName.replace("^\\.+", "").replaceAll("[\\\\/:*?\"<>|]", "");

		if ((temp.equals("http")) || (temp.equals("www")) || (temp.equals("html"))) {
			return "";
		}

		return temp;
	}

	/**
	 * Creates a HTML file at the given path with the name fileName;
	 * 
	 * @param path
	 *            The location where the HTML file should be created
	 * @param filename
	 *            The name of the HTML file, meaning the version of the file
	 * @return
	 */
	public String createFileInPath(String fileName) {
		File file = new File(PATH + "\\\\" + fileName + ".txt");
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return PATH + "\\\\" + fileName + ".txt";
	}

}
