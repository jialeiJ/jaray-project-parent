package com.vienna.jaray.controller.workflow;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 跳转页面控制器
 * 
 * @author Administrator
 *
 */
@Controller
public class JumpPageController {
	/**
	 * 跳转请假申请页面
	 * @return
	 */
	@GetMapping("/jumpLeaveApply")
	public String jumpLeaveApply() {
		return "leaveApply";
	}
	
	/**
	 * 跳转请假列表页面
	 * @return
	 */
	@GetMapping("/jumpLeaveApplyList")
	public String jumpLeaveApplyList() {
		return "leaveApplyList";
	}

}
