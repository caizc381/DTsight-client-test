package com.tijiantest.listener;

import com.tijiantest.annotations.DeepHospital;
import com.tijiantest.base.ConfDefine;
import com.tijiantest.util.ConfParser;
import org.apache.log4j.Logger;
import org.junit.internal.builders.IgnoredClassRunner;
import org.testng.*;

import com.tijiantest.base.BaseTest;

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
		boolean isDeep = method.getTestMethod().getConstructorOrMethod().getMethod().isAnnotationPresent( DeepHospital.class)?true:false;
		String mediatorStart = new ConfParser(ConfDefine.ENV_CONFIG).getValue(ConfDefine.PUBLIC,ConfDefine.MEDIATORAGENTSTART);
		boolean isMediatorStart = (mediatorStart.equals("true")||mediatorStart.equals("yes"))?true:false;
		if(method.isTestMethod() && isDeep && !isMediatorStart){
			System.out.println("hospital not open mediatorAgent ");
			throw  new SkipException("深对接环境不符合，本用例不执行！！");
		}
	}

	@Override
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {

	}
}