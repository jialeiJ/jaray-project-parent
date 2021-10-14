package com.vienna.jaray.workflow.service.impl;

import com.vienna.jaray.workflow.service.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
public class WorkflowServiceImpl implements WorkflowService {
	/**
	 * 流程运行时对流程实例进行管理与控制
	 */
	@Autowired
	private RuntimeService runtimeService;
	/**
	 * 流程任务进行管理，例如任务提醒、任务完成和创建任务等
	 */
	@Autowired
	private TaskService taskService;
	/**
	 * 流程角色数据进行管理的API，这些角色数据包括用户组、用户及它们之间的关系
	 */
	@Autowired
	private IdentityService identityService;
	
	/**
	 * 生成高亮流程图
	 */
	@Autowired
	private ProcessDiagramGenerator processDiagramGenerator;
	/**
	 * 提供一系列管理流程部署和流程定义的API
	 */
	@Autowired
	private RepositoryService repositoryService;
	/**
	 * 流程的历史数据进行操作，包括查询、删除这些历史数据
	 */
	@Autowired
	private HistoryService historyService;
	@Autowired
	private ProcessEngine processEngine;

	@Override
	public void deploymentProcessDefinition_classpath(String name, String classpathResourceBpmn,
			String classpathResourcePng) {
		Deployment deployment = repositoryService
				// 创建一个部署对象
                .createDeployment()
                // 添加部署的名称
                .name(name)
                // 从classpath资源中加载,一次只能加载一个文件
                .addClasspathResource(classpathResourceBpmn)
                // 从classpath资源中加载,一次只能加载一个文件
                .addClasspathResource(classpathResourcePng)
                // 完成部署
                .deploy();
		log.info("部署id: [{}]",deployment.getId());
		log.info("部署名称: [{}]",deployment.getName());
	}

	@Override
	public void deploymentProcessDefinition_zip(String name, String classpathResourceZip) {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(classpathResourceZip);
		ZipInputStream zipInputStream = new ZipInputStream(is);
		Deployment deployment = repositoryService
						 // 创建一个部署对象
		                 .createDeployment()
		                 // 添加部署的名称
		                 .name("流程定义")
		                 // 指定zip格式文件完成部署
		                 .addZipInputStream(zipInputStream)
		                 // 完成部署
		                 .deploy();
		log.info("部署id: [{}]",deployment.getId());
		log.info("部署名称: [{}]",deployment.getName());
	}

	@Override
	public void findProcessDefinition() {
		List<ProcessDefinition> list = repositoryService
				// 创建流程定义的查询
                .createProcessDefinitionQuery()
                /**指定查询条件,where条件*/
                // 使用部署对象ID查询
                //.deploymentId(deploymentId)
                // 使用流程定义ID查询
                //.processDefinitionId(processDefinitionId)
                // 使用流程定义的key查询
                //.processDefinitionKey(processDefinitionKey)
                // 使用流程定义的名称模糊查询
                //.processDefinitionNameLike(processDefinitionNameLike)
                
                /**排序*/
                // 按照版本的升序排列
                .orderByProcessDefinitionVersion().asc()
                // 按照流程定义的名称降序排列
                //.orderByProcessDefinitionName().desc()
                
                /**返回的结果集*/
                // 返回一个集合列表
                .list();
		        // 返回唯一结果集
                //.singleResult()
		        // 结果集数量
                //.count()
		        // 分页查询
                //.listPage(firstResult, maxResults)
		if(list!=null && list.size()>0){
			for (ProcessDefinition processDefinition : list) {
				// 流程定义的key+版本+随机生成数
				log.info("流程定义id: [{}]", processDefinition.getId());
				// 对应helloworld.bpmn文件中的name属性值
				log.info("流程定义名称: [{}]", processDefinition.getName());
				// 对应helloworld.bpmn文件中的id属性值
				log.info("流程定义key: [{}]", processDefinition.getKey());
				// 当流程定义的key值相同的情况下,版本升级,默认1
				log.info("流程定义的版本: [{}]", processDefinition.getVersion());
				log.info("资源名称bpmn文件: [{}]", processDefinition.getResourceName());
				log.info("资源名称png文件: [{}]", processDefinition.getDiagramResourceName());
				log.info("部署对象id: [{}]", processDefinition.getDeploymentId());
				log.info("------------------------------------");
			}
		}
	}

