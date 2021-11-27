package com.vienna.jaray.controller.system;

import com.vienna.jaray.annotation.LogAnnotation;
import com.vienna.jaray.common.ResponseResult;
import com.vienna.jaray.entity.system.SysDict;
import com.vienna.jaray.model.system.CommonParamsModel;
import com.vienna.jaray.service.system.SysDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Jaray
 * @date 2020年09月10日 22:29
 * @description: 系统字典制器
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/sys/dict")
public class SysDictController {
    @Autowired
    private SysDictService sysDictService;

    @LogAnnotation(value = "查询字典列表")
    @PostMapping("/find/all")
    public ResponseResult findAll(CommonParamsModel commonParamsModel) {
        return sysDictService.findAll(commonParamsModel);
    }

    @LogAnnotation(value = "添加字典")
    @PostMapping("/add")
    public ResponseResult add(SysDict sysDictEntity) {
        return sysDictService.add(sysDictEntity);
    }

    @LogAnnotation(value = "删除字典")
    @PostMapping("/delete")
    public ResponseResult deleteByIds(String ids) {
        return sysDictService.deleteByIds(ids.split(","));
    }

    @LogAnnotation(value = "更新字典")
    @PostMapping("/update")
    public ResponseResult updateById(SysDict sysDictEntity) {
        return sysDictService.updateById(sysDictEntity);
    }


    @LogAnnotation(value = "查询字典")
    @PostMapping("/find")
    public ResponseResult findById(String id) {
        return sysDictService.findById(id);
    }

    @LogAnnotation(value = "查询字典")
    @PostMapping("/find/desc")
    public ResponseResult findByDesc(String description){
        return sysDictService.findByDesc(description);
    }
}
