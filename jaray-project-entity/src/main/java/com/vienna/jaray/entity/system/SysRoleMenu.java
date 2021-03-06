package com.vienna.jaray.entity.system;

import lombok.Data;

/**
 * @author Jaray
 * @date 2020年09月10日 22:29
 * @description: 系统角色菜单实体类
 */
@Data
public class SysRoleMenu {
    private Integer id;
    private Integer roleId;
    private Integer menuId;
    private String createBy;
    private String createTime;
    private String lastUpdateBy;
    private String lastUpdateTime;
}
