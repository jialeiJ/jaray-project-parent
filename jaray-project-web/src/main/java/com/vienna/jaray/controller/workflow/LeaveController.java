package com.vienna.jaray.controller.workflow;

import com.vienna.jaray.common.ResponseResult;
import com.vienna.jaray.entity.leave.LeaveApply;
import com.vienna.jaray.mapper.leave.LeaveApplyMapper;
import com.vienna.jaray.model.system.CommonParamsModel;
import com.vienna.jaray.service.leave.LeaveApplyService;
import com.vienna.jaray.workflow.service.WorkflowImageService;
import com.vienna.jaray.workflow.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 请假申请控制器
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = "/leave")
public class LeaveController {
	private static final String PROCESS_DEFINITION_KEY = "leaveProcess";
	private static final String NAME = "请假流程";
	private static final String CLASSPATH_RESOURCE_BPMN = "processes/LeaveProcess.bpmn";
	private static final String CLASSPATH_RESOURCE_PNG = "processes/LeaveProcess.png";
	private static final String CLASSPATH_RESOURCE_ZIP = "processes/LeaveProcess.zip";
	@Autowired
	private WorkflowService workflowService;
	@Autowired
    private WorkflowImageService workflowImageService;
	@Autowired
	private LeaveApplyMapper leaveApplyMapper;
	@Autowired
	private LeaveApplyService leaveApplyService;
	
	
	/**
	 * 部署请假流程定义
	 * @return
	 */
	@PostMapping("/deploymentProcessDefinition")
	@ResponseBody
	public void deploymentProcessDefinition() {
		workflowService
		.deploymentProcessDefinition_classpath(NAME, CLASSPATH_RESOURCE_BPMN, CLASSPATH_RESOURCE_PNG);
	}
	
	/**
	 * 请假申请
	 */
	@PostMapping("/leaveApply")
	@ResponseBody
	public ResponseResult leaveApply(LeaveApply leaveApply) {
		return leaveApplyService.leaveApply(leaveApply);
	}
	
	/**
	 * 请假信息列表
	 * @return 
	 */
	@PostMapping("/find/all")
	@ResponseBody
	public ResponseResult findAll(CommonParamsModel commonParams) {
		return leaveApplyService.findAll(commonParams);
	}

	/**
	 * 根据id查询请假申请信息
	 * @return
	 */
	@PostMapping("/find")
	@ResponseBody
	public ResponseResult findAll(String id) {
		return leaveApplyService.findById(id);
	}

	/**
	 * 删除请假申请信息
	 * @return
	 */
	@PostMapping("/delete")
	@ResponseBody
	public ResponseResult findAll(String[] ids) {
		return leaveApplyService.deleteByIds(ids);
	}

	/**
	 * 查询我的待办信息
	 * @return
	 */
	@PostMapping("/update")
	@ResponseBody
	public ResponseResult updateById(LeaveApply leaveApply) {
		return leaveApplyService.updateById(leaveApply);
	}

	/**
	 * 查询我的待办信息
	 * @return
	 */
	@PostMapping("/myToDo")
	@ResponseBody
	public ResponseResult myToDo(CommonParamsModel commonParams, String userId) {
		return leaveApplyService.findMyToDo(commonParams, userId);
	}

	/**
	 * 审批待办信息
	 * @return
	 */
	@PostMapping("/approve")
	@ResponseBody
	public ResponseResult approveLeaveApply(String userId, String processInstanceId) {
		return leaveApplyService.approveLeaveApply(userId, processInstanceId);
	}

	@PostMapping("/task/all")
	@ResponseBody
	public ResponseResult findAllTaskByProcessInstanceId(String processInstanceId) {
		return leaveApplyService.findAllTaskByProcessInstanceId(processInstanceId);
	}
}
