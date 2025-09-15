package com.xiaobaitiao.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaobaitiao.springbootinit.model.dto.userCommodityFavorites.UserCommodityFavoritesQueryRequest;
import com.xiaobaitiao.springbootinit.model.entity.UserCommodityFavorites;
import com.xiaobaitiao.springbootinit.model.vo.UserCommodityFavoritesVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户商品收藏表服务
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
public interface UserCommodityFavoritesService extends IService<UserCommodityFavorites> {

    /**
     * 校验数据
     *
     * @param userCommodityFavorites
     * @param add 对创建的数据进行校验
     */
    void validUserCommodityFavorites(UserCommodityFavorites userCommodityFavorites, boolean add);

    /**
     * 获取查询条件
     *
     * @param userCommodityFavoritesQueryRequest
     * @return
     */
    QueryWrapper<UserCommodityFavorites> getQueryWrapper(UserCommodityFavoritesQueryRequest userCommodityFavoritesQueryRequest);
    
    /**
     * 获取用户商品收藏表封装
     *
     * @param userCommodityFavorites
     * @param request
     * @return
     */
    UserCommodityFavoritesVO getUserCommodityFavoritesVO(UserCommodityFavorites userCommodityFavorites, HttpServletRequest request);

    /**
     * 分页获取用户商品收藏表封装
     *
     * @param userCommodityFavoritesPage
     * @param request
     * @return
     */
    Page<UserCommodityFavoritesVO> getUserCommodityFavoritesVOPage(Page<UserCommodityFavorites> userCommodityFavoritesPage, HttpServletRequest request);
}
