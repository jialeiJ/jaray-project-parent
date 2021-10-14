package com.vienna.jaray.model.leave;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 请假申请实体
 * 
 * @author Administrator
 *
 */
@Data
public class TaskModel {

	String id;

	String name;

	Date startTime;

	Date endTime;

	String description;

	int priority;

	String owner;

	String assignee;

	String processInstanceId;

	String executionId;

	String processDefinitionId;

	Date createTime;

	String taskDefinitionKey;

	Date dueDate;

	String category;

	String parentTaskId;

	String tenantId;

	String formKey;

	Map<String, Object> taskLocalVariables;

	Map<String, Object> processVariables;

	Date claimTime;
}
