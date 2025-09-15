package com.xiaobaitiao.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaobaitiao.springbootinit.common.ErrorCode;
import com.xiaobaitiao.springbootinit.constant.CommonConstant;
import com.xiaobaitiao.springbootinit.exception.ThrowUtils;
import com.xiaobaitiao.springbootinit.mapper.CommodityScoreMapper;
import com.xiaobaitiao.springbootinit.model.dto.commodityScore.CommodityScoreQueryRequest;
import com.xiaobaitiao.springbootinit.model.entity.CommodityScore;
import com.xiaobaitiao.springbootinit.model.entity.User;
import com.xiaobaitiao.springbootinit.model.vo.CommodityScoreVO;
import com.xiaobaitiao.springbootinit.service.CommodityScoreService;
import com.xiaobaitiao.springbootinit.service.UserService;
import com.xiaobaitiao.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品评分表服务实现
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@Service
@Slf4j
public class CommodityScoreServiceImpl extends ServiceImpl<CommodityScoreMapper, CommodityScore> implements CommodityScoreService {

    @Resource
    private UserService userService;
    @Resource
    private CommodityScoreMapper commodityScoreMapper;
    /**
     * 校验数据
     *
     * @param commodityScore
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validCommodityScore(CommodityScore commodityScore, boolean add) {
        ThrowUtils.throwIf(commodityScore == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        Long commodityId = commodityScore.getCommodityId();
        Integer score = commodityScore.getScore();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(commodityId == null || commodityId <=0 , ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(score == null || score <0 , ErrorCode.PARAMS_ERROR);
        }

    }

    /**
     * 获取查询条件
     *
     * @param commodityScoreQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<CommodityScore> getQueryWrapper(CommodityScoreQueryRequest commodityScoreQueryRequest) {
        QueryWrapper<CommodityScore> queryWrapper = new QueryWrapper<>();
        if (commodityScoreQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = commodityScoreQueryRequest.getId();
        Long commodityId = commodityScoreQueryRequest.getCommodityId();
        Long userId = commodityScoreQueryRequest.getUserId();
        Integer score = commodityScoreQueryRequest.getScore();
        String sortField = commodityScoreQueryRequest.getSortField();
        String sortOrder = commodityScoreQueryRequest.getSortOrder();
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(commodityId), "commodityId", commodityId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(score), "score", score);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取商品评分表封装
     *
     * @param commodityScore
     * @param request
     * @return
     */
    @Override
    public CommodityScoreVO getCommodityScoreVO(CommodityScore commodityScore, HttpServletRequest request) {
        // 对象转封装类
        return CommodityScoreVO.objToVo(commodityScore);
    }

    /**
     * 分页获取商品评分表封装
     *
     * @param commodityScorePage
     * @param request
     * @return
     */
    @Override
    public Page<CommodityScoreVO> getCommodityScoreVOPage(Page<CommodityScore> commodityScorePage, HttpServletRequest request) {
        List<CommodityScore> commodityScoreList = commodityScorePage.getRecords();
        Page<CommodityScoreVO> commodityScoreVOPage = new Page<>(commodityScorePage.getCurrent(), commodityScorePage.getSize(), commodityScorePage.getTotal());
        if (CollUtil.isEmpty(commodityScoreList)) {
            return commodityScoreVOPage;
        }
        // 对象列表 => 封装对象列表
        List<CommodityScoreVO> commodityScoreVOList = commodityScoreList.stream().map(CommodityScoreVO::objToVo).collect(Collectors.toList());
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = commodityScoreVOList.stream().map(CommodityScoreVO::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        commodityScoreVOList.forEach(commodityScoreVO -> {
            Long userId = commodityScoreVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            commodityScoreVO.setUserVO(userService.getUserVO(user));
        });
        // endregion
        commodityScoreVOPage.setRecords(commodityScoreVOList);
        return commodityScoreVOPage;
    }

    @Override
    public Double getAverageScoreBySpotId(Long commodityId) {
        return commodityScoreMapper.getAverageScoreByCommodityId(commodityId);
    }

}
