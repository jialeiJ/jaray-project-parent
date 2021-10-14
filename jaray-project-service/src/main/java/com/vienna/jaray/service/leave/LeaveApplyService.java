package com.vienna.jaray.service.leave;

import com.vienna.jaray.common.ResponseResult;
import com.vienna.jaray.entity.leave.LeaveApply;
import com.vienna.jaray.model.system.CommonParamsModel;

/**
 * @author Jaray
 * @date 2021年05月04日 22:42
 * @description: 请假接口
 */
public interface LeaveApplyService {
    /**
     * 查询请假申请列表
     *
     * @param commonParams 表格数据通用参数对象
     * @return 请假申请列表
     */
    ResponseResult findAll(CommonParamsModel commonParams);

    /**
     * 请假申请
     * @param leaveApply
     * @return
     */
    ResponseResult leaveApply(LeaveApply leaveApply);

    /**
     * 根据id查询请假申请信息
     * @param id
     * @return
     */
    ResponseResult findById(String id);

    /**
     * 删除请假申请信息
     * @param ids
     * @return
     */
    ResponseResult deleteByIds(String[] ids);

    /**
     * 更新请假申请信息
     * @param leaveApply
     * @return
     */
    ResponseResult updateById(LeaveApply leaveApply);

    /**
     * 查询待办信息
     * @param userId
     * @return
     */
    ResponseResult findMyToDo(CommonParamsModel commonParams, String userId);

    /**
     * 审批待办信息
     * @return
     */
    ResponseResult approveLeaveApply(String userId, String processInstanceId);

    /**
     * 查询所有任务
     * @param processInstanceId
     * @return
     */
    ResponseResult findAllTaskByProcessInstanceId(String processInstanceId);
}
