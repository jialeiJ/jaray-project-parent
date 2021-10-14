package com.vienna.jaray.mapper.leave;

import com.vienna.jaray.entity.leave.LeaveApply;
import com.vienna.jaray.entity.leave.ManagerApproval;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ManagerApprovalMapper {
	
	int insert(@Param("managerApproval") ManagerApproval managerApproval);
	
	int deletePrimaryKey(@Param("id") String id);
	
	int updatePrimaryKey(@Param("managerApproval") ManagerApproval managerApproval);
	
	LeaveApply selectPrimaryKey(@Param("id") String id);

}
