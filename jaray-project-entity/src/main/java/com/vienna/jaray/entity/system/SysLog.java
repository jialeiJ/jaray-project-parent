package com.vienna.jaray.entity.system;

import lombok.Data;

/**
 * @author Jaray
 * @date 2020年09月10日 22:29
 * @description: 系统日志实体类
 */
@Data
public class SysLog {
    private Integer id;
    private String userName;
    private String operation;
    private String url;
    private String method;
    private String params;
    private String returnResult;
    private String time;
    private String ip;
    private String createBy;
    private String createTime;
    private String lastUpdateBy;
    private String lastUpdateTime;
}
