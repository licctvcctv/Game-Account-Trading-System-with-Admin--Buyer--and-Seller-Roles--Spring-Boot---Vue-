package com.xiaobaitiao.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaobaitiao.springbootinit.model.dto.commodityScore.CommodityScoreQueryRequest;
import com.xiaobaitiao.springbootinit.model.entity.CommodityScore;
import com.xiaobaitiao.springbootinit.model.vo.CommodityScoreVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 商品评分表服务
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
public interface CommodityScoreService extends IService<CommodityScore> {

    /**
     * 校验数据
     *
     * @param commodityScore
     * @param add 对创建的数据进行校验
     */
    void validCommodityScore(CommodityScore commodityScore, boolean add);

    /**
     * 获取查询条件
     *
     * @param commodityScoreQueryRequest
     * @return
     */
    QueryWrapper<CommodityScore> getQueryWrapper(CommodityScoreQueryRequest commodityScoreQueryRequest);
    
    /**
     * 获取商品评分表封装
     *
     * @param commodityScore
     * @param request
     * @return
     */
    CommodityScoreVO getCommodityScoreVO(CommodityScore commodityScore, HttpServletRequest request);

    /**
     * 分页获取商品评分表封装
     *
     * @param commodityScorePage
     * @param request
     * @return
     */
    Page<CommodityScoreVO> getCommodityScoreVOPage(Page<CommodityScore> commodityScorePage, HttpServletRequest request);
    /**
     * 获取商品的平均评分
     *
     * @param spotId 商品 ID
     * @return 平均评分
     */
    Double getAverageScoreBySpotId(Long spotId);
}
