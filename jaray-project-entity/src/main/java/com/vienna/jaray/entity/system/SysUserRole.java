package com.vienna.jaray.entity.system;

import lombok.Data;

/**
 * @author Jaray
 * @date 2020年09月10日 22:29
 * @description: 系统用户角色实体类
 */
@Data
public class SysUserRole {
    private Integer id;
    private Integer userId;
    private Integer roleId;
    private String createBy;
    private String createTime;
    private String lastUpdateBy;
    private String lastUpdateTime;
}
