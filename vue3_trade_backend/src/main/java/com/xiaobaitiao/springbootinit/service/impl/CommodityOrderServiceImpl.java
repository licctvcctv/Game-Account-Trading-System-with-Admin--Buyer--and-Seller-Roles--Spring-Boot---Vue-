package com.xiaobaitiao.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaobaitiao.springbootinit.common.ErrorCode;
import com.xiaobaitiao.springbootinit.constant.CommonConstant;
import com.xiaobaitiao.springbootinit.exception.BusinessException;
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
import java.util.Collections;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品订单表服务实现
 */
@Service
@Slf4j
public class CommodityOrderServiceImpl extends ServiceImpl<CommodityOrderMapper, CommodityOrder> implements CommodityOrderService {

    @Resource
    private UserService userService;
    @Resource
    private CommodityService commodityService;

    @Override
    public void validCommodityOrder(CommodityOrder commodityOrder, boolean add) {
        ThrowUtils.throwIf(commodityOrder == null, ErrorCode.PARAMS_ERROR);
        Long userId = commodityOrder.getUserId();
        Long commodityId = commodityOrder.getCommodityId();
        Integer buyNumber = commodityOrder.getBuyNumber();
        Integer rentalDuration = commodityOrder.getRentalDuration();
        Integer tradeType = commodityOrder.getTradeType();
        String rentalUnit = commodityOrder.getRentalUnit();

        if (add) {
            ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(commodityId == null || commodityId <= 0, ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(tradeType == null || (tradeType != 1 && tradeType != 2), ErrorCode.PARAMS_ERROR);
            if (tradeType == 1) {
                ThrowUtils.throwIf(buyNumber == null || buyNumber <= 0, ErrorCode.PARAMS_ERROR);
            } else {
                ThrowUtils.throwIf(rentalDuration == null || rentalDuration <= 0, ErrorCode.PARAMS_ERROR);
                ThrowUtils.throwIf(StringUtils.isBlank(rentalUnit), ErrorCode.PARAMS_ERROR);
            }
        }
        if (buyNumber != null) {
            ThrowUtils.throwIf(buyNumber < 0, ErrorCode.PARAMS_ERROR);
        }
        if (rentalDuration != null) {
            ThrowUtils.throwIf(rentalDuration <= 0, ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isNotBlank(rentalUnit)) {
            ThrowUtils.throwIf(rentalUnit.length() > 16, ErrorCode.PARAMS_ERROR);
        }
    }

    @Override
    public QueryWrapper<CommodityOrder> getQueryWrapper(CommodityOrderQueryRequest commodityOrderQueryRequest) {
        QueryWrapper<CommodityOrder> queryWrapper = new QueryWrapper<>();
        if (commodityOrderQueryRequest == null) {
            return queryWrapper;
        }
        Long id = commodityOrderQueryRequest.getId();
        Long userId = commodityOrderQueryRequest.getUserId();
        Long sellerId = commodityOrderQueryRequest.getSellerId();
        Long commodityId = commodityOrderQueryRequest.getCommodityId();
        String remark = commodityOrderQueryRequest.getRemark();
        Integer buyNumber = commodityOrderQueryRequest.getBuyNumber();
        Integer rentalDuration = commodityOrderQueryRequest.getRentalDuration();
        String rentalUnit = commodityOrderQueryRequest.getRentalUnit();
        Integer tradeType = commodityOrderQueryRequest.getTradeType();
        Integer payStatus = commodityOrderQueryRequest.getPayStatus();
        Integer deliveryStatus = commodityOrderQueryRequest.getDeliveryStatus();
        Integer finishStatus = commodityOrderQueryRequest.getFinishStatus();
        String sortField = commodityOrderQueryRequest.getSortField();
        String sortOrder = commodityOrderQueryRequest.getSortOrder();

        queryWrapper.like(StringUtils.isNotBlank(remark), "remark", remark);
        queryWrapper.eq(ObjectUtils.isNotEmpty(payStatus), "payStatus", payStatus);
        queryWrapper.eq(ObjectUtils.isNotEmpty(buyNumber), "buyNumber", buyNumber);
        queryWrapper.eq(ObjectUtils.isNotEmpty(rentalDuration), "rentalDuration", rentalDuration);
        queryWrapper.eq(StringUtils.isNotBlank(rentalUnit), "rentalUnit", rentalUnit);
        queryWrapper.eq(ObjectUtils.isNotEmpty(tradeType), "tradeType", tradeType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(commodityId), "commodityId", commodityId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(sellerId), "sellerId", sellerId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(deliveryStatus), "deliveryStatus", deliveryStatus);
        queryWrapper.eq(ObjectUtils.isNotEmpty(finishStatus), "finishStatus", finishStatus);

        if (SqlUtils.validSortField(sortField)) {
            boolean isAsc = StringUtils.equals(sortOrder, CommonConstant.SORT_ORDER_ASC);
            queryWrapper.orderBy(true, isAsc, sortField);
        } else {
            queryWrapper.orderByDesc("createTime");
        }
        return queryWrapper;
    }

    @Override
    public CommodityOrderVO getCommodityOrderVO(CommodityOrder commodityOrder, HttpServletRequest request) {
        CommodityOrderVO commodityOrderVO = CommodityOrderVO.objToVo(commodityOrder);
        if (commodityOrderVO == null) {
            return null;
        }
        Long userId = commodityOrder.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            if (user != null) {
                commodityOrderVO.setUserName(user.getUserName());
                commodityOrderVO.setUserPhone(user.getUserPhone());
            }
        }
        Long sellerId = commodityOrder.getSellerId();
        if (sellerId != null) {
            User seller = userService.getById(sellerId);
            if (seller != null) {
                commodityOrderVO.setSellerName(seller.getUserName());
            }
        }
        return commodityOrderVO;
    }

    @Override
    public Page<CommodityOrderVO> getCommodityOrderVOPage(Page<CommodityOrder> commodityOrderPage, HttpServletRequest request) {
        List<CommodityOrder> commodityOrderList = commodityOrderPage.getRecords();
        Page<CommodityOrderVO> commodityOrderVOPage = new Page<>(commodityOrderPage.getCurrent(), commodityOrderPage.getSize(), commodityOrderPage.getTotal());
        if (CollUtil.isEmpty(commodityOrderList)) {
            return commodityOrderVOPage;
        }
        List<CommodityOrderVO> commodityOrderVOList = commodityOrderList.stream()
                .map(CommodityOrderVO::objToVo)
                .collect(Collectors.toList());

        Set<Long> userIdSet = commodityOrderList.stream()
                .map(CommodityOrder::getUserId)
                .collect(Collectors.toSet());
        Set<Long> sellerIdSet = commodityOrderList.stream()
                .map(CommodityOrder::getSellerId)
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toSet());
        Set<Long> commodityIdSet = commodityOrderList.stream()
                .map(CommodityOrder::getCommodityId)
                .collect(Collectors.toSet());

        Map<Long, User> userIdUserMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        Map<Long, User> sellerIdUserMap = sellerIdSet.isEmpty() ? Collections.emptyMap()
                : userService.listByIds(sellerIdSet).stream().collect(Collectors.toMap(User::getId, user -> user));
        Map<Long, Commodity> commodityIdMap = commodityService.listByIds(commodityIdSet).stream()
                .collect(Collectors.toMap(Commodity::getId, commodity -> commodity));

        commodityOrderVOList.forEach(vo -> {
            User buyer = userIdUserMap.get(vo.getUserId());
            if (buyer != null) {
                vo.setUserName(buyer.getUserName());
                vo.setUserPhone(buyer.getUserPhone());
            }
            if (vo.getSellerId() != null) {
                User seller = sellerIdUserMap.get(vo.getSellerId());
                if (seller != null) {
                    vo.setSellerName(seller.getUserName());
                }
            }
            Commodity commodity = commodityIdMap.get(vo.getCommodityId());
            if (commodity != null) {
                vo.setCommodityName(commodity.getCommodityName());
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
        return this.list(getQueryWrapper(queryRequest));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deliverOrder(Long orderId, String deliveryContent, User seller) {
        ThrowUtils.throwIf(orderId == null || orderId <= 0, ErrorCode.PARAMS_ERROR);
        CommodityOrder order = getByIdWithLock(orderId);
        ThrowUtils.throwIf(order == null, ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        ThrowUtils.throwIf(order.getSellerId() == null || !order.getSellerId().equals(seller.getId()),
                ErrorCode.NO_AUTH_ERROR, "无权操作该订单");
        ThrowUtils.throwIf(order.getPayStatus() == null || order.getPayStatus() != 1, ErrorCode.OPERATION_ERROR,
                "订单未支付，无法发货");
        ThrowUtils.throwIf(order.getDeliveryStatus() != null && order.getDeliveryStatus() == 1,
                ErrorCode.OPERATION_ERROR, "订单已发货");
        ThrowUtils.throwIf(StringUtils.isBlank(deliveryContent), ErrorCode.PARAMS_ERROR, "发货内容不能为空");
        order.setDeliveryStatus(1);
        order.setDeliveryContent(deliveryContent);
        order.setDeliverTime(new Date());
        boolean updated = this.updateById(order);
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "更新订单发货信息失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishOrder(Long orderId, String reviewMessage, User buyer) {
        ThrowUtils.throwIf(orderId == null || orderId <= 0, ErrorCode.PARAMS_ERROR);
        CommodityOrder order = getByIdWithLock(orderId);
        ThrowUtils.throwIf(order == null, ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        ThrowUtils.throwIf(order.getUserId() == null || !order.getUserId().equals(buyer.getId()),
                ErrorCode.NO_AUTH_ERROR, "无权确认该订单");
        ThrowUtils.throwIf(order.getPayStatus() == null || order.getPayStatus() != 1, ErrorCode.OPERATION_ERROR,
                "订单未完成支付");
        ThrowUtils.throwIf(order.getDeliveryStatus() == null || order.getDeliveryStatus() != 1,
                ErrorCode.OPERATION_ERROR, "订单未发货");
        if (order.getFinishStatus() != null && order.getFinishStatus() == 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "订单已完成");
        }
        order.setFinishStatus(1);
        order.setFinishTime(new Date());
        if (StringUtils.isNotBlank(reviewMessage)) {
            order.setRemark(reviewMessage);
        }
        boolean updated = this.updateById(order);
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "更新订单状态失败");

        if (order.getSellerId() != null && order.getPaymentAmount() != null) {
            User seller = userService.getByIdWithLock(order.getSellerId());
            ThrowUtils.throwIf(seller == null, ErrorCode.NOT_FOUND_ERROR, "卖家不存在");
            seller.setBalance(seller.getBalance().add(order.getPaymentAmount()));
            boolean sellerUpdated = userService.updateById(seller);
            ThrowUtils.throwIf(!sellerUpdated, ErrorCode.OPERATION_ERROR, "更新卖家余额失败");
        }
    }
}
