package com.learningsolo.spring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileUploadDao extends BaseDAO{
	
	public boolean saveFileDataInDB(List<ExcelTemplateVO> employeeList){
		String sql = "insert into EMPLOYEE (EMPLOYEEID, EMPLOYEENAME, ADDRESS, COUNTRY) "
				+ " VALUES (:employeeId, :employeeName, :address, :country)";
		try {
			List<Map<String, String>> batchUpdateParams = new ArrayList<>();
			
				for(ExcelTemplateVO vo : employeeList){
					Map<String, String> paramMap = new HashMap<>();
					paramMap.put("employeeId", vo.getEmployeeId());
					paramMap.put("employeeName", vo.getEmployeeName());
					paramMap.put("address", vo.getAddress());
					paramMap.put("country", vo.getCountry());
					batchUpdateParams.add(paramMap);
				}
				
				getNamedParamJdbcTemplate().batchUpdate(sql, batchUpdateParams.toArray(new Map[batchUpdateParams.size()] ));
			
		} catch (Exception e) {
			e.printStackTrace(); 
			return false;
		}
		
		return true;
		
	}

}
