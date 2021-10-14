package com.vienna.jaray.entity.leave;

import lombok.Data;

/**
 * 请假申请实体
 * 
 * @author Administrator
 *
 */
@Data
public class LeaveApply {
	
	private String id;
	private String processInstanceId;
	private String userId;
	private String leaveType;
	private String reason;
	private String startTime;
	private String endTime;
	private String days;
	private String userName;
	private String processStatus;
	private String submitTime;
	private String createTime;
	private String updateTime;

}
