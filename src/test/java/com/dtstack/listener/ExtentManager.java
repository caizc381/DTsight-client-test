package com.dtstack.listener;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ResourceCDN;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.KlovReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.dtstack.base.BaseTest;

public class ExtentManager extends BaseTest{
	
	private static ExtentReports extent;


    public static ExtentReports getInstance(String filePath) {
        if (extent == null)
            createInstance(filePath);
        return extent;
    }


    public static void createInstance(String filePath) {
        extent = new ExtentReports();
//        extent.setSystemInfo("os", "Linux");
        int indexStart = uicurl.indexOf("//");
    	int indexEnd = uicurl.indexOf(".");
        extent.setSystemInfo("env",uicurl.substring(indexStart+2, indexEnd));
        //extent.attachReporter(createHtmlReporter(filePath), createKlovReporter());
    }

    public static ExtentHtmlReporter createHtmlReporter(String filePath){
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(filePath);
        //报表位置
        htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
        //使报表上的图表可见
        htmlReporter.config().setDocumentTitle("ExtentReports");
        htmlReporter.config().setChartVisibilityOnOpen(true);
        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setDocumentTitle(filePath);
        htmlReporter.config().setEncoding("utf-8");
        htmlReporter.config().setReportName("tradeClient自动化测试报告");
//      如果cdn.rawgit.com访问不了，可以设置为：ResourceCDN.EXTENTREPORTS或者ResourceCDN.GITHUB
        htmlReporter.config().setResourceCDN(ResourceCDN.EXTENTREPORTS);
//		htmlReporter.views().dashboard().setSection("Sample", SectionSize.S4, header, list); pro-only
        return htmlReporter;
    }
    

}
