package com.vienna.jaray.controller.system;

import com.vienna.jaray.annotation.LogAnnotation;
import com.vienna.jaray.common.ResponseResult;
import com.vienna.jaray.entity.system.SysRole;
import com.vienna.jaray.model.system.CommonParamsModel;
import com.vienna.jaray.service.system.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Jaray
 * @date 2020年09月10日 22:29
 * @description: 系统角色控制器
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/sys/role")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    @LogAnnotation(value = "查询角色列表")
    @PostMapping("/find/all")
    public ResponseResult findAll(CommonParamsModel commonParamsModel) {
        return sysRoleService.findAll(commonParamsModel);
    }

    @LogAnnotation(value = "添加角色")
    @PostMapping("/add")
    public ResponseResult add(SysRole sysRoleEntity) {
        return sysRoleService.add(sysRoleEntity);
    }

    @LogAnnotation(value = "删除角色")
    @PostMapping("/delete")
    public ResponseResult deleteByIds(String ids) {
        return sysRoleService.deleteByIds(ids.split(","));
    }

    @LogAnnotation(value = "更新角色")
    @PostMapping("/update")
    public ResponseResult updateById(SysRole sysRoleEntity) {
        return sysRoleService.updateById(sysRoleEntity);
    }


    @LogAnnotation(value = "查询角色")
    @PostMapping("/find")
    public ResponseResult findById(String id) {
        return sysRoleService.findById(id);
    }
}
