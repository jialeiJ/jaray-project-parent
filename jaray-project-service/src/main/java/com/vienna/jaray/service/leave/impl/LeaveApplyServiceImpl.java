package com.vienna.jaray.service.leave.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.vienna.jaray.common.ResponseResult;
import com.vienna.jaray.common.WorkflowStatus;
import com.vienna.jaray.entity.leave.LeaveApply;
import com.vienna.jaray.mapper.leave.LeaveApplyMapper;
import com.vienna.jaray.model.leave.TaskModel;
import com.vienna.jaray.model.system.CommonParamsModel;
import com.vienna.jaray.service.leave.LeaveApplyService;
import com.vienna.jaray.utils.DateUtil;
import com.vienna.jaray.workflow.service.WorkflowService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jaray
 * @date 2021年05月04日 22:42
 * @description: 请假接口实现类
 */
@Service
public class LeaveApplyServiceImpl implements LeaveApplyService {
    private static final String PROCESS_DEFINITION_KEY = "leaveProcess";
    private static final String NAME = "请假流程";
    private static final String CLASSPATH_RESOURCE_BPMN = "processes/LeaveProcess.bpmn";
    private static final String CLASSPATH_RESOURCE_PNG = "processes/LeaveProcess.png";
    private static final String CLASSPATH_RESOURCE_ZIP = "processes/LeaveProcess.zip";
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private LeaveApplyMapper leaveApplyMapper;
    /**
     * 查询请假申请列表
     *
     * @param commonParams 表格数据通用参数对象
     * @return 请假申请列表
     */
    @Override
    public ResponseResult findAll(CommonParamsModel commonParams) {
        //设置分页信息(第几页，每页数量)
        PageHelper.startPage(commonParams.getPageNum(), commonParams.getPageSize());
        List<LeaveApply> leaveApplyList = leaveApplyMapper.findAll(commonParams);

        //取记录总条数
        PageInfo<?> pageInfo = new PageInfo<>(leaveApplyList);
        return ResponseResult.success().add("leaveApplyList", pageInfo);
    }

    /**
     * 请假申请
     * @param leaveApply
     * @return
     */
    @Override
    public ResponseResult leaveApply(LeaveApply leaveApply) {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("applicant", leaveApply.getUserId());
        // 启动流程实例
        String processInstanceId = workflowService.startProcessDefinition(PROCESS_DEFINITION_KEY, variables);

        // 查询个人任务
        Task task = workflowService.findPersonalTaskByProcessInstanceId(processInstanceId);

        variables = new HashMap<String, Object>();
        variables.put("leaveDays", leaveApply.getDays());
        if(Integer.valueOf(leaveApply.getDays()) > 3) {
            variables.put("generalManager", leaveApply.getUserId());
        } else {
            variables.put("manager", leaveApply.getUserId());
        }
        // 完成任务
        workflowService.setVariablesCompleteTask(task.getId(), variables);

        // 入库
        leaveApply.setProcessInstanceId(processInstanceId);
        leaveApply.setProcessStatus(WorkflowStatus.WORKFLOW_NEW.getStatusCode());
        leaveApply.setSubmitTime(DateUtil.format(new Date(), DateUtil.DATE_TIME_PATTERN));
        leaveApplyMapper.add(leaveApply);
        return ResponseResult.success();
    }

    /**
     * 根据id查询请假申请信息
     * @param id
     * @return
     */
    @Override
    public ResponseResult findById(String id) {
        LeaveApply leaveApply = leaveApplyMapper.findById(id);
        return ResponseResult.success().add("leaveApply", leaveApply);
    }

    @Override
    public ResponseResult deleteByIds(String[] ids) {
        leaveApplyMapper.deleteByIds(ids);
        return ResponseResult.success();
    }

    @Override
    public ResponseResult updateById(LeaveApply leaveApply) {
        leaveApplyMapper.updateById(leaveApply);
        return ResponseResult.success();
    }

    /**
     * 查询待办信息
     * @param userId
     * @return
     */
    @Override
    public ResponseResult findMyToDo(CommonParamsModel commonParams, String userId) {
        List<Task> taskList = workflowService.findPersonalTaskByAssignee(userId);
        List<String> processInstanceIdList =  taskList.stream().map(Task::getProcessInstanceId).collect(Collectors.toList());
        //设置分页信息(第几页，每页数量)
        PageHelper.startPage(commonParams.getPageNum(), commonParams.getPageSize());

        List<LeaveApply> leaveApplyList = leaveApplyMapper.findByProcessInstanceIds(processInstanceIdList);
        //取记录总条数
        PageInfo<?> pageInfo = new PageInfo<>(leaveApplyList);
        return ResponseResult.success().add("leaveApplyList", pageInfo);
    }

    /**
     * 审批待办信息
     * @return
     */
    @Override
    public ResponseResult approveLeaveApply(String userId, String processInstanceId) {
        Task task = workflowService.findPersonalTaskByProcessInstanceId(processInstanceId);

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("humanResources", userId);

        String taskId = task.getId();
        workflowService.setVariablesCompleteTask(taskId, variables);

        return ResponseResult.success();
    }

    /**
     * 查询所有任务
     * @param processInstanceId
     * @return
     */
    @Override
    public ResponseResult findAllTaskByProcessInstanceId(String processInstanceId) {
        List<HistoricTaskInstance> historicTaskInstanceList = workflowService.findHistoryTaskByProcessInstanceId(processInstanceId);
        List<TaskModel> taskModelList = new ArrayList<TaskModel>();
        TaskModel taskModel = null;
        for (HistoricTaskInstance historicTaskInstance :historicTaskInstanceList) {
            taskModel = new TaskModel();
            taskModel.setId(historicTaskInstance.getId());
            taskModel.setName(historicTaskInstance.getName());
            taskModel.setAssignee(historicTaskInstance.getAssignee());
            taskModel.setProcessInstanceId(historicTaskInstance.getProcessInstanceId());
            taskModel.setStartTime(historicTaskInstance.getStartTime());
            taskModel.setEndTime(historicTaskInstance.getEndTime());

            taskModelList.add(taskModel);
        }

        return ResponseResult.success().add("taskList", taskModelList);
    }

}
