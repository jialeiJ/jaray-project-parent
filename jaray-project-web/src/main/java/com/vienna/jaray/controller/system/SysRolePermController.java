package com.vienna.jaray.controller.system;

import com.vienna.jaray.annotation.LogAnnotation;
import com.vienna.jaray.common.ResponseResult;
import com.vienna.jaray.entity.system.SysRolePerm;
import com.vienna.jaray.model.system.CommonParamsModel;
import com.vienna.jaray.service.system.SysRolePermService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Jaray
 * @date 2020年09月10日 22:29
 * @description: 系统角色权限控制器
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/sys/role/perm")
public class SysRolePermController {
    @Autowired
    private SysRolePermService sysRolePermService;

    @LogAnnotation(value = "查询角色权限列表")
    @PostMapping("/find/all")
    public ResponseResult findAll(CommonParamsModel commonParamsModel) {
        return sysRolePermService.findAll(commonParamsModel);
    }

    @LogAnnotation(value = "添加角色权限")
    @PostMapping("/add")
    public ResponseResult add(SysRolePerm sysRolePermEntity) {
        return sysRolePermService.add(sysRolePermEntity);
    }

    @LogAnnotation(value = "删除角色权限")
    @PostMapping("/delete")
    public ResponseResult deleteByIds(String ids) {
        return sysRolePermService.deleteByIds(ids.split(","));
    }

    @LogAnnotation(value = "更新角色权限")
    @PostMapping("/update")
    public ResponseResult updateByRid(SysRolePerm sysRolePermEntity) {
        return sysRolePermService.updateByRid(sysRolePermEntity);
    }

    @LogAnnotation(value = "查询角色权限")
    @PostMapping("/findByRid")
    public ResponseResult findByRid(String roleId) {
        return sysRolePermService.findByRid(roleId);
    }
}
