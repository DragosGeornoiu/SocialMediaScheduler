package ro.project.parser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import ro.project.Constants;

/**
 * 
 * @author Caphyon1
 *
 *         FileManager class is used for creating a file name from a given URl
 *         and for creating that file in path.
 */
public class FileManager {
	final static Logger logger = Logger.getLogger(FileManager.class);
	private String path;

	public FileManager(String path) {
		this.path = path + Constants.QUOTES_FILE;
	}

	/**
	 * Given an URL it creates a file name from it.
	 * 
	 * @param url
	 *            the given URL.
	 * @return the name of the file.
	 */
	public String createFileNameFromUrl(String url) {
		List<String> files = splitUrlToFileNames(url);
		String dir = "";
		int i=0;
		if (url.contains("brainyquote")) {
			while(!files.get(i).equals("quotes")) {
				dir += files.get(i);
				i++;
			}
			
			i=i+1;

			dir += " - ";
			
			if(files.get(i).equals("topics")) {

				dir += files.get(i+1).split("_")[1];
			} else if(files.get(i).equals("authors")) {
				dir += files.get(i+2);
			}
		} else {
			for (i = 0; i < files.size(); i++) {
				dir += files.get(i);
			}

		}
		
		return dir;
	}

	/**
	 * Given a String representation of an URL, the method returns a list with
	 * the URL split for an appropriate folder creation for the placement of the
	 * the new file.
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
	 * check correctly if quotes from that web site were already stored, even
	 * though this time it has or not HTTP or WWW in front of it.
	 * 
	 * @param fileName
	 *            String to remove from not allowed folder creation characters,
	 *            if any.
	 * @return a valid folder name.
	 */
	private String getValidFileName(String fileName) {
		String temp = fileName.replace("^\\.+", "").replaceAll("[\\\\/:*?\"<>|]", "");

		if ((temp.equals(Constants.HTTP)) || (temp.equals(Constants.WWW)) || (temp.equals(Constants.HTML))) {
			return "";
		}

		return temp;
	}

	/**
	 * Creates a text file at the given path with the name fileName;
	 * 
	 * @param filename
	 *            The name of the text file, meaning the version of the file.
	 * @return the location where the text file name fileName will be located.
	 */
	public String createFileInPath(String fileName) {
		File file = new File(path + "\\\\" + fileName);
		if (file.exists() && !file.isDirectory()) {
			return "";
		}
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e) {
			logger.error("Problem creating file in path", e);
		}

		return path + "\\\\" + fileName;
	}

}