	@Override
	public void deleteProcessDefinition(String deploymentId) {
		/**
		 * 不带级联的删除
		 *    只能删除没有启动的流程(进行了部署但是没有启动,如果流程启动就会报异常)
		 */
//		repositoryService
//				.deleteDeployment(deploymentId);
		
		/**
		 * 级联删除
		 *    不管流程是否启动,都能删除
		 */
		repositoryService
				.deleteDeployment(deploymentId, true);//默认为false
		log.info("删除成功_流程部署id: [{}]", deploymentId);
	}

	@Override
	public void viewProcessPic(String deploymentId) {
		/**将生成的图片放在文件夹下*/
		//获取图片资源名称
		List<String> list = repositoryService
				        .getDeploymentResourceNames(deploymentId);
		//定义图片资源的名称
		String resourceName = "";
		if(list!=null && list.size()>0){
			for (String name : list) {
				if(name.indexOf(".png")>=0){
					resourceName = name;
				}
			}
		}
		
		//获取图片的输入流
		InputStream is = repositoryService
				     .getResourceAsStream(deploymentId, resourceName);
		
		//将图片生成到D盘的目录下
		File file = new File("D:/"+resourceName);
		//将输入流的图片写到D盘下
		try {
			FileUtils.copyInputStreamToFile(is, file);
		} catch (IOException e) {
			log.error("流程图生成异常", e);
		}
	}

	@Override
	public String startProcessDefinition(String processDefinitionKey) {
		ProcessInstance processInstance = runtimeService
				// 使用流程定义的key启动流程实例，key对应bpmn文件中的id的属性值
		        .startProcessInstanceByKey(processDefinitionKey);
		log.info("流程实例id: [{}]", processInstance.getId());
		log.info("流程定义id: [{}]", processInstance.getProcessDefinitionId());
		return processInstance.getId();
	}
	
	@Override
	public String startProcessDefinition(String processDefinitionKey, Map<String, Object> variables) {
		ProcessInstance processInstance = runtimeService
				// 使用流程定义的key启动流程实例，key对应bpmn文件中的id的属性值
		        .startProcessInstanceByKey(processDefinitionKey, variables);
		log.info("流程实例id: [{}]", processInstance.getId());
		log.info("流程定义id: [{}]", processInstance.getProcessDefinitionId());
		return processInstance.getId();
	}

	@Override
	public List<Task> findPersonalTaskByAssignee(String assignee) {
		List<Task> list = taskService
			// 创建任务查询对象
			.createTaskQuery()
		    /**查询条件(where部分)*/
			// 指定个人任务查询，指定办理人
		    .taskAssignee(assignee)
		    // 组任务的办理人查询
//		    .taskCandidateUser(candidateUser)
//		    // 使用流程定义ID查询
//		    .processDefinitionId(processDefinitionId)
//		    // 使用流程实例ID查询
//		    .processInstanceId(processInstanceId)
//		    // 使用执行对象ID查询
//		    .executionId(executionId)
		    /**排序*/
		    // 使用创建时间升序排列
		    .orderByTaskCreateTime().asc()
		    /**返回结果集*/
//		    // 返回唯一结果集
//		    .singleResult()
//		    // 返回结果集的数量
//		    .count()
//		    // 分页查询
//		    .listPage(firstResult, maxResults);
		    // 返回列表
		    .list();
		if(list!=null && list.size()>0){
			for (Task task : list) {
				log.info("任务id: [{}]", task.getId());
				log.info("任务名称: [{}]", task.getName());
				log.info("任务的创建时间: [{}]", task.getCreateTime());
				log.info("任务的办理人: [{}]", task.getAssignee());
				log.info("流程实例id: [{}]", task.getProcessInstanceId());
				log.info("执行对象id: [{}]", task.getExecutionId());
				log.info("流程定义id: [{}]", task.getProcessDefinitionId());
				log.info("----------------------------------");
			}
		}
		return list;
		
	}

	@Override
	public List<Task> findAllTaskByProcessInstanceId(String processInstanceId) {
		List<Task> taskList = taskService
				// 创建任务查询对象
				.createTaskQuery()
				/**查询条件(where部分)*/
//		    .taskAssignee(assignee)//指定个人任务查询，指定办理人
//		    .taskCandidateUser(candidateUser)//组任务的办理人查询
//		    .processDefinitionId(processDefinitionId)//使用流程定义ID查询
				.processInstanceId(processInstanceId)//使用流程实例ID查询
//		    .executionId(executionId)//使用执行对象ID查询
				/**排序*/
				.orderByTaskCreateTime().desc()//使用创建时间升序排列
				/**返回结果集*/
		    	.list();//返回列表
		for (Task task :taskList) {
			log.info("任务id: [{}]", task.getId());
			log.info("任务名称: [{}]", task.getName());
			log.info("任务的创建时间: [{}]", task.getCreateTime());
			log.info("任务的办理人: [{}]", task.getAssignee());
			log.info("流程实例id: [{}]", task.getProcessInstanceId());
			log.info("执行对象id: [{}]", task.getExecutionId());
			log.info("流程定义id: [{}]", task.getProcessDefinitionId());
			log.info("----------------------------------");
		}

		return taskList;
	}
	
