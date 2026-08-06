package com.wddlhyss.myblog.service;

import com.wddlhyss.myblog.entity.VO.WxLoginResponse;
import com.wddlhyss.myblog.entity.WxUser;
import com.baomidou.mybatisplus.extension.service.IService;
import me.chanjar.weixin.common.error.WxErrorException;

/**
 * <p>
 * 小程序用户表 服务类
 * </p>
 *
 * @author haoyanlu
 * @since 2026-08-02
 */
public interface IWxUserService extends IService<WxUser> {

    WxLoginResponse login(String code) throws WxErrorException;
}
