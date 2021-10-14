package com.vienna.jaray.workflow.service;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

public interface WorkflowService {
	/**
	 * 部署流程定义(从classpath)
	 * 
	 * @param name 部署名称
	 * @param classpathResourceBpmn bpmn资源
	 * @param classpathResourcePng png资源
	 */
	public void deploymentProcessDefinition_classpath(String name, String classpathResourceBpmn, String classpathResourcePng);
	
	/**
	 * 部署流程定义(从zip)
	 * 
	 * @param name 部署名称
	 * @param classpathResourceZip zip资源
	 */
	public void deploymentProcessDefinition_zip(String name, String classpathResourceZip);
	
	/**
	 * 查询流程定义
	 */
	public void findProcessDefinition();
	
	/**
	 * 删除流程定义
	 * 
	 * @param deploymentId 部署id
	 */
	public void deleteProcessDefinition(String deploymentId);
	
	/**
	 * 查看流程图
	 * 
	 * @param deploymentId 部署id
	 */
	public void viewProcessPic(String deploymentId);
	
	/**
	 * 启动流程实例
	 * 
	 * @param processDefinitionKey 流程定义key
	 * @return 流程实例id
	 */
	public String startProcessDefinition(String processDefinitionKey);
	
	/**
	 * 启动流程实例
	 * 
	 * @param processDefinitionKey 流程定义key
	 * @return 流程实例id
	 */
	public String startProcessDefinition(String processDefinitionKey, Map<String, Object> variables);
	
	/**
	 * 查询个人任务
	 * 
	 * @param assignee 办理人
	 * @return 任务集合
	 */
	public List<Task> findPersonalTaskByAssignee(String assignee);

	/**
	 * 查询所有任务
	 * @param processInstanceId
	 * @return
	 */
	public List<Task> findAllTaskByProcessInstanceId(String processInstanceId);
	
	/**
	 * 查询个人任务
	 * 
	 * @param processInstanceId 流程实例id
	 * @return 任务
	 */
	public Task findPersonalTaskByProcessInstanceId(String processInstanceId);
	
	/**
	 * 完成个人任务
	 * 
	 * @param taskId 任务id
	 */
	public void completePersonalTask(String taskId);
	
	/**
	 * 查询当前流程状态(判断流程正在执行，还是结束)
	 * 
	 * @param processInstanceId 流程实例id
	 */
	public void isProcessEnd(String processInstanceId);
	
	/**
	 * 查询历史流程实例
	 * 
	 * @param assignee 办理人
	 */
	public void findHistoryTaskByAssignee(String assignee);
	
	/**
	 * 查询历史流程实例
	 * 
	 * @param processInstanceId 流程实例id
	 * @return 
	 */
	public HistoricProcessInstance findHistoryProcessInstance(String processInstanceId);
	
	/**
	 * 查询历史流程实例
	 * 
	 * @param processInstanceId 流程实例id
	 */
	public void findHistoryActiviti(String processInstanceId);
	
	/**
	 * 查询历史任务
	 * 
	 * @param processInstanceId 流程实例id
	 */
	public List<HistoricTaskInstance> findHistoryTaskByProcessInstanceId(String processInstanceId);
	
	/**
	 * 设置流程变量并完成任务
	 * 
	 * @param taskId 任务id
	 * @param variables 流程变量
	 */
	public void setVariablesCompleteTask(String taskId, Map<String, Object> variables);

}
