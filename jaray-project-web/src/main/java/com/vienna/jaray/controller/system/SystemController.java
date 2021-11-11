package com.vienna.jaray.controller.system;

import com.vienna.jaray.common.ResponseResult;
import com.vienna.jaray.entity.system.SysUserToken;
import com.vienna.jaray.service.system.KaptchaService;
import com.vienna.jaray.service.system.SystemService;
import com.vienna.jaray.service.system.SysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotEmpty;

/**
 * @author Jaray
 * @date 2020年09月10日 22:29
 * @description: 系统控制器
 */
@Slf4j
@CrossOrigin
@Validated
@RestController
@RequestMapping("/system")
public class SystemController {

    @Autowired
    HttpServletRequest request;
    @Autowired
    HttpServletResponse response;
    @Autowired
    HttpSession session;
    @Autowired
    private KaptchaService kaptchaService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private SysMenuService sysMenuService;

    @GetMapping("/captcha.jpg")
    public void kaptchaImage() throws Exception {
        kaptchaService.kaptchaImage(request, response, session);
    }

    @PostMapping("/login")
    public ResponseResult login(String username, String password, String captcha) {
        return systemService.login(request, username, password, captcha, session);
    }

    @PostMapping("/findLeftNav")
    public ResponseResult findLeftNav(String userId){
        return sysMenuService.findLeftNav(userId);
    }

    @PostMapping("/refreshToken")
    public ResponseResult refreshToken(HttpServletRequest request, SysUserToken sysUserToken){
        return systemService.reLogin(request, sysUserToken);
    }

    @PostMapping("/updatePassword")
    public ResponseResult updatePassword(String id, String password){
        return systemService.updatePassword(id, password);
    }

}
