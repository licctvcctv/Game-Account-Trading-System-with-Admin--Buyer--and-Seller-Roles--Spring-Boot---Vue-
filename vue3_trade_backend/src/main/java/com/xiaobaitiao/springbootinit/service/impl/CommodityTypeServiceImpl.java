package com.xiaobaitiao.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaobaitiao.springbootinit.common.ErrorCode;
import com.xiaobaitiao.springbootinit.constant.CommonConstant;
import com.xiaobaitiao.springbootinit.exception.ThrowUtils;
import com.xiaobaitiao.springbootinit.mapper.CommodityTypeMapper;
import com.xiaobaitiao.springbootinit.model.dto.commodityType.CommodityTypeQueryRequest;
import com.xiaobaitiao.springbootinit.model.entity.CommodityType;
import com.xiaobaitiao.springbootinit.model.entity.User;
import com.xiaobaitiao.springbootinit.model.vo.CommodityTypeVO;
import com.xiaobaitiao.springbootinit.model.vo.UserVO;
import com.xiaobaitiao.springbootinit.service.CommodityTypeService;
import com.xiaobaitiao.springbootinit.service.UserService;
import com.xiaobaitiao.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品类别表服务实现
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@Service
@Slf4j
public class CommodityTypeServiceImpl extends ServiceImpl<CommodityTypeMapper, CommodityType> implements CommodityTypeService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param commodityType
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validCommodityType(CommodityType commodityType, boolean add) {
        ThrowUtils.throwIf(commodityType == null, ErrorCode.PARAMS_ERROR);
        String typeName = commodityType.getTypeName();


        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(typeName), ErrorCode.PARAMS_ERROR);
        }

    }

    /**
     * 获取查询条件
     *
     * @param commodityTypeQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<CommodityType> getQueryWrapper(CommodityTypeQueryRequest commodityTypeQueryRequest) {
        QueryWrapper<CommodityType> queryWrapper = new QueryWrapper<>();
        if (commodityTypeQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = commodityTypeQueryRequest.getId();
        String typeName = commodityTypeQueryRequest.getTypeName();
        String sortField = commodityTypeQueryRequest.getSortField();
        String sortOrder = commodityTypeQueryRequest.getSortOrder();
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(typeName), "typeName", typeName);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取商品类别表封装
     *
     * @param commodityType
     * @param request
     * @return
     */
    @Override
    public CommodityTypeVO getCommodityTypeVO(CommodityType commodityType, HttpServletRequest request) {
        // 对象转封装类
        return CommodityTypeVO.objToVo(commodityType);
    }

    /**
     * 分页获取商品类别表封装
     *
     * @param commodityTypePage
     * @param request
     * @return
     */
    @Override
    public Page<CommodityTypeVO> getCommodityTypeVOPage(Page<CommodityType> commodityTypePage, HttpServletRequest request) {
        List<CommodityType> commodityTypeList = commodityTypePage.getRecords();
        Page<CommodityTypeVO> commodityTypeVOPage = new Page<>(commodityTypePage.getCurrent(), commodityTypePage.getSize(), commodityTypePage.getTotal());
        if (CollUtil.isEmpty(commodityTypeList)) {
            return commodityTypeVOPage;
        }
        // 对象列表 => 封装对象列表
        List<CommodityTypeVO> commodityTypeVOList = commodityTypeList.stream().map(CommodityTypeVO::objToVo).collect(Collectors.toList());
        commodityTypeVOPage.setRecords(commodityTypeVOList);
        return commodityTypeVOPage;
    }

}
