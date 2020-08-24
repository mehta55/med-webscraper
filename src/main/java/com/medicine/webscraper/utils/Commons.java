package com.medicine.webscraper.utils;

import static com.medicine.webscraper.utils.Constants.HTTPS;
import static com.medicine.webscraper.utils.Constants.SLASH;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Commons {
	
	public static String getDomainFromURL(String url) {
		return url.substring(HTTPS.length()).split(SLASH)[0];
	}
	
	public static LocalDateTime getCurrentDateTime() {
		return LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
	}
	
	public static String getFileAsSource(String path) throws IOException {
		StringBuilder strBuilder = new StringBuilder();
		path = path.substring(1).replace("/", "\\");
		Files.readAllLines(Paths.get(path)).forEach(line -> strBuilder.append(line));
		return strBuilder.toString();
	}
	
	public static String encoded(String str) throws UnsupportedEncodingException {
		return URLEncoder.encode(str, "UTF-8");
	}

}