	@Override
	public Task findPersonalTaskByProcessInstanceId(String processInstanceId) {
		Task task = taskService
			// 创建任务查询对象
			.createTaskQuery()
		    /**查询条件(where部分)*/
//		    .taskAssignee(assignee)//指定个人任务查询，指定办理人
//		    .taskCandidateUser(candidateUser)//组任务的办理人查询
//		    .processDefinitionId(processDefinitionId)//使用流程定义ID查询
		    .processInstanceId(processInstanceId)//使用流程实例ID查询
//		    .executionId(executionId)//使用执行对象ID查询
		    /**排序*/
		    .orderByTaskCreateTime().asc()//使用创建时间升序排列
		    /**返回结果集*/
		    .singleResult();//返回唯一结果集
//		    .count();//返回结果集的数量
//		    .listPage(firstResult, maxResults);//分页查询
//		    .list();//返回列表
		log.info("任务id: [{}]", task.getId());
		log.info("任务名称: [{}]", task.getName());
		log.info("任务的创建时间: [{}]", task.getCreateTime());
		log.info("任务的办理人: [{}]", task.getAssignee());
		log.info("流程实例id: [{}]", task.getProcessInstanceId());
		log.info("执行对象id: [{}]", task.getExecutionId());
		log.info("流程定义id: [{}]", task.getProcessDefinitionId());
		log.info("----------------------------------");
		return task;
	}

	@Override
	public void completePersonalTask(String taskId) {
		taskService
			.complete(taskId);
		log.info("完成任务id: [{}]", taskId);
	}

	@Override
	public void isProcessEnd(String processInstanceId) {
		ProcessInstance processInstance = runtimeService
				// 创建流程实例查询
				.createProcessInstanceQuery()
				// 使用流程实例id查询
				.processInstanceId(processInstanceId)
				.singleResult();
		
		if(processInstance == null) {
			log.info("流程已经结束");
		}else {
			log.info("流程没有结束");
		}
		
	}

