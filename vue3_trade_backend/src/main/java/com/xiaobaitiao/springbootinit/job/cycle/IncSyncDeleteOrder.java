package com.xiaobaitiao.springbootinit.job.cycle;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaobaitiao.springbootinit.model.entity.Commodity;
import com.xiaobaitiao.springbootinit.model.entity.CommodityOrder;
import com.xiaobaitiao.springbootinit.service.CommodityOrderService;
import com.xiaobaitiao.springbootinit.service.CommodityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @date 2025/3/12 15:32
 * @gitee https://gitee.com/falle22222n-leaves/vue_-book-manage-system
 */
@Component
@Slf4j
public class IncSyncDeleteOrder {
    @Resource
    private CommodityOrderService commodityOrderService;
    @Resource
    private CommodityService commodityService;

    @Scheduled(fixedRate = 60 * 1000) // 每分钟执行一次
    public void checkExpiredOrders() {
        log.info("===============订单过期定时任务查询开始===============");
        // 1. 构建查询条件
        Date unpaidCutoff = new Date(System.currentTimeMillis() - 15 * 60 * 1000L);
        LambdaQueryWrapper<CommodityOrder> queryWrapper = new LambdaQueryWrapper<CommodityOrder>()
                .eq(CommodityOrder::getPayStatus, 0)  // 未支付订单
                .lt(CommodityOrder::getCreateTime, unpaidCutoff);  // 创建时间超过15分钟

        // 2. 查询符合条件的订单
        List<CommodityOrder> orders = commodityOrderService.list(queryWrapper);
        // 3.获取所有 ID
        if (!orders.isEmpty()) {
            List<Long> orderIds = orders.stream()
                    .map(CommodityOrder::getId)
                    .collect(Collectors.toList());
            // 4. 执行批量删除
            boolean remove = commodityOrderService.removeBatchByIds(orderIds);
            if(!remove){
                log.error("执行批量删除订单任务失败"+ LocalDateTime.now());
            }

            log.info("已处理过期订单数量：{}", orders.size());
        }
        // 释放已结束的租用账号
        LambdaQueryWrapper<CommodityOrder> rentalWrapper = new LambdaQueryWrapper<CommodityOrder>()
                .eq(CommodityOrder::getTradeType, 2)
                .eq(CommodityOrder::getPayStatus, 1)
                .isNotNull(CommodityOrder::getRentEndTime)
                .lt(CommodityOrder::getRentEndTime, new Date());
        List<CommodityOrder> finishedRentals = commodityOrderService.list(rentalWrapper);
        if (!finishedRentals.isEmpty()) {
            finishedRentals.forEach(order -> {
                Commodity commodity = commodityService.getById(order.getCommodityId());
                if (commodity == null) {
                    return;
                }
                Date current = new Date();
                if (commodity.getRentStatus() != null && commodity.getRentStatus() == 1) {
                    Date rentEnd = commodity.getRentEndTime();
                    if (rentEnd == null || !rentEnd.after(current)) {
                        commodity.setRentStatus(0);
                        commodity.setRentStartTime(null);
                        commodity.setRentEndTime(null);
                        commodityService.updateById(commodity);
                    }
                }
            });
        }

        log.info("===============订单过期定时任务查询结束===============");
    }
}
