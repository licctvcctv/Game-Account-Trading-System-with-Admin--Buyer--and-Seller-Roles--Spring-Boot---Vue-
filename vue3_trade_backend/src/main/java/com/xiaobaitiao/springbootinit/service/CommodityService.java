package com.xiaobaitiao.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaobaitiao.springbootinit.model.dto.commodity.CommodityQueryRequest;
import com.xiaobaitiao.springbootinit.model.entity.Commodity;
import com.xiaobaitiao.springbootinit.model.vo.CommodityVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 商品表服务
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
public interface CommodityService extends IService<Commodity> {

    /**
     * 校验数据
     *
     * @param commodity
     * @param add 对创建的数据进行校验
     */
    void validCommodity(Commodity commodity, boolean add);

    /**
     * 获取查询条件
     *
     * @param commodityQueryRequest
     * @return
     */
    QueryWrapper<Commodity> getQueryWrapper(CommodityQueryRequest commodityQueryRequest);
    
    /**
     * 获取商品表封装
     *
     * @param commodity
     * @param request
     * @return
     */
    CommodityVO getCommodityVO(Commodity commodity, HttpServletRequest request);

    /**
     * 分页获取商品表封装
     *
     * @param commodityPage
     * @param request
     * @return
     */
    Page<CommodityVO> getCommodityVOPage(Page<Commodity> commodityPage, HttpServletRequest request);

    /**
     * 根据 ID 获取商品信息加锁
     * @param id
     * @return
     */
    Commodity getByIdWithLock(Long id);
}
