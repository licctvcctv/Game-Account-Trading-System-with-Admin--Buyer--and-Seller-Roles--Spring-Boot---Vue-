package com.xiaobaitiao.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaobaitiao.springbootinit.common.ErrorCode;
import com.xiaobaitiao.springbootinit.constant.CommonConstant;
import com.xiaobaitiao.springbootinit.exception.ThrowUtils;
import com.xiaobaitiao.springbootinit.mapper.UserCommodityFavoritesMapper;
import com.xiaobaitiao.springbootinit.model.dto.userCommodityFavorites.UserCommodityFavoritesQueryRequest;
import com.xiaobaitiao.springbootinit.model.entity.UserCommodityFavorites;
import com.xiaobaitiao.springbootinit.model.vo.UserCommodityFavoritesVO;
import com.xiaobaitiao.springbootinit.service.UserCommodityFavoritesService;
import com.xiaobaitiao.springbootinit.service.UserService;
import com.xiaobaitiao.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户商品收藏表服务实现
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@Service
@Slf4j
public class UserCommodityFavoritesServiceImpl extends ServiceImpl<UserCommodityFavoritesMapper, UserCommodityFavorites> implements UserCommodityFavoritesService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param userCommodityFavorites
     * @param add                    对创建的数据进行校验
     */
    @Override
    public void validUserCommodityFavorites(UserCommodityFavorites userCommodityFavorites, boolean add) {
        ThrowUtils.throwIf(userCommodityFavorites == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        Long commodityId = userCommodityFavorites.getCommodityId();


        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(commodityId == null || commodityId <= 0, ErrorCode.PARAMS_ERROR);
        }

    }

    /**
     * 获取查询条件
     *
     * @param userCommodityFavoritesQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<UserCommodityFavorites> getQueryWrapper(UserCommodityFavoritesQueryRequest userCommodityFavoritesQueryRequest) {
        QueryWrapper<UserCommodityFavorites> queryWrapper = new QueryWrapper<>();
        if (userCommodityFavoritesQueryRequest == null) {
            return queryWrapper;
        }
        Long id = userCommodityFavoritesQueryRequest.getId();
        Long userId = userCommodityFavoritesQueryRequest.getUserId();
        Long commodityId = userCommodityFavoritesQueryRequest.getCommodityId();
        Integer status = userCommodityFavoritesQueryRequest.getStatus();
        String remark = userCommodityFavoritesQueryRequest.getRemark();
        String sortField = userCommodityFavoritesQueryRequest.getSortField();
        String sortOrder = userCommodityFavoritesQueryRequest.getSortOrder();
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(remark), "remark", remark);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
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
     * 获取用户商品收藏表封装
     *
     * @param userCommodityFavorites
     * @param request
     * @return
     */
    @Override
    public UserCommodityFavoritesVO getUserCommodityFavoritesVO(UserCommodityFavorites userCommodityFavorites, HttpServletRequest request) {
        // 对象转封装类

        return UserCommodityFavoritesVO.objToVo(userCommodityFavorites);
    }

    /**
     * 分页获取用户商品收藏表封装
     *
     * @param userCommodityFavoritesPage
     * @param request
     * @return
     */
    @Override
    public Page<UserCommodityFavoritesVO> getUserCommodityFavoritesVOPage(Page<UserCommodityFavorites> userCommodityFavoritesPage, HttpServletRequest request) {
        List<UserCommodityFavorites> userCommodityFavoritesList = userCommodityFavoritesPage.getRecords();
        Page<UserCommodityFavoritesVO> userCommodityFavoritesVOPage = new Page<>(userCommodityFavoritesPage.getCurrent(), userCommodityFavoritesPage.getSize(), userCommodityFavoritesPage.getTotal());
        if (CollUtil.isEmpty(userCommodityFavoritesList)) {
            return userCommodityFavoritesVOPage;
        }

        // 对象列表 => 封装对象列表
        List<UserCommodityFavoritesVO> userCommodityFavoritesVOList = userCommodityFavoritesList.stream()
                .map(UserCommodityFavoritesVO::objToVo) // 调用 objToVo 方法
                .collect(Collectors.toList());

        userCommodityFavoritesVOPage.setRecords(userCommodityFavoritesVOList);
        return userCommodityFavoritesVOPage;
    }

}
