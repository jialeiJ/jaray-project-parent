package com.vienna.jaray.entity.system;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jaray
 * @date 2020年09月10日 22:29
 * @description: 系统菜单实体类
 */
@Data
public class SysMenu {
	private Integer id;
	private String value;
	private String name;
	private String label;
	private String title;
	private Integer parentId;
	private String url;
	private String path;
	private String perm;
	private int type;
	private String icon;
	private int orderNum;
	private String createBy;
	private String createTime;
	private String lastUpdateBy;
	private String lastUpdateTime;
	private int delFlag;

	private List<SysMenu> perms;
	private List<SysMenu> children = new ArrayList<>();

}
