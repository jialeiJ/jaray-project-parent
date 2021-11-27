package com.vienna.jaray.controller.system;

import com.vienna.jaray.annotation.LogAnnotation;
import com.vienna.jaray.common.ResponseResult;
import com.vienna.jaray.entity.system.SysDept;
import com.vienna.jaray.model.system.CommonParamsModel;
import com.vienna.jaray.service.system.SysDeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Jaray
 * @date 2020年09月10日 22:29
 * @description: 系统部门制器
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/sys/dept")
public class SysDeptController {
    @Autowired
    private SysDeptService sysDeptService;

    @LogAnnotation(value = "查询部门列表")
    @PostMapping("/find/all")
    public ResponseResult findAll(CommonParamsModel commonParamsModel) {
        return sysDeptService.findAllTop(commonParamsModel);
    }

    @LogAnnotation(value = "添加部门")
    @PostMapping("/add")
    public ResponseResult add(SysDept sysDeptEntity) {
        return sysDeptService.add(sysDeptEntity);
    }

    @LogAnnotation(value = "删除部门")
    @PostMapping("/delete")
    public ResponseResult deleteByIds(String ids) {
        return sysDeptService.deleteByIds(ids.split(","));
    }

    @LogAnnotation(value = "更新部门")
    @PostMapping("/update")
    public ResponseResult updateById(SysDept sysDeptEntity) {
        return sysDeptService.updateById(sysDeptEntity);
    }


    @LogAnnotation(value = "查询部门")
    @PostMapping("/find")
    public ResponseResult findById(String id) {
        return sysDeptService.findById(id);
    }
}
