package com.wddlhyss.myblog.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wddlhyss.myblog.entity.VO.WxLoginResponse;
import com.wddlhyss.myblog.entity.WxUser;
import com.wddlhyss.myblog.mapper.WxUserMapper;
import com.wddlhyss.myblog.service.IWxUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wddlhyss.myblog.utils.JwtUtils;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 小程序用户表 服务实现类
 * </p>
 *
 * @author haoyanlu
 * @since 2026-08-02
 */
@Service
public class WxUserServiceImpl extends ServiceImpl<WxUserMapper, WxUser> implements IWxUserService {

    private final WxMaService wxMaService;

    private final JwtUtils jwtUtils;

    public WxUserServiceImpl(WxMaService wxMaService, JwtUtils jwtUtils) {
        this.wxMaService = wxMaService;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public WxLoginResponse login(String code) throws WxErrorException {

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("微信登录code不能为空");
        }

        WxMaJscode2SessionResult sessionResult = wxMaService.getUserService().getSessionInfo(code);
        String openId = sessionResult.getOpenid();

        if (openId == null || openId.isBlank()) {
            throw new IllegalStateException("微信服务器未返回openId");
        }

        WxUser wxUser =
                baseMapper.selectOne(
                        new LambdaQueryWrapper<WxUser>()
                                .eq(
                                        WxUser::getOpenId,
                                        openId
                                )
                );

        /*
         * 用户不存在：创建本地用户。
         */
        if (wxUser == null) {
            wxUser = new WxUser();

            wxUser.setOpenId(openId);
            wxUser.setStatus((byte) 1);

            wxUser.setNickName(null);
            wxUser.setAvatarUrl(null);

            baseMapper.insert(wxUser);
        } else {
            /*
             * 用户已存在：检查账号状态，
             * 不再重复插入数据库。
             */
            if (wxUser.getStatus() == null || wxUser.getStatus() != (byte) 1) {
                throw new IllegalStateException("该用户已被停用");
            }
        }
        String token = jwtUtils.generateToken(wxUser.getId());

        return new WxLoginResponse(token);
    }
}
