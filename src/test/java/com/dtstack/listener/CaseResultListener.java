package com.dtstack.listener;

import com.dtstack.annotations.DeepHospital;
import com.dtstack.base.ConfDefine;
import com.dtstack.util.ConfParser;
import org.apache.log4j.Logger;
import org.testng.*;

import com.dtstack.base.BaseTest;

public class CaseResultListener extends TestListenerAdapter implements IInvokedMethodListener,ITestListener{
	protected final static Logger log = Logger.getLogger(BaseTest.class);
	private String caseIdentifier = "";
	
	 @Override
	    public void onTestFailure(ITestResult tr) {
		 caseIdentifier = tr.getTestClass().getName()+"."+tr.getName();
			String[] strArray = caseIdentifier.split("\\.");
			if (strArray.length > 1)
				log.info(String.format("%-10s", "failed:")
						+ strArray[strArray.length - 2] + "."
						+ strArray[strArray.length - 1]);
			else
				Assert.assertTrue(false,"caseIdentifier failed! " + caseIdentifier);
	    }
	     
	    @Override
	    public void onTestSkipped(ITestResult tr) {
	    	caseIdentifier = tr.getTestClass().getName()+"."+tr.getName();
			String[] strArray = caseIdentifier.split("\\.");
			if (strArray.length > 1)
				log.info(String.format("%-10s", "skipped:")
						+ strArray[strArray.length - 2] + "."
						+ strArray[strArray.length - 1]);
			else
				Assert.assertTrue(false,"caseIdentifier failed! " + caseIdentifier);
	    }
	     
	    @Override
	    public void onTestSuccess(ITestResult tr) {
	    	caseIdentifier = tr.getTestClass().getName()+"."+tr.getName();
			String[] strArray = caseIdentifier.split("\\.");
			if (strArray.length > 1)
				log.info(String.format("%-10s", "passed:")
						+ strArray[strArray.length - 2]+ "."
						+ strArray[strArray.length - 1]);
			else
				Assert.assertTrue(false,"caseIdentifier failed! " + caseIdentifier);
	    }
	     
	    @Override
	    public void onTestStart(ITestResult tr){
			caseIdentifier = tr.getTestClass().getName()+"."+tr.getName();
			String[] strArray = caseIdentifier.split("\\.");
			if (strArray.length > 1)
				log.info(String.format("%-10s", "starting:")
						+ strArray[strArray.length - 2] + "."
						+ strArray[strArray.length - 1]);
			else
				Assert.assertTrue(false,"caseIdentifier failed! " + caseIdentifier);	
	    }


	@Override
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
		System.out.println("beforeInvocation");

	}

	@Override
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {

	}
}