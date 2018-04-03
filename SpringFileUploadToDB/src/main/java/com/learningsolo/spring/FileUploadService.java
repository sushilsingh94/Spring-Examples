package com.learningsolo.spring;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;


public class FileUploadService {
	
	@Autowired
	FileUploadDao fileUploadDao;
	
	public String uploadFileData(String inputFilePath){
		Workbook workbook = null;
			Sheet sheet = null;
			try 
			{
				
				workbook = getWorkBook(new File(inputFilePath));
				sheet = workbook.getSheetAt(0);
				
				/*Build the header portion of the Output File*/
				String headerDetails= "EmployeeId,EmployeeName,Address,Country";
				String headerNames[] = headerDetails.split(",");
				 
				 /*Read and process each Row*/
				 ArrayList<ExcelTemplateVO> employeeList = new ArrayList<>();
				 Iterator<Row> rowIterator = sheet.iterator();
				 
				 while(rowIterator.hasNext()) 
				 {
				        Row row = rowIterator.next();
				        //Read and process each column in row
				        ExcelTemplateVO excelTemplateVO = new ExcelTemplateVO();
				        int count=0;
				        while(count<headerNames.length){
				        	String methodName = "set"+headerNames[count];
				        	String inputCellValue = getCellValueBasedOnCellType(row,count++);
				        	setValueIntoObject(excelTemplateVO, ExcelTemplateVO.class, methodName, "java.lang.String", inputCellValue);
				        }
				        
				        employeeList.add(excelTemplateVO);
				 }
				 fileUploadDao.saveFileDataInDB(employeeList);
				 
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			
		
		return "Success";
	}
	
	public static Workbook getWorkBook(File fileName)
	{
		Workbook workbook = null;
		try {	
			String myFileName=fileName.getName();
			String extension = myFileName.substring(myFileName.lastIndexOf("."));
			if(extension.equalsIgnoreCase(".xls")){
				workbook = new HSSFWorkbook(new FileInputStream(fileName));
			}
			else if(extension.equalsIgnoreCase(".xlsx")){
				workbook = new XSSFWorkbook(new FileInputStream(fileName));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return workbook;
	}
	
	public static String getCellValueBasedOnCellType(Row rowData,int columnPosition)
	{
		String cellValue=null;
		Cell cell = rowData.getCell(columnPosition);
		if(cell!=null){
			if(cell.getCellType()==Cell.CELL_TYPE_STRING)
			{
				String inputCellValue=cell.getStringCellValue();
				if(inputCellValue.endsWith(".0")){
					inputCellValue=inputCellValue.substring(0, inputCellValue.length()-2);
				}
		 		cellValue=inputCellValue;
			}
			else if (cell.getCellType()==Cell.CELL_TYPE_NUMERIC)
			{
		 		Double doubleVal = new Double(cell.getNumericCellValue());
		 		cellValue= Integer.toString(doubleVal.intValue());
			}
		}
		return cellValue;
	}
	
	private static <T> void  setValueIntoObject(Object obj, Class<T> clazz, String methodNameForField, String dataType, String inputCellValue) 
		throws SecurityException, NoSuchMethodException, ClassNotFoundException, NumberFormatException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		
		
		Method meth = clazz.getMethod(methodNameForField, Class.forName(dataType));
		T t = clazz.cast(obj);
		 
		 if("java.lang.Double".equalsIgnoreCase(dataType))
		{
			meth.invoke(t, Double.parseDouble(inputCellValue));
		}else if(!"java.lang.Integer".equalsIgnoreCase(dataType))
		{
			meth.invoke(t, inputCellValue);
		}
		else
		{
			meth.invoke(t, Integer.parseInt(inputCellValue));
		}		 		
	}

}
