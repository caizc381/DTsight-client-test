package com.dtstack.util.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 *
 * @author linzhihao
 */
public class DBExpUtil {
	
	static String inDir = "settlement";
	static String outDir = "out";
	
	public static void main(String[] args) throws Exception {
		
		File rootDir = new File("exp/"+inDir);
		for (File f : rootDir.listFiles()) {
			if ("pom.xml".equals(f.getName())) {
				continue;
			}
			
			System.out.println("<table><name>"+f.getName().split("\\.")[0]+"</name></table>");
		}
//		
//		File rootdir = new File(inDir);
//		System.out.println(rootdir.getAbsolutePath());
//		if (!rootdir.isDirectory()) {
//			throw new Exception("不是目录");
//		}
//		
//		File[] dirs = rootdir.listFiles();
//		
//		for (File dir : dirs) {
//			if (dir.isDirectory()) {
//				File[] files = dir.listFiles();
//				
//				System.out.println(dir.getName());
//				StringBuilder sb = new StringBuilder();
//				sb.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");
//				sb.append("<dataset>\r\n");
//				for (File file:files) {
//					if (file.getName().indexOf(".xml")>0) {
//						System.out.println(file.getName());
//						readFile(file, sb);
//					}
//					
//				}
//				sb.append("</dataset>\r\n");
//				String outputPath = outDir+"/"+dir.getName()+".xml";
//				
//				File out = new File(outputPath);
//				BufferedWriter writer = new BufferedWriter(new FileWriter(out));
//				writer.write(sb.toString());
//				writer.flush();
//				writer.close();
//			}
//		}
		
	}
	
	static void readFile(File file, StringBuilder sb) throws Exception {
		String tableName = file.getName().split("\\.")[0];
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String temp = null;
		
		while ((temp=reader.readLine()) != null) {
			if (temp.indexOf("<?")==0  ||
					temp.indexOf("<RECORDS>")==0 ||
					temp.indexOf("</RECORDS>")==0) {
				continue;
			}
			
			temp = temp.replace("RECORD", tableName)
					.replace("0000-0-0 00:00:00", "2016-3-9 21:11:12")
					.replaceAll("description=\"([^\"]*)\"", "description=\"数据可能有问题 被我替换了\"")
					.replace("update_time=\"\"", "update_time=\"2016-3-9 21:11:12\"")
					.replace("recoverable_balance_time=\"\" revocable_time=\"\"", "")
					.replace("department=\"\"", "department=\"部门\"")
					.replace("gender=\"\"", "gender=\"1\"")
					.replace("exam_date=\"\"", "exam_date=\"2016-3-9 21:11:12\"")
					.replaceAll("(\\w+)=\"\"", "")
					.replace("\n", "").replace("\"\"", "\"0\"");
			
			sb.append(temp+"\r\n");
		}
		reader.close();
	}
}
