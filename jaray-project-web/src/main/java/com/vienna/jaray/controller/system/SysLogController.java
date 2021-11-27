package com.vienna.jaray.controller.system;

import com.vienna.jaray.annotation.LogAnnotation;
import com.vienna.jaray.common.ResponseResult;
import com.vienna.jaray.entity.system.SysLog;
import com.vienna.jaray.model.system.CommonParamsModel;
import com.vienna.jaray.service.system.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Jaray
 * @date 2020年09月10日 22:29
 * @description: 系统日志制器
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/sys/log")
public class SysLogController {
    @Autowired
    private SysLogService sysLogService;

    @LogAnnotation(value = "查询日志列表")
    @PostMapping("/find/all")
    public ResponseResult findAll(CommonParamsModel commonParamsModel) {
        return sysLogService.findAll(commonParamsModel);
    }

    @LogAnnotation(value = "添加日志")
    @PostMapping("/add")
    public ResponseResult add(SysLog sysLogEntity) {
        return sysLogService.add(sysLogEntity);
    }

    @LogAnnotation(value = "删除日志")
    @PostMapping("/delete")
    public ResponseResult deleteByIds(String ids) {
        return sysLogService.deleteByIds(ids.split(","));
    }

    @LogAnnotation(value = "更新日志")
    @PostMapping("/update")
    public ResponseResult updateById(SysLog sysLogEntity) {
        return sysLogService.updateById(sysLogEntity);
    }


    @LogAnnotation(value = "查询日志")
    @PostMapping("/find")
    public ResponseResult findById(String id) {
        return sysLogService.findById(id);
    }
}
