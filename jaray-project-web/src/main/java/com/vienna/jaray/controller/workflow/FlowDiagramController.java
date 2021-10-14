package com.vienna.jaray.controller.workflow;

import com.vienna.jaray.workflow.service.WorkflowImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 请假申请控制器
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = "/flow")
public class FlowDiagramController {
	@Autowired
    private WorkflowImageService workflowImageService;

    /**
     * 查看流程图
     * @param request
     * @param response
     * @throws Exception
     */
	@GetMapping("/diagram")
	@ResponseBody
	public void flowDiagram(HttpServletRequest request,HttpServletResponse response, String processInstanceId) throws Exception {
		byte[] processImage = workflowImageService.getFlowImgByProcInstId(processInstanceId);
        
        try (OutputStream out = response.getOutputStream();
			InputStream is = new ByteArrayInputStream(processImage);
			BufferedInputStream bis = new BufferedInputStream(is);) {
        	String fileName = String.valueOf(System.currentTimeMillis());
	        boolean isOnLine = true;
	        response.reset(); // 非常重要
	        if (isOnLine) { // 在线打开方式
	            //response.setContentType("");
	            response.setHeader("Content-Disposition", "inline; filename="+fileName+".png");
	            // 文件名应该编码成UTF-8
	        } else { // 纯下载方式
	            response.setContentType("application/x-msdownload");
	            response.setHeader("Content-Disposition", "attachment; filename="+fileName+".png");
	        }
	        //读取文件流
	        int len = 0;  
	        byte[] buffer = new byte[1024 * 10];  
	        while ((len = bis.read(buffer)) != -1){  
	            out.write(buffer,0,len);  
	        }
            out.flush();
		}catch (Exception e){
            e.printStackTrace();  
        }
	}

}
