package com.vienna.jaray.entity.system;

import lombok.Data;

/**
 * @author Jaray
 * @date 2020年09月10日 22:29
 * @description: 系统角色权限实体类
 */
@Data
public class SysRolePerm {
    private Integer id;
    private Integer roleId;
    private Integer permId;
    private String createBy;
    private String createTime;
    private String lastUpdateBy;
    private String lastUpdateTime;
}
