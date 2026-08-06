package com.wddlhyss.myblog.controller;

import com.wddlhyss.myblog.entity.VO.WxLoginResponse;
import com.wddlhyss.myblog.entity.WxLoginRequest;
import com.wddlhyss.myblog.service.IWxUserService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

/**
 * <p>
 * 小程序用户表 前端控制器
 * </p>
 *
 * @author haoyanlu
 * @since 2026-08-02
 */
@RestController
@CrossOrigin
@RequestMapping("/wxUser")
public class WxUserController {

    @Autowired
    private IWxUserService wxUserService;

    @PostMapping(("/login"))
    public WxLoginResponse login(@RequestBody WxLoginRequest wxLoginRequest) throws WxErrorException {

        return wxUserService.login(wxLoginRequest.getCode());
    }

}
