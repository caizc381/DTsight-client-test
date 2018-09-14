package com.tijiantest.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfParser {
	private String file;
	private String currentSection;
	private List<String> sections;
	public static final String FILEENCODING = "UTF-8";  

	private Map<String, Map<String, String>> confs;
	private Map<String, List<String>> sectionItems = new HashMap<String, List<String>>();

	public ConfParser(String file) {
		this.file = file;
		this.sections = new ArrayList<String>();
		this.confs = new HashMap<String, Map<String, String>>();

		loadConf();
	}

	public String getValue(String section, String key) {
		if (confs == null || !confs.containsKey(section)) {
			return null;
		}

		Map<String, String> kvs = confs.get(section);
		return kvs.get(key);
	}

	public void setValue(String section, Map<String, String> kvMaps) {
		if (!confs.containsKey(section)) {
			confs.put(section, new HashMap<String, String>());
			sectionItems.put(section, new ArrayList<String>());
			sections.add(section);
		}

		Map<String, String> kvs = confs.get(section);
		for (String key : kvMaps.keySet()) {
			kvs.put(key, kvMaps.get(key));
			if (!sectionItems.get(section).contains(key)) {
				sectionItems.get(section).add(key);
			}
		}

	}

	public boolean save() {
		FileOutputStream fos;
		OutputStreamWriter osw;
		BufferedWriter writer;

		try {
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos, FILEENCODING);
			writer = new BufferedWriter(osw);

			for (String section : sections) {
				writer.write("[" + section + "]");
				writer.newLine();

				for (String key : this.sectionItems.get(section)) {
					writer.write(key + "=" + confs.get(section).get(key));
					writer.newLine();
				}
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	protected void loadConf() {
		FileInputStream fis;
		InputStreamReader isr;	
		BufferedReader reader;

		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, FILEENCODING);
			reader = new BufferedReader(isr);

			String line = reader.readLine();
			while (line != null) {
				if (!parseLine(line)) {
					reader.close();
					isr.close();
					fis.close();
					
					confs = null;
					break;
				}
				line = reader.readLine();
//				System.out.println("line"+line);
			}
			reader.close();
		} catch (Exception e) {
			confs = null;
			e.printStackTrace();
		}
	}

	protected boolean parseLine(String line) {
		line = line.trim();
		if (line.matches("^\\[.*\\]$")) {
			currentSection = line.replaceFirst("\\[(.*)\\]", "$1");
			currentSection = currentSection.trim();
			System.out.println("section: " + currentSection);
			if (confs.containsKey(currentSection)) {
				return false;
			} else {
				sections.add(currentSection);
				confs.put(currentSection, new HashMap<String, String>());
				sectionItems.put(currentSection, new ArrayList<String>());
			}
		} else if (line.equals("") || line.startsWith("#")) {
			;
		} else {
			String[] sArray = line.split("=");
			if (sArray == null)
				return false;
			if (sArray.length < 2)
				return false;
			if (sArray.length == 2){
				String key = sArray[0].trim();
				String value = sArray[1].trim();

				confs.get(currentSection).put(key, value);
				sectionItems.get(currentSection).add(key);
			}
			if(sArray.length > 2){
				String key = sArray[0].trim();
				String value = line.substring(key.length()+1);

				confs.get(currentSection).put(key, value);
				sectionItems.get(currentSection).add(key);
			}
			
		}
		return true;
	}
}
