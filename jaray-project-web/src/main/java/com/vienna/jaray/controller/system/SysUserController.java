package com.vienna.jaray.controller.system;

import com.vienna.jaray.annotation.LogAnnotation;
import com.vienna.jaray.common.ResponseResult;
import com.vienna.jaray.entity.system.SysUser;
import com.vienna.jaray.model.system.CommonParamsModel;
import com.vienna.jaray.service.system.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

/**
 * @author Jaray
 * @date 2020年09月10日 22:29
 * @description: 系统用户控制器
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/sys/user")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    @LogAnnotation(value = "查询用户列表")
    @PostMapping("/find/all")
    public ResponseResult findAll(CommonParamsModel commonParamsModel) {
        return sysUserService.findAll(commonParamsModel);
    }

    @LogAnnotation(value = "添加用户")
    @PostMapping("/add")
    public ResponseResult add(SysUser sysUserEntity) {
        return sysUserService.add(sysUserEntity);
    }

    @LogAnnotation(value = "删除用户")
    @PostMapping("/deleteByIds")
    public ResponseResult deleteByIds(String ids) {
        return sysUserService.deleteByIds(ids.split(","));
    }

    @LogAnnotation(value = "更新用户")
    @PostMapping("/updateById")
    public ResponseResult updateById(SysUser sysUserEntity) {
        return sysUserService.updateById(sysUserEntity);
    }


    @LogAnnotation(value = "查询用户")
    @PostMapping("/findById")
    public ResponseResult findById(String id) {
        return sysUserService.findById(id);
    }


    @LogAnnotation(value = "文件上传")
    @PostMapping("/uploadFile")
    public ResponseResult uploadFile(@RequestParam("file")MultipartFile file, String userId, String userNme) {
        log.info("上传文件用户为：{}-{}", userNme, userId);

        if (file.isEmpty()) {
            return ResponseResult.fail();
        }

        String fileName = file.getOriginalFilename();
        log.info("上传文件名为：{}", fileName);

        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        log.info("上传文件后缀名为：{}", suffixName);

        long fileSize = file.getSize();
        log.info("上传文件大小为：{}", fileSize);

        return ResponseResult.success().add("fileName", fileName);
    }
}
