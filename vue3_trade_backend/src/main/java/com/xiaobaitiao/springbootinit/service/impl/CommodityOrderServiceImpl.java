package com.xiaobaitiao.springbootinit.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaobaitiao.springbootinit.common.ErrorCode;
import com.xiaobaitiao.springbootinit.constant.CommonConstant;
import com.xiaobaitiao.springbootinit.exception.ThrowUtils;
import com.xiaobaitiao.springbootinit.mapper.CommodityOrderMapper;
import com.xiaobaitiao.springbootinit.model.dto.commodityOrder.CommodityOrderQueryRequest;
import com.xiaobaitiao.springbootinit.model.entity.Commodity;
import com.xiaobaitiao.springbootinit.model.entity.CommodityOrder;
import com.xiaobaitiao.springbootinit.model.entity.User;
import com.xiaobaitiao.springbootinit.model.vo.CommodityOrderVO;
import com.xiaobaitiao.springbootinit.service.CommodityOrderService;
import com.xiaobaitiao.springbootinit.service.CommodityService;
import com.xiaobaitiao.springbootinit.service.UserService;
import com.xiaobaitiao.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品订单表服务实现
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@Service
@Slf4j
public class CommodityOrderServiceImpl extends ServiceImpl<CommodityOrderMapper, CommodityOrder> implements CommodityOrderService {

    @Resource
    private UserService userService;
    @Resource
    private CommodityService commodityService;
    /**
     * 校验数据
     *
     * @param commodityOrder
     * @param add            对创建的数据进行校验
     */
    @Override
    public void validCommodityOrder(CommodityOrder commodityOrder, boolean add) {
        ThrowUtils.throwIf(commodityOrder == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        Long userId = commodityOrder.getUserId();
        Long commodityId = commodityOrder.getCommodityId();
        Integer buyNumber = commodityOrder.getBuyNumber();

        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(commodityId == null || commodityId <= 0, ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(buyNumber == null || buyNumber <= 0, ErrorCode.PARAMS_ERROR);
        }

    }

    /**
     * 获取查询条件
     *
     * @param commodityOrderQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<CommodityOrder> getQueryWrapper(CommodityOrderQueryRequest commodityOrderQueryRequest) {
        QueryWrapper<CommodityOrder> queryWrapper = new QueryWrapper<>();
        if (commodityOrderQueryRequest == null) {
            return queryWrapper;
        }
        Long id = commodityOrderQueryRequest.getId();
        Long userId = commodityOrderQueryRequest.getUserId();
        Long commodityId = commodityOrderQueryRequest.getCommodityId();
        String remark = commodityOrderQueryRequest.getRemark();
        Integer buyNumber = commodityOrderQueryRequest.getBuyNumber();
        Integer payStatus = commodityOrderQueryRequest.getPayStatus();
        String sortField = commodityOrderQueryRequest.getSortField();
        String sortOrder = commodityOrderQueryRequest.getSortOrder();
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(remark), "remark", remark);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(payStatus), "payStatus", payStatus);
        queryWrapper.eq(ObjectUtils.isNotEmpty(buyNumber), "buyNumber", buyNumber);
        queryWrapper.eq(ObjectUtils.isNotEmpty(commodityId), "commodityId", commodityId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取商品订单表封装
     *
     * @param commodityOrder
     * @param request
     * @return
     */
    @Override
    public CommodityOrderVO getCommodityOrderVO(CommodityOrder commodityOrder, HttpServletRequest request) {
        // 对象转封装类
        CommodityOrderVO commodityOrderVO = CommodityOrderVO.objToVo(commodityOrder);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = commodityOrder.getUserId();
        User user = userService.getById(userId);
        commodityOrderVO.setUserName(user.getUserName());
        commodityOrderVO.setUserPhone(user.getUserPhone());

        return commodityOrderVO;
    }

    /**
     * 分页获取商品订单表封装
     *
     * @param commodityOrderPage
     * @param request
     * @return
     */
    @Override
    public Page<CommodityOrderVO> getCommodityOrderVOPage(Page<CommodityOrder> commodityOrderPage, HttpServletRequest request) {
        List<CommodityOrder> commodityOrderList = commodityOrderPage.getRecords();
        Page<CommodityOrderVO> commodityOrderVOPage = new Page<>(commodityOrderPage.getCurrent(), commodityOrderPage.getSize(), commodityOrderPage.getTotal());
        if (CollUtil.isEmpty(commodityOrderList)) {
            return commodityOrderVOPage;
        }

        // 对象列表 => 封装对象列表
        List<CommodityOrderVO> commodityOrderVOList = commodityOrderList.stream()
                .map(CommodityOrderVO::objToVo)
                .collect(Collectors.toList());

        // 关联查询用户信息
        Set<Long> userIdSet = commodityOrderList.stream()
                .map(CommodityOrder::getUserId)
                .collect(Collectors.toSet());
        // 关联查询商品信息
        Set<Long> commodityIdSet = commodityOrderList.stream()
                .map(CommodityOrder::getCommodityId)
                .collect(Collectors.toSet());
        // 批量查询用户信息
        Map<Long, User> userIdUserMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        // 批量查询商品信息
        Map<Long, Commodity> commodityIdMap = commodityService.listByIds(commodityIdSet).stream()
                .collect(Collectors.toMap(Commodity::getId, commodity -> commodity));
        // 填充用户信息到 VO 对象
        commodityOrderVOList.forEach(commodityOrderVO -> {
            User user = userIdUserMap.get(commodityOrderVO.getUserId());
            if (user != null) {
                commodityOrderVO.setUserName(user.getUserName());
                commodityOrderVO.setUserPhone(user.getUserPhone());
            }
        });
        // 填充商品信息到 VO 对象
        commodityOrderVOList.forEach(commodityOrderVO -> {
            Commodity commodity = commodityIdMap.get(commodityOrderVO.getCommodityId());
            if (commodity != null) {
                commodityOrderVO.setCommodityName(commodity.getCommodityName());
            }
        });
        commodityOrderVOPage.setRecords(commodityOrderVOList);
        return commodityOrderVOPage;
    }
    @Override
    public CommodityOrder getByIdWithLock(Long id) {
        return baseMapper.selectOne(new LambdaQueryWrapper<CommodityOrder>()
                .eq(CommodityOrder::getId, id)
                .last("FOR UPDATE"));
    }

    @Override
    public List<CommodityOrder> listByQuery(CommodityOrderQueryRequest queryRequest) {
        QueryWrapper<CommodityOrder> queryWrapper = new QueryWrapper<>();
        if (queryRequest.getUserId() != null) {
            queryWrapper.eq("userId", queryRequest.getUserId());
        }
        if (queryRequest.getPayStatus() != null) {
            queryWrapper.eq("payStatus", queryRequest.getPayStatus());
        }
        return this.list(queryWrapper);
    }
}