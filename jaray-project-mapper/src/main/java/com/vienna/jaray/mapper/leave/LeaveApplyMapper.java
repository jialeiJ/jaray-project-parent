package com.vienna.jaray.mapper.leave;

import com.vienna.jaray.entity.leave.LeaveApply;
import com.vienna.jaray.model.system.CommonParamsModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface LeaveApplyMapper {
	
	int add(@Param("leaveApply") LeaveApply leaveApply);
	
	int deleteByIds(@Param("ids") String[] ids);
	
	int updateById(@Param("entity") LeaveApply leaveApply);
	
	List<LeaveApply> findAll(@Param("common") CommonParamsModel commonParams);
	
	LeaveApply findById(@Param("id") String id);

	List<LeaveApply> findByProcessInstanceIds(@Param("processInstanceIdList") List processInstanceIdList);

}
