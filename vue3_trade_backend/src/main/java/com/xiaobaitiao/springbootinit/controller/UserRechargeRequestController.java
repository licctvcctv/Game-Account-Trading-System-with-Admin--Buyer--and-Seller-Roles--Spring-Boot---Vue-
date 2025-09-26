package com.xiaobaitiao.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaobaitiao.springbootinit.annotation.AuthCheck;
import com.xiaobaitiao.springbootinit.common.BaseResponse;
import com.xiaobaitiao.springbootinit.common.ErrorCode;
import com.xiaobaitiao.springbootinit.common.ResultUtils;
import com.xiaobaitiao.springbootinit.constant.UserConstant;
import com.xiaobaitiao.springbootinit.exception.BusinessException;
import com.xiaobaitiao.springbootinit.exception.ThrowUtils;
import com.xiaobaitiao.springbootinit.model.dto.userRecharge.UserRechargeApplyRequest;
import com.xiaobaitiao.springbootinit.model.dto.userRecharge.UserRechargeQueryRequest;
import com.xiaobaitiao.springbootinit.model.dto.userRecharge.UserRechargeReviewRequest;
import com.xiaobaitiao.springbootinit.model.entity.User;
import com.xiaobaitiao.springbootinit.model.entity.UserRechargeRequest;
import com.xiaobaitiao.springbootinit.model.vo.UserRechargeRequestVO;
import com.xiaobaitiao.springbootinit.service.UserRechargeRequestService;
import com.xiaobaitiao.springbootinit.service.UserService;
import java.util.Objects;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户充值申请接口
 */
@RestController
@RequestMapping("/userRecharge")
@Slf4j
public class UserRechargeRequestController {

    @Resource
    private UserRechargeRequestService userRechargeRequestService;

    @Resource
    private UserService userService;

    /**
     * 提交充值申请
     */
    @PostMapping("/apply")
    public BaseResponse<Long> applyRecharge(@RequestBody UserRechargeApplyRequest applyRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(applyRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        UserRechargeRequest entity = new UserRechargeRequest();
        BeanUtils.copyProperties(applyRequest, entity);
        entity.setStatus(0);
        entity.setUserId(loginUser.getId());
        if (entity.getPayChannel() != null) {
            entity.setPayChannel(entity.getPayChannel().toUpperCase());
        }
        userRechargeRequestService.validUserRechargeRequest(entity, true);
        boolean saved = userRechargeRequestService.save(entity);
        ThrowUtils.throwIf(!saved, ErrorCode.OPERATION_ERROR, "提交充值申请失败");
        return ResultUtils.success(entity.getId());
    }

    /**
     * 分页获取个人充值申请列表
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<UserRechargeRequestVO>> listMyRechargeRequest(@RequestBody UserRechargeQueryRequest queryRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        queryRequest.setUserId(loginUser.getId());
        Page<UserRechargeRequest> page = userRechargeRequestService.page(
                new Page<>(queryRequest.getCurrent(), queryRequest.getPageSize()),
                userRechargeRequestService.getQueryWrapper(queryRequest));
        return ResultUtils.success(userRechargeRequestService.getUserRechargeRequestVOPage(page, request));
    }

    /**
     * 管理员分页获取充值申请列表
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserRechargeRequestVO>> listRechargeRequestByPage(
            @RequestBody UserRechargeQueryRequest queryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR);
        Page<UserRechargeRequest> page = userRechargeRequestService.page(
                new Page<>(queryRequest.getCurrent(), queryRequest.getPageSize()),
                userRechargeRequestService.getQueryWrapper(queryRequest));
        return ResultUtils.success(userRechargeRequestService.getUserRechargeRequestVOPage(page, request));
    }

    /**
     * 审核充值申请
     */
    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> reviewRecharge(@RequestBody UserRechargeReviewRequest reviewRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(reviewRequest == null, ErrorCode.PARAMS_ERROR);
        User reviewer = userService.getLoginUser(request);
        if (!userService.isAdmin(reviewer)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        userRechargeRequestService.reviewRechargeRequest(reviewRequest, reviewer);
        return ResultUtils.success(true);
    }
}
