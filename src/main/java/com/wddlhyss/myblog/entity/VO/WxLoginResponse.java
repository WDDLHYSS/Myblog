package com.wddlhyss.myblog.entity.VO;

public class WxLoginResponse {

    private String token;

    public WxLoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}