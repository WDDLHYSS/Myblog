package com.wddlhyss.myblog.controller;

import com.wddlhyss.myblog.entity.VO.SavedScheduleResponse;
import com.wddlhyss.myblog.entity.VO.ScheduleRow;
import com.wddlhyss.myblog.entity.VO.ScheduleRuleResponse;
import com.wddlhyss.myblog.entity.dto.MakeSchedulePlanOfNormalRequest;
import com.wddlhyss.myblog.entity.dto.MakeSchedulePlanOfSpeciallRequest;
import com.wddlhyss.myblog.entity.dto.ScheduleTestRequest;
import com.wddlhyss.myblog.service.*;
import com.wddlhyss.myblog.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户个人排班方案表 前端控制器
 * </p>
 *
 * @author haoyanlu
 * @since 2026-08-02
 */
@RestController
@CrossOrigin
@RequestMapping("/userSchedulePlan")
public class UserSchedulePlanController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private IUserSchedulePlanService userSchedulePlanService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 新增normal规则
     * @param makeSchedulePlanOfNormalRequest
     * @param authorization
     * @return ruleId
     */
    @PostMapping("/addRule")
    public Long makeScheduleNormalPlan(@RequestBody MakeSchedulePlanOfNormalRequest makeSchedulePlanOfNormalRequest,
                                 @RequestHeader("Authorization")String authorization ) {
        String token = jwtUtils.extractToken(authorization);
        Long userId = jwtUtils.parseUserId(token);

        return userSchedulePlanService.makeScheduleNormalPlan(userId, makeSchedulePlanOfNormalRequest);
    }
    /**
     * 新增special规则
     * @param makeSchedulePlanOfSpeciallRequest
     * @param authorization
     * @return ruleId
     */
    @PostMapping("/addSpecialRule")
    public Long makeScheduleSpecialPlan(@RequestBody MakeSchedulePlanOfSpeciallRequest makeSchedulePlanOfSpeciallRequest,
                                 @RequestHeader("Authorization")String authorization ) {
        String token = jwtUtils.extractToken(authorization);
        Long userId = jwtUtils.parseUserId(token);

        return userSchedulePlanService.makeScheduleSpecialPlan(userId, makeSchedulePlanOfSpeciallRequest);
    }

    /**
     * 查询用户所有设定规则
     * @param authorization
     * @return
     */
   @GetMapping("/findRules")
   public List<ScheduleRuleResponse> findRules(@RequestHeader("Authorization")String authorization) {
        String token = jwtUtils.extractToken(authorization);
        Long userId = jwtUtils.parseUserId(token);

        return userSchedulePlanService.findUserRule(userId);
   }

    /**
     * 删除创建的规则
     * @param ruleId
     * @param authorization
     * @return
     */

    @DeleteMapping("/deleteRules/{ruleId}")
    public boolean deleteRule(@PathVariable("ruleId") Long ruleId,
                              @RequestHeader("Authorization")String authorization) {
        String token = jwtUtils.extractToken(authorization);
        Long userId = jwtUtils.parseUserId(token);

       return userSchedulePlanService.deleteByRulesId(ruleId,userId);
    }

    /**
     * 根据ruleid查询规则
     * @param ruleId
     * @param authorization
     * @return
     */
    @GetMapping("/editFindSchedulePlan/{ruleId}")
    public ScheduleRuleResponse editSchedulePlan(@PathVariable("ruleId") Long ruleId,
                                                 @RequestHeader("Authorization")String authorization){
        String token = jwtUtils.extractToken(authorization);
        Long userId = jwtUtils.parseUserId(token);

        return userSchedulePlanService.getRuleDetail(userId, ruleId);
    }

    /**
     * 根据修改normal规则
     * @param ruleId
     * @param request
     * @param authorization
     * @return
     */
    @PutMapping("/editSchedulePlan/{ruleId}")
    public boolean updateNormalRule(@PathVariable Long ruleId,
                              @RequestBody MakeSchedulePlanOfNormalRequest request,
                              @RequestHeader("Authorization") String authorization) {

        String token = jwtUtils.extractToken(authorization);
        Long userId = jwtUtils.parseUserId(token);

        return userSchedulePlanService.updateRule(userId, ruleId, request);
    }

    /**
     * 根据修改special规则
     * @param ruleId
     * @param request
     * @param authorization
     * @return
     */
    @PutMapping("/editSpecialRule/{ruleId}")
    public boolean updateSpecialRule(@PathVariable Long ruleId,
                              @RequestBody MakeSchedulePlanOfSpeciallRequest request,
                              @RequestHeader("Authorization") String authorization) {

        String token = jwtUtils.extractToken(authorization);
        Long userId = jwtUtils.parseUserId(token);

        return userSchedulePlanService.updateSpecialRule(userId, ruleId, request);
    }

    @PostMapping("/makeSchedulePlanRow")
    public List<ScheduleRow> makeSchedulePlanRow(
            @RequestBody ScheduleTestRequest request,
            @RequestHeader("Authorization") String authorization) {

        String token = jwtUtils.extractToken(authorization);
        Long userId = jwtUtils.parseUserId(token);

        return userSchedulePlanService.makeSchedulePlanRow(userId,request);
    }

    /**
     * 校验登录已经排版用户进入时生成
     * @param ruleId
     * @param authorization
     * @return
     */
    @GetMapping("/savedSchedule")
    public SavedScheduleResponse getSavedSchedule(
            @RequestParam("ruleId") Long ruleId,
            @RequestHeader("Authorization") String authorization
    ) {
        String token = jwtUtils.extractToken(authorization);
        Long userId = jwtUtils.parseUserId(token);

        return userSchedulePlanService.getSavedSchedule(userId, ruleId);
    }

    /**
     * Swagger测试排班规则。
     */
    @PostMapping("/testRule")
    public List<ScheduleRow> testRule(
            @RequestBody ScheduleTestRequest request
    ) {
        return scheduleService.testRule(request);
    }


}
