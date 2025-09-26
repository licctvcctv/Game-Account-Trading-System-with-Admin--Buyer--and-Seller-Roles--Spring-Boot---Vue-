package com.xiaobaitiao.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaobaitiao.springbootinit.common.ErrorCode;
import com.xiaobaitiao.springbootinit.exception.BusinessException;
import com.xiaobaitiao.springbootinit.exception.ThrowUtils;
import com.xiaobaitiao.springbootinit.mapper.UserRechargeRequestMapper;
import com.xiaobaitiao.springbootinit.model.dto.userRecharge.UserRechargeQueryRequest;
import com.xiaobaitiao.springbootinit.model.dto.userRecharge.UserRechargeReviewRequest;
import com.xiaobaitiao.springbootinit.model.entity.User;
import com.xiaobaitiao.springbootinit.model.entity.UserRechargeRequest;
import com.xiaobaitiao.springbootinit.model.vo.UserRechargeRequestVO;
import com.xiaobaitiao.springbootinit.service.UserRechargeRequestService;
import com.xiaobaitiao.springbootinit.service.UserService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户充值申请服务实现
 */
@Service
@Slf4j
public class UserRechargeRequestServiceImpl extends ServiceImpl<UserRechargeRequestMapper, UserRechargeRequest>
        implements UserRechargeRequestService {

    private static final String CHANNEL_WECHAT = "WECHAT";
    private static final String CHANNEL_ALIPAY = "ALIPAY";

    @Resource
    private UserService userService;

    @Override
    public void validUserRechargeRequest(UserRechargeRequest request, boolean add) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        BigDecimal amount = request.getAmount();
        String payChannel = request.getPayChannel();
        if (add) {
            ThrowUtils.throwIf(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0, ErrorCode.PARAMS_ERROR, "充值金额必须大于0");
            ThrowUtils.throwIf(StringUtils.isBlank(payChannel), ErrorCode.PARAMS_ERROR, "支付渠道不能为空");
        }
        if (amount != null) {
            ThrowUtils.throwIf(amount.compareTo(BigDecimal.ZERO) <= 0, ErrorCode.PARAMS_ERROR, "充值金额必须大于0");
        }
        if (StringUtils.isNotBlank(payChannel)) {
            String upperChannel = payChannel.toUpperCase();
            boolean supported = CHANNEL_WECHAT.equals(upperChannel) || CHANNEL_ALIPAY.equals(upperChannel);
            ThrowUtils.throwIf(!supported, ErrorCode.PARAMS_ERROR, "不支持的支付渠道");
        }
    }

    @Override
    public QueryWrapper<UserRechargeRequest> getQueryWrapper(UserRechargeQueryRequest queryRequest) {
        QueryWrapper<UserRechargeRequest> queryWrapper = new QueryWrapper<>();
        if (queryRequest == null) {
            return queryWrapper;
        }
        Long id = queryRequest.getId();
        Long userId = queryRequest.getUserId();
        Integer status = queryRequest.getStatus();
        Long reviewerId = queryRequest.getReviewerId();
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(reviewerId), "reviewerId", reviewerId);
        queryWrapper.orderByDesc("createTime");
        return queryWrapper;
    }

    @Override
    public UserRechargeRequestVO getUserRechargeRequestVO(UserRechargeRequest request, HttpServletRequest httpRequest) {
        UserRechargeRequestVO vo = UserRechargeRequestVO.objToVo(request);
        if (vo == null) {
            return null;
        }
        Long userId = request.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            if (user != null) {
                vo.setUserName(user.getUserName());
            }
        }
        Long reviewerId = request.getReviewerId();
        if (reviewerId != null) {
            User reviewer = userService.getById(reviewerId);
            if (reviewer != null) {
                vo.setReviewerName(reviewer.getUserName());
            }
        }
        return vo;
    }

    @Override
    public Page<UserRechargeRequestVO> getUserRechargeRequestVOPage(Page<UserRechargeRequest> page,
            HttpServletRequest request) {
        List<UserRechargeRequest> records = page.getRecords();
        Page<UserRechargeRequestVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        if (CollUtil.isEmpty(records)) {
            return voPage;
        }
        Set<Long> userIds = records.stream().map(UserRechargeRequest::getUserId).collect(Collectors.toSet());
        Set<Long> reviewerIds = records.stream().map(UserRechargeRequest::getReviewerId)
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toSet());
        Map<Long, User> userMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        Map<Long, User> reviewerMap = reviewerIds.isEmpty() ? Map.of() : userService.listByIds(reviewerIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<UserRechargeRequestVO> voList = records.stream().map(record -> {
            UserRechargeRequestVO vo = UserRechargeRequestVO.objToVo(record);
            User user = userMap.get(record.getUserId());
            if (user != null) {
                vo.setUserName(user.getUserName());
            }
            if (record.getReviewerId() != null) {
                User reviewer = reviewerMap.get(record.getReviewerId());
                if (reviewer != null) {
                    vo.setReviewerName(reviewer.getUserName());
                }
            }
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewRechargeRequest(UserRechargeReviewRequest reviewRequest, User reviewer) {
        ThrowUtils.throwIf(reviewRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(reviewer == null || reviewer.getId() == null, ErrorCode.NO_AUTH_ERROR);
        Long id = reviewRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        Integer status = reviewRequest.getStatus();
        ThrowUtils.throwIf(status == null || (status != 1 && status != 2), ErrorCode.PARAMS_ERROR, "不支持的审核状态");
        UserRechargeRequest rechargeRequest = this.getById(id);
        if (rechargeRequest == null || rechargeRequest.getIsDelete() != null && rechargeRequest.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "充值申请不存在");
        }
        if (!Integer.valueOf(0).equals(rechargeRequest.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "充值申请已被处理");
        }
        rechargeRequest.setStatus(status);
        rechargeRequest.setReviewerId(reviewer.getId());
        rechargeRequest.setReviewMessage(reviewRequest.getReviewMessage());
        rechargeRequest.setReviewTime(new Date());
        boolean update = this.updateById(rechargeRequest);
        ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "更新充值申请失败");
        if (status == 1) {
            User user = userService.getByIdWithLock(rechargeRequest.getUserId());
            ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
            BigDecimal newBalance = user.getBalance().add(rechargeRequest.getAmount());
            user.setBalance(newBalance);
            boolean updated = userService.updateById(user);
            ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "更新用户余额失败");
        }
    }
}
