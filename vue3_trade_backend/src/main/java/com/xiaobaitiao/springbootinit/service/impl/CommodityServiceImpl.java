package com.xiaobaitiao.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaobaitiao.springbootinit.common.ErrorCode;
import com.xiaobaitiao.springbootinit.constant.CommonConstant;
import com.xiaobaitiao.springbootinit.exception.ThrowUtils;
import com.xiaobaitiao.springbootinit.mapper.CommodityMapper;
import com.xiaobaitiao.springbootinit.model.dto.commodity.CommodityQueryRequest;
import com.xiaobaitiao.springbootinit.model.entity.Commodity;
import com.xiaobaitiao.springbootinit.model.entity.CommodityType;
import com.xiaobaitiao.springbootinit.model.entity.User;
import com.xiaobaitiao.springbootinit.model.vo.CommodityVO;
import com.xiaobaitiao.springbootinit.model.vo.UserVO;
import com.xiaobaitiao.springbootinit.service.CommodityService;
import com.xiaobaitiao.springbootinit.service.CommodityTypeService;
import com.xiaobaitiao.springbootinit.service.UserService;
import com.xiaobaitiao.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商品表服务实现
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@Service
@Slf4j
public class CommodityServiceImpl extends ServiceImpl<CommodityMapper, Commodity> implements CommodityService {

    @Resource
    private UserService userService;
    @Resource
    private CommodityTypeService commodityTypeService;
    /**
     * 校验数据
     *
     * @param commodity
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validCommodity(Commodity commodity, boolean add) {
        ThrowUtils.throwIf(commodity == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String commodityName = commodity.getCommodityName();
        Integer commodityInventory = commodity.getCommodityInventory();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(commodityName), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(commodityInventory == null || commodityInventory <= 0, ErrorCode.PARAMS_ERROR);
        }
    }

    /**
     * 获取查询条件
     *
     * @param commodityQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Commodity> getQueryWrapper(CommodityQueryRequest commodityQueryRequest) {
        QueryWrapper<Commodity> queryWrapper = new QueryWrapper<>();
        if (commodityQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = commodityQueryRequest.getId();
        String commodityName = commodityQueryRequest.getCommodityName();
        String commodityDescription = commodityQueryRequest.getCommodityDescription();
        String degree = commodityQueryRequest.getDegree();
        Long commodityTypeId = commodityQueryRequest.getCommodityTypeId();
        Long adminId = commodityQueryRequest.getAdminId();
        Integer isListed = commodityQueryRequest.getIsListed();
        Integer commodityInventory = commodityQueryRequest.getCommodityInventory();
        String sortField = commodityQueryRequest.getSortField();
        String sortOrder = commodityQueryRequest.getSortOrder();
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(commodityName), "commodityName", commodityName);
        queryWrapper.like(StringUtils.isNotBlank(commodityDescription), "commodityDescription", commodityDescription);
        queryWrapper.like(StringUtils.isNotBlank(degree), "degree", degree);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(commodityTypeId), "commodityTypeId", commodityTypeId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(adminId), "adminId", adminId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(commodityInventory), "commodityInventory", commodityInventory);
        queryWrapper.eq(ObjectUtils.isNotEmpty(isListed), "isListed", isListed);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取商品表封装
     *
     * @param commodity
     * @param request
     * @return
     */
    /**
     * 获取商品表封装
     *
     * @param commodity
     * @param request
     * @return
     */
    @Override
    public CommodityVO getCommodityVO(Commodity commodity, HttpServletRequest request) {
        // 对象转封装类
        CommodityVO commodityVO = CommodityVO.objToVo(commodity);

        // 填充 commodityTypeName
        if (commodity.getCommodityTypeId() != null) {
            CommodityType commodityType = commodityTypeService.getById(commodity.getCommodityTypeId());
            if (commodityType != null) {
                commodityVO.setCommodityTypeName(commodityType.getTypeName());
            }
        }

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long adminId = commodity.getAdminId();
        User admin = null;
        if (adminId != null && adminId > 0) {
            admin = userService.getById(adminId);
        }
        if (admin != null) {
            commodityVO.setAdminName(admin.getUserName());
        }
        // endregion

        return commodityVO;
    }

    /**
     * 分页获取商品表封装
     *
     * @param commodityPage
     * @param request
     * @return
     */
    @Override
    public Page<CommodityVO> getCommodityVOPage(Page<Commodity> commodityPage, HttpServletRequest request) {
        List<Commodity> commodityList = commodityPage.getRecords();
        Page<CommodityVO> commodityVOPage = new Page<>(commodityPage.getCurrent(), commodityPage.getSize(), commodityPage.getTotal());
        if (CollUtil.isEmpty(commodityList)) {
            return commodityVOPage;
        }

        // 收集所有 commodityTypeId
        Set<Long> commodityTypeIds = commodityList.stream()
                .map(Commodity::getCommodityTypeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 批量查询 CommodityType
        Map<Long, CommodityType> commodityTypeMap = new HashMap<>();
        if (CollUtil.isNotEmpty(commodityTypeIds)) {
            List<CommodityType> commodityTypes = commodityTypeService.listByIds(commodityTypeIds);
            commodityTypeMap = commodityTypes.stream()
                    .collect(Collectors.toMap(CommodityType::getId, commodityType->commodityType));
        }

        // 对象列表 => 封装对象列表
        Map<Long, CommodityType> finalCommodityTypeMap = commodityTypeMap;
        List<CommodityVO> commodityVOList = commodityList.stream().map(commodity -> {
            CommodityVO commodityVO = CommodityVO.objToVo(commodity);
            // 从 Map 中获取 commodityTypeName
            if (commodity.getCommodityTypeId() != null) {
                CommodityType commodityType = finalCommodityTypeMap.get(commodity.getCommodityTypeId());
                if (commodityType != null) {
                    commodityVO.setCommodityTypeName(commodityType.getTypeName());
                }
            }
            return commodityVO;
        }).collect(Collectors.toList());

        commodityVOPage.setRecords(commodityVOList);
        return commodityVOPage;
    }

    @Override
    public Commodity getByIdWithLock(Long id) {
        return baseMapper.selectByIdWithLock(id);
    }

}
