package com.vienna.jaray.workflow.service;

public interface WorkflowImageService {
	
	byte[] getFlowImgByProcInstId(String processInstanceId) throws Exception;

}
