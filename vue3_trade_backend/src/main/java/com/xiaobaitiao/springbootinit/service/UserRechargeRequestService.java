package com.xiaobaitiao.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaobaitiao.springbootinit.model.dto.userRecharge.UserRechargeQueryRequest;
import com.xiaobaitiao.springbootinit.model.dto.userRecharge.UserRechargeReviewRequest;
import com.xiaobaitiao.springbootinit.model.entity.UserRechargeRequest;
import com.xiaobaitiao.springbootinit.model.entity.User;
import com.xiaobaitiao.springbootinit.model.vo.UserRechargeRequestVO;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户充值申请服务
 */
public interface UserRechargeRequestService extends IService<UserRechargeRequest> {

    void validUserRechargeRequest(UserRechargeRequest request, boolean add);

    QueryWrapper<UserRechargeRequest> getQueryWrapper(UserRechargeQueryRequest queryRequest);

    UserRechargeRequestVO getUserRechargeRequestVO(UserRechargeRequest request, HttpServletRequest httpRequest);

    Page<UserRechargeRequestVO> getUserRechargeRequestVOPage(Page<UserRechargeRequest> page, HttpServletRequest httpRequest);

    void reviewRechargeRequest(UserRechargeReviewRequest reviewRequest, User reviewer);
}
