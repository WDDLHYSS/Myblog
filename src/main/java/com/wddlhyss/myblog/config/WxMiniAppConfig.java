package com.wddlhyss.myblog.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WxMiniAppConfig {

    @Value("${wx.miniapp.app-id}")
    private String appId;

    @Value("${wx.miniapp.secret}")
    private String secret;

    @Bean
    public WxMaService wxMaService() {
        WxMaDefaultConfigImpl config =
                new WxMaDefaultConfigImpl();

        config.setAppid(appId);
        config.setSecret(secret);

        WxMaService wxMaService =
                new WxMaServiceImpl();

        wxMaService.setWxMaConfig(config);

        return wxMaService;
    }
}