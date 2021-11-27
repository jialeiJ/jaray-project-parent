package com.vienna.jaray.controller.system;

import com.vienna.jaray.annotation.LogAnnotation;
import com.vienna.jaray.common.ResponseResult;
import com.vienna.jaray.entity.system.SysMenu;
import com.vienna.jaray.model.system.CommonParamsModel;
import com.vienna.jaray.service.system.SysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Jaray
 * @date 2020年09月10日 22:29
 * @description: 系统菜单控制器
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;

    @LogAnnotation(value = "查询菜单列表")
    @PostMapping("/find/all")
    public ResponseResult findTreeAll(CommonParamsModel commonParamsModel) {
        return sysMenuService.findTreeAll(commonParamsModel);
    }

    @LogAnnotation(value = "添加菜单")
    @PostMapping("/add")
    public ResponseResult add(SysMenu sysMenuEntity) {
        return sysMenuService.add(sysMenuEntity);
    }

    @LogAnnotation(value = "删除菜单")
    @PostMapping("/delete")
    public ResponseResult deleteByIds(String[] ids) {
        return sysMenuService.deleteByIds(ids);
    }

    @LogAnnotation(value = "更新菜单")
    @PostMapping("/update")
    public ResponseResult updateById(SysMenu sysMenuEntity) {
        return sysMenuService.updateById(sysMenuEntity);
    }

    @LogAnnotation(value = "查询菜单")
    @PostMapping("/find")
    public ResponseResult findById(String id) {
        return sysMenuService.findById(id);
    }
}
