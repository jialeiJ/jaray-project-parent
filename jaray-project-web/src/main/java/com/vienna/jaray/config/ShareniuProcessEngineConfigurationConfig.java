package com.vienna.jaray.config;

import lombok.extern.slf4j.Slf4j;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.context.annotation.Configuration;

/**
 * *解决流程图汉字乱码问题
 * @author Administrator
 *
 */
@Slf4j
@Configuration
public class ShareniuProcessEngineConfigurationConfig implements ProcessEngineConfigurationConfigurer {

	@Override
	public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
		processEngineConfiguration.setActivityFontName("宋体");
		processEngineConfiguration.setLabelFontName("宋体");
		processEngineConfiguration.setAnnotationFontName("宋体");
		log.info("ShareniuProcessEngineConfigurationConfigurer#############");
		log.info(processEngineConfiguration.getActivityFontName());
	}

}