	@Override
	public void findHistoryTaskByAssignee(String assignee) {
		List<HistoricTaskInstance> historicTaskInstances = historyService
				// 创建历史任务实例查询
				.createHistoricTaskInstanceQuery()
				// 指定历史任务的办理人
				.taskAssignee(assignee)
				.list();
		if(historicTaskInstances !=null && historicTaskInstances.size() > 0) {
			for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
				log.info(historicTaskInstance.getId() +"   "+historicTaskInstance.getName() +"   "+
						historicTaskInstance.getProcessInstanceId() +"   "+ historicTaskInstance.getStartTime() +"   "+
						historicTaskInstance.getEndTime() +"   "+ historicTaskInstance.getDurationInMillis());
			}
		}
	}

	@Override
	public HistoricProcessInstance findHistoryProcessInstance(String processInstanceId) {
		HistoricProcessInstance historicProcessInstance = historyService
				// 创建历史流程实例查询
				.createHistoricProcessInstanceQuery()
				// 使用流程实例ID查询
				.processInstanceId(processInstanceId)
				.singleResult();
		log.info(historicProcessInstance.getId() +"   "+ historicProcessInstance.getProcessDefinitionId() +"   "+
						historicProcessInstance.getStartTime() +"   "+ historicProcessInstance.getEndTime() +"   "+ historicProcessInstance.getDurationInMillis());
		return historicProcessInstance;
	}

	@Override
	public void findHistoryActiviti(String processInstanceId) {
		List<HistoricActivityInstance> historicActivityInstances = historyService
				// 创建历史活动实例查询
				.createHistoricActivityInstanceQuery()
				// 使用流程实例id查询
				.processInstanceId(processInstanceId)
				// 开始时间升序
				.orderByHistoricActivityInstanceStartTime().asc()
				.list();
		if(historicActivityInstances != null && historicActivityInstances.size() > 0) {
			for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
				log.info(historicActivityInstance.getId() +"   "+ historicActivityInstance.getProcessDefinitionId() +"   "+ historicActivityInstance.getActivityType() +"   "+
						historicActivityInstance.getStartTime() +"   "+ historicActivityInstance.getEndTime() +"   "+ historicActivityInstance.getDurationInMillis());
			}
		}	
	}

	@Override
	public List<HistoricTaskInstance> findHistoryTaskByProcessInstanceId(String processInstanceId) {
		List<HistoricTaskInstance> historicTaskInstances = historyService
				// 创建历史任务实例查询
				.createHistoricTaskInstanceQuery()
				// 使用流程实例ID查询
				.processInstanceId(processInstanceId)
				// 指定历史任务的办理人
				//.taskAssignee(taskAssignee)
				// 开始时间升序
				.orderByHistoricTaskInstanceStartTime().asc()
				.list();
		if(historicTaskInstances !=null && historicTaskInstances.size() > 0) {
			for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
				log.info(historicTaskInstance.getId() +" "+historicTaskInstance.getName() +" "+historicTaskInstance.getAssignee() +" "+
						historicTaskInstance.getProcessInstanceId() +" "+ historicTaskInstance.getStartTime() +" "+
						historicTaskInstance.getEndTime() +" "+ historicTaskInstance.getDurationInMillis());
			}
		}
		return historicTaskInstances;
	}

	@Override
	public void setVariablesCompleteTask(String taskId, Map<String, Object> variables) {
		taskService
			.complete(taskId, variables);
	}
	
	/**
     * 获取已经流转的线
     *
     * @param bpmnModel
     * @param historicActivityInstances
     * @return
     */
    private static List<String> getHighLightedFlows(BpmnModel bpmnModel, List<HistoricActivityInstance> historicActivityInstances) {
        // 高亮流程已发生流转的线id集合
        List<String> highLightedFlowIds = new ArrayList<>();
        // 全部活动节点
        List<FlowNode> historicActivityNodes = new ArrayList<>();
        // 已完成的历史活动节点
        List<HistoricActivityInstance> finishedActivityInstances = new ArrayList<>();

        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId(), true);
            historicActivityNodes.add(flowNode);
            if (historicActivityInstance.getEndTime() != null) {
                finishedActivityInstances.add(historicActivityInstance);
            }
        }

        FlowNode currentFlowNode = null;
        FlowNode targetFlowNode = null;
        // 遍历已完成的活动实例，从每个实例的outgoingFlows中找到已执行的
        for (HistoricActivityInstance currentActivityInstance : finishedActivityInstances) {
            // 获得当前活动对应的节点信息及outgoingFlows信息
            currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currentActivityInstance.getActivityId(), true);
            List<SequenceFlow> sequenceFlows = currentFlowNode.getOutgoingFlows();

            /**
             * 遍历outgoingFlows并找到已已流转的 满足如下条件认为已已流转： 1.当前节点是并行网关或兼容网关，则通过outgoingFlows能够在历史活动中找到的全部节点均为已流转 2.当前节点是以上两种类型之外的，通过outgoingFlows查找到的时间最早的流转节点视为有效流转
             */
            if ("parallelGateway".equals(currentActivityInstance.getActivityType()) || "inclusiveGateway".equals(currentActivityInstance.getActivityType())) {
                // 遍历历史活动节点，找到匹配流程目标节点的
                for (SequenceFlow sequenceFlow : sequenceFlows) {
                    targetFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(sequenceFlow.getTargetRef(), true);
                    if (historicActivityNodes.contains(targetFlowNode)) {
                        highLightedFlowIds.add(targetFlowNode.getId());
                    }
                }
            } else {
                List<Map<String, Object>> tempMapList = new ArrayList<>();
                for (SequenceFlow sequenceFlow : sequenceFlows) {
                    for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
                        if (historicActivityInstance.getActivityId().equals(sequenceFlow.getTargetRef())) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("highLightedFlowId", sequenceFlow.getId());
                            map.put("highLightedFlowStartTime", historicActivityInstance.getStartTime().getTime());
                            tempMapList.add(map);
                        }
                    }
                }

                if (!CollectionUtils.isEmpty(tempMapList)) {
                    // 遍历匹配的集合，取得开始时间最早的一个
                    long earliestStamp = 0L;
                    String highLightedFlowId = null;
                    for (Map<String, Object> map : tempMapList) {
                        long highLightedFlowStartTime = Long.valueOf(map.get("highLightedFlowStartTime").toString());
                        if (earliestStamp == 0 || earliestStamp >= highLightedFlowStartTime) {
                            highLightedFlowId = map.get("highLightedFlowId").toString();
                            earliestStamp = highLightedFlowStartTime;
                        }
                    }
                    highLightedFlowIds.add(highLightedFlowId);
                }
            }
        }
        return highLightedFlowIds;
    }

}
