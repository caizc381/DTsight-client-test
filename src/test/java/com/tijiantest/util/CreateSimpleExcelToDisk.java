package com.tijiantest.util;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
    
public class CreateSimpleExcelToDisk    
{    
    
	public static void createSimpleExcel(List<ExcelMember> list,String fileName) throws Exception    
    {    
        // 第一步，创建一个webbook，对应一个Excel文件    
        HSSFWorkbook wb = new HSSFWorkbook();    
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet    
        HSSFSheet sheet = wb.createSheet("orderTest");  
//        sheet.setColumnWidth(0, 50*256);
//        sheet.setColumnWidth(1, 10*256);
//        sheet.setColumnWidth(2, 100*256);
//        sheet.setColumnWidth(3, 20*256);
        // 第三步，创建单元格，并设置值表头 设置表头居中    
        HSSFCellStyle style = wb.createCellStyle();    
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式    
        // 第四步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short    
       
    
        int i = 0;
        for ( ;i<list.size();i++){
        	HSSFRow row = sheet.createRow(i);    
        	ExcelMember stu = (ExcelMember)list.get(i);
        	row.createCell(0).setCellValue(stu.getOrderNum());
        	row.createCell(1).setCellValue(stu.getStr());
        	row.createCell(2).setCellValue(stu.getHisItemStrs());
        	row.createCell(3).setCellValue(stu.getAction());
        }
        
        // 第六步，将文件存到指定位置    
        try    
        {  
        	FileOutputStream fout = new FileOutputStream(fileName);    
            wb.write(fout);    
            wb.close();
            fout.close();    
        }    
        catch (Exception e)    
        {    
            e.printStackTrace();    
        }    
    }    
    
    public static void main(String[] args) {
    	List<ExcelMember> list = new ArrayList<ExcelMember>();
    	list.add(new ExcelMember("20170822181918527003446", "X","0101:5.00;010fghhj2:0.00;0202:0.00;0302:0.00;0403:0.00;0509:60.00;0702:10.00;0906:0.00;1006:6.00;1024:8.00;1036:31.00;1202:15.00;1301:18.00;1804:35.00;2403:30.00;2404:16.00", "撤订单"));
		try {
			CreateSimpleExcelToDisk.createSimpleExcel(list, "./csv/order/tesgtt.xlsx");
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
}    