package com.xiaobaitiao.springbootinit.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaobaitiao.springbootinit.annotation.AuthCheck;
import com.xiaobaitiao.springbootinit.common.BaseResponse;
import com.xiaobaitiao.springbootinit.common.DeleteRequest;
import com.xiaobaitiao.springbootinit.common.ErrorCode;
import com.xiaobaitiao.springbootinit.common.ResultUtils;
import com.xiaobaitiao.springbootinit.constant.UserConstant;
import com.xiaobaitiao.springbootinit.exception.BusinessException;
import com.xiaobaitiao.springbootinit.exception.ThrowUtils;
import com.xiaobaitiao.springbootinit.model.dto.commodityType.CommodityTypeAddRequest;
import com.xiaobaitiao.springbootinit.model.dto.commodityType.CommodityTypeEditRequest;
import com.xiaobaitiao.springbootinit.model.dto.commodityType.CommodityTypeQueryRequest;
import com.xiaobaitiao.springbootinit.model.dto.commodityType.CommodityTypeUpdateRequest;
import com.xiaobaitiao.springbootinit.model.entity.Commodity;
import com.xiaobaitiao.springbootinit.model.entity.CommodityType;
import com.xiaobaitiao.springbootinit.model.entity.User;
import com.xiaobaitiao.springbootinit.model.vo.CommodityTypeVO;
import com.xiaobaitiao.springbootinit.service.CommodityService;
import com.xiaobaitiao.springbootinit.service.CommodityTypeService;
import com.xiaobaitiao.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 商品类别表接口
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@RestController
@RequestMapping("/commodityType")
@Slf4j
public class CommodityTypeController {

    @Resource
    private CommodityTypeService commodityTypeService;

    @Resource
    private UserService userService;
    @Resource
    private CommodityService commodityService;
    // region 增删改查

    /**
     * 创建商品类别表
     *
     * @param commodityTypeAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addCommodityType(@RequestBody CommodityTypeAddRequest commodityTypeAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(commodityTypeAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        CommodityType commodityType = new CommodityType();
        BeanUtils.copyProperties(commodityTypeAddRequest, commodityType);
        // 数据校验
        commodityTypeService.validCommodityType(commodityType, true);
        // 写入数据库
        boolean result = commodityTypeService.save(commodityType);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newCommodityTypeId = commodityType.getId();
        return ResultUtils.success(newCommodityTypeId);
    }

    /**
     * 删除商品类别表
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteCommodityType(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        // 参数校验
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();

        // 判断商品分类是否存在
        CommodityType oldCommodityType = commodityTypeService.getById(id);
        ThrowUtils.throwIf(oldCommodityType == null, ErrorCode.NOT_FOUND_ERROR);

        // 仅管理员可删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 检查该分类下是否有挂载的商品
        QueryWrapper<Commodity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("commodityTypeId", id);
        long count = commodityService.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该分类下存在商品，无法删除");
        }

        // 执行删除操作
        boolean result = commodityTypeService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新商品类别表（仅管理员可用）
     *
     * @param commodityTypeUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateCommodityType(@RequestBody CommodityTypeUpdateRequest commodityTypeUpdateRequest) {
        if (commodityTypeUpdateRequest == null || commodityTypeUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        CommodityType commodityType = new CommodityType();
        BeanUtils.copyProperties(commodityTypeUpdateRequest, commodityType);
        // 数据校验
        commodityTypeService.validCommodityType(commodityType, false);
        // 判断是否存在
        long id = commodityTypeUpdateRequest.getId();
        CommodityType oldCommodityType = commodityTypeService.getById(id);
        ThrowUtils.throwIf(oldCommodityType == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = commodityTypeService.updateById(commodityType);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取商品类别表（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<CommodityTypeVO> getCommodityTypeVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        CommodityType commodityType = commodityTypeService.getById(id);
        ThrowUtils.throwIf(commodityType == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(commodityTypeService.getCommodityTypeVO(commodityType, request));
    }

    /**
     * 分页获取商品类别表列表（仅管理员可用）
     *
     * @param commodityTypeQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<CommodityType>> listCommodityTypeByPage(@RequestBody CommodityTypeQueryRequest commodityTypeQueryRequest) {
        long current = commodityTypeQueryRequest.getCurrent();
        long size = commodityTypeQueryRequest.getPageSize();
        // 查询数据库
        Page<CommodityType> commodityTypePage = commodityTypeService.page(new Page<>(current, size),
                commodityTypeService.getQueryWrapper(commodityTypeQueryRequest));
        return ResultUtils.success(commodityTypePage);
    }

    /**
     * 分页获取商品类别表列表（封装类）
     *
     * @param commodityTypeQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<CommodityTypeVO>> listCommodityTypeVOByPage(@RequestBody CommodityTypeQueryRequest commodityTypeQueryRequest,
                                                               HttpServletRequest request) {
        long current = commodityTypeQueryRequest.getCurrent();
        long size = commodityTypeQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 1000, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<CommodityType> commodityTypePage = commodityTypeService.page(new Page<>(current, size),
                commodityTypeService.getQueryWrapper(commodityTypeQueryRequest));
        // 获取封装类
        return ResultUtils.success(commodityTypeService.getCommodityTypeVOPage(commodityTypePage, request));
    }

    /**
     * 分页获取当前登录用户创建的商品类别表列表
     *
     * @param commodityTypeQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<CommodityTypeVO>> listMyCommodityTypeVOByPage(@RequestBody CommodityTypeQueryRequest commodityTypeQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(commodityTypeQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = commodityTypeQueryRequest.getCurrent();
        long size = commodityTypeQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<CommodityType> commodityTypePage = commodityTypeService.page(new Page<>(current, size),
                commodityTypeService.getQueryWrapper(commodityTypeQueryRequest));
        // 获取封装类
        return ResultUtils.success(commodityTypeService.getCommodityTypeVOPage(commodityTypePage, request));
    }

    /**
     * 编辑商品类别表（给用户使用）
     *
     * @param commodityTypeEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editCommodityType(@RequestBody CommodityTypeEditRequest commodityTypeEditRequest, HttpServletRequest request) {
        if (commodityTypeEditRequest == null || commodityTypeEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        CommodityType commodityType = new CommodityType();
        BeanUtils.copyProperties(commodityTypeEditRequest, commodityType);
        // 数据校验
        commodityTypeService.validCommodityType(commodityType, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = commodityTypeEditRequest.getId();
        CommodityType oldCommodityType = commodityTypeService.getById(id);
        ThrowUtils.throwIf(oldCommodityType == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅管理员可编辑
        if ( !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = commodityTypeService.updateById(commodityType);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
