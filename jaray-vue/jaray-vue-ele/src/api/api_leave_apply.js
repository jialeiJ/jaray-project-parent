import * as API from '.'

export default {
  // 查询请假申请信息列表
  findLeaveList: params => {
    return API.POST('/leave/find/all', params)
  },
  // 添加请假申请信息
  addLeaveApply: params => {
    return API.POST('/leave/leaveApply', params)
  },
  // 删除请假申请信息
  deleteLeaveApplyByIds: params => {
    return API.POST('/leave/delete', params)
  },
  // 更新请假申请信息
  updateLeaveApplyById: params => {
    return API.POST('/leave/update', params)
  },
  // 查询请假申请信息
  viewLeaveApplyById: params => {
    return API.POST('/leave/find', params)
  },
  findMyToDo: params => {
    return API.POST('/leave/myToDo', params)
  },
  approveLeave: params => {
    return API.POST('/leave/approve', params)
  },
  findHistory: params => {
    return API.POST('/leave/task/all', params)
  },
  viewFlowDiagram: params => {
    return API.viewImage('/flow/diagram', params)
  }
}
