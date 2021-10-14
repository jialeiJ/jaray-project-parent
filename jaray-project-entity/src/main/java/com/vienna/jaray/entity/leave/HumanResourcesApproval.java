package com.vienna.jaray.entity.leave;

import lombok.Data;

/**
 * 人力资源审核实体
 * 
 * @author Administrator
 *
 */

@Data
public class HumanResourcesApproval {
	
	private String id;
	private String process_instance_id;
	private String task_id;
	private String user_id;
	private String approval_result;
	private String remark;
	private String approval_name;
	private String create_time;
	private String update_time;

}
