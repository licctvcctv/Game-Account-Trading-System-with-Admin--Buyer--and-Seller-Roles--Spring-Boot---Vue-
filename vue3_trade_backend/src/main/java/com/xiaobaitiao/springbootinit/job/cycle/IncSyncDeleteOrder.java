package com.xiaobaitiao.springbootinit.job.cycle;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaobaitiao.springbootinit.model.entity.CommodityOrder;
import com.xiaobaitiao.springbootinit.service.CommodityOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 程序员小白条
 * @date 2025/3/12 15:32
 * @gitee https://gitee.com/falle22222n-leaves/vue_-book-manage-system
 */
@Component
@Slf4j
public class IncSyncDeleteOrder {
    @Resource
    private CommodityOrderService commodityOrderService;

    @Scheduled(fixedRate = 60 * 1000) // 每分钟执行一次
    public void checkExpiredOrders() {
        log.info("===============订单过期定时任务查询开始===============");
        // 1. 构建查询条件
        LambdaQueryWrapper<CommodityOrder> queryWrapper = new LambdaQueryWrapper<CommodityOrder>()
                .eq(CommodityOrder::getPayStatus, 0)  // 未支付订单
                .lt(CommodityOrder::getCreateTime, LocalDateTime.now().minusMinutes(15));  // 创建时间超过15分钟

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
                log.error("执行批量删除订单任务失败"+LocalDateTime.now());
            }

            log.info("已处理过期订单数量：{}", orders.size());
        }
        log.info("===============订单过期定时任务查询结束===============");
    }
}
