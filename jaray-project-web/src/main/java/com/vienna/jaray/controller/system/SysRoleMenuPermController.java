package com.vienna.jaray.controller.system;

import com.vienna.jaray.annotation.LogAnnotation;
import com.vienna.jaray.common.ResponseResult;
import com.vienna.jaray.entity.system.SysRoleMenu;
import com.vienna.jaray.model.system.CommonParamsModel;
import com.vienna.jaray.service.system.SysRoleMenuPermService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Jaray
 * @date 2020年09月10日 22:29
 * @description: 系统角色菜单权限控制器
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/sys/role/menu/perm")
public class SysRoleMenuPermController {
    @Autowired
    private SysRoleMenuPermService sysRoleMenuPermService;

    @LogAnnotation(value = "查询角色菜单权限列表")
    @PostMapping("/find/all")
    public ResponseResult findAll(CommonParamsModel commonParamsModel) {
        return sysRoleMenuPermService.findAll(commonParamsModel);
    }

    @LogAnnotation(value = "添加角色菜单权限")
    @PostMapping("/add")
    public ResponseResult add(SysRoleMenu sysRoleMenu) {
        return sysRoleMenuPermService.add(sysRoleMenu);
    }

    @LogAnnotation(value = "删除角色菜单权限")
    @PostMapping("/delete")
    public ResponseResult deleteByRids(String roleIds) {
        return sysRoleMenuPermService.deleteByIds(roleIds.split(","));
    }

    @LogAnnotation(value = "更新角色菜单权限")
    @PostMapping("/update")
    public ResponseResult updateByRid(Integer roleId, String menuPermId) {
        return sysRoleMenuPermService.updateByRid(roleId, menuPermId);
    }

    @LogAnnotation(value = "查询角色菜单权限")
    @PostMapping("/findByRid")
    public ResponseResult findByRid(String roleId) {
        return sysRoleMenuPermService.findByRid(roleId);
    }
}
