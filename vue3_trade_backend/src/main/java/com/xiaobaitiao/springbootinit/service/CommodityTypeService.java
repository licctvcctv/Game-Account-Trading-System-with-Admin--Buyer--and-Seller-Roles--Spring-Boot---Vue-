package com.xiaobaitiao.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaobaitiao.springbootinit.model.dto.commodityType.CommodityTypeQueryRequest;
import com.xiaobaitiao.springbootinit.model.entity.CommodityType;
import com.xiaobaitiao.springbootinit.model.vo.CommodityTypeVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 商品类别表服务
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
public interface CommodityTypeService extends IService<CommodityType> {

    /**
     * 校验数据
     *
     * @param commodityType
     * @param add 对创建的数据进行校验
     */
    void validCommodityType(CommodityType commodityType, boolean add);

    /**
     * 获取查询条件
     *
     * @param commodityTypeQueryRequest
     * @return
     */
    QueryWrapper<CommodityType> getQueryWrapper(CommodityTypeQueryRequest commodityTypeQueryRequest);
    
    /**
     * 获取商品类别表封装
     *
     * @param commodityType
     * @param request
     * @return
     */
    CommodityTypeVO getCommodityTypeVO(CommodityType commodityType, HttpServletRequest request);

    /**
     * 分页获取商品类别表封装
     *
     * @param commodityTypePage
     * @param request
     * @return
     */
    Page<CommodityTypeVO> getCommodityTypeVOPage(Page<CommodityType> commodityTypePage, HttpServletRequest request);
}
