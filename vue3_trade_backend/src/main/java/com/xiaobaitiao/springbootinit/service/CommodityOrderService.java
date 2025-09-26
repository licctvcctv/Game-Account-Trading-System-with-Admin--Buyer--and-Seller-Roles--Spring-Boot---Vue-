package com.xiaobaitiao.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaobaitiao.springbootinit.model.dto.commodityOrder.CommodityOrderQueryRequest;
import com.xiaobaitiao.springbootinit.model.entity.CommodityOrder;
import com.xiaobaitiao.springbootinit.model.entity.User;
import com.xiaobaitiao.springbootinit.model.vo.CommodityOrderVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 商品订单表服务
 *
 * 
 * 
 */
public interface CommodityOrderService extends IService<CommodityOrder> {

    /**
     * 校验数据
     *
     * @param commodityOrder
     * @param add 对创建的数据进行校验
     */
    void validCommodityOrder(CommodityOrder commodityOrder, boolean add);

    /**
     * 获取查询条件
     *
     * @param commodityOrderQueryRequest
     * @return
     */
    QueryWrapper<CommodityOrder> getQueryWrapper(CommodityOrderQueryRequest commodityOrderQueryRequest);
    
    /**
     * 获取商品订单表封装
     *
     * @param commodityOrder
     * @param request
     * @return
     */
    CommodityOrderVO getCommodityOrderVO(CommodityOrder commodityOrder, HttpServletRequest request);

    /**
     * 分页获取商品订单表封装
     *
     * @param commodityOrderPage
     * @param request
     * @return
     */
    Page<CommodityOrderVO> getCommodityOrderVOPage(Page<CommodityOrder> commodityOrderPage, HttpServletRequest request);

    /**
     * 查询商品订单加锁
     * @param id
     * @return
     */
    CommodityOrder getByIdWithLock(Long id);

    List<CommodityOrder> listByQuery(CommodityOrderQueryRequest queryRequest);

    void deliverOrder(Long orderId, String deliveryContent, User seller);

    void finishOrder(Long orderId, String reviewMessage, User buyer);
}

