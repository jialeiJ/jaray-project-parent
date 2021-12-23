package com.vienna.jaray.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author Jaray
 * @date 2020年09月10日 22:29
 * @description: 用户日志切面处理类
 */
@Slf4j
@Aspect
@Component
public class ControllerAspect {

	/**
	 * 定义切点@Pointcut
	 * 在注解的位置切入代码
	 */
	@Pointcut("execution(public * com.vienna.jaray.controller..*.*(..))")
	public void controllerPointCut() {
		
	}

	/**
	 * 切面 配置通知
	 * @param joinPoint 切点
	 */
	@Around("controllerPointCut()")
	public Object timeAround(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime;
		long endTime;
		Object obj;
		// 获取开始时间
		startTime = System.currentTimeMillis();
		// 获取返回结果集
		obj = joinPoint.proceed(joinPoint.getArgs());
		// 获取方法执行时间
		endTime= System.currentTimeMillis() - startTime;

		String classAndMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
		log.info("执行 " + classAndMethod + " 耗时为：" + endTime+ "ms");
		return obj;
	}
}
