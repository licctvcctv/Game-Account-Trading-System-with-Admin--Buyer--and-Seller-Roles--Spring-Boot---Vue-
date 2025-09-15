package com.xiaobaitiao.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaobaitiao.springbootinit.annotation.AuthCheck;
import com.xiaobaitiao.springbootinit.common.BaseResponse;
import com.xiaobaitiao.springbootinit.common.DeleteRequest;
import com.xiaobaitiao.springbootinit.common.ErrorCode;
import com.xiaobaitiao.springbootinit.common.ResultUtils;
import com.xiaobaitiao.springbootinit.constant.UserConstant;
import com.xiaobaitiao.springbootinit.exception.BusinessException;
import com.xiaobaitiao.springbootinit.exception.ThrowUtils;
import com.xiaobaitiao.springbootinit.model.dto.commodityOrder.CommodityOrderAddRequest;
import com.xiaobaitiao.springbootinit.model.dto.commodityOrder.CommodityOrderEditRequest;
import com.xiaobaitiao.springbootinit.model.dto.commodityOrder.CommodityOrderQueryRequest;
import com.xiaobaitiao.springbootinit.model.dto.commodityOrder.CommodityOrderUpdateRequest;
import com.xiaobaitiao.springbootinit.model.entity.CommodityOrder;
import com.xiaobaitiao.springbootinit.model.entity.User;
import com.xiaobaitiao.springbootinit.model.vo.CommodityOrderVO;
import com.xiaobaitiao.springbootinit.service.CommodityOrderService;
import com.xiaobaitiao.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品订单表接口
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@RestController
@RequestMapping("/commodityOrder")
@Slf4j
public class CommodityOrderController {

    @Resource
    private CommodityOrderService commodityOrderService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建商品订单表
     *
     * @param commodityOrderAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addCommodityOrder(@RequestBody CommodityOrderAddRequest commodityOrderAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(commodityOrderAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        CommodityOrder commodityOrder = new CommodityOrder();
        BeanUtils.copyProperties(commodityOrderAddRequest, commodityOrder);
        // 数据校验
        commodityOrderService.validCommodityOrder(commodityOrder, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        commodityOrder.setUserId(loginUser.getId());
        // 写入数据库a
        boolean result = commodityOrderService.save(commodityOrder);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newCommodityOrderId = commodityOrder.getId();
        return ResultUtils.success(newCommodityOrderId);
    }

    /**
     * 删除商品订单表
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteCommodityOrder(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        CommodityOrder oldCommodityOrder = commodityOrderService.getById(id);
        ThrowUtils.throwIf(oldCommodityOrder == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldCommodityOrder.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = commodityOrderService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新商品订单表（仅管理员可用）
     *
     * @param commodityOrderUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateCommodityOrder(@RequestBody CommodityOrderUpdateRequest commodityOrderUpdateRequest) {
        if (commodityOrderUpdateRequest == null || commodityOrderUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        CommodityOrder commodityOrder = new CommodityOrder();
        BeanUtils.copyProperties(commodityOrderUpdateRequest, commodityOrder);
        // 数据校验
        commodityOrderService.validCommodityOrder(commodityOrder, false);
        // 判断是否存在
        long id = commodityOrderUpdateRequest.getId();
        CommodityOrder oldCommodityOrder = commodityOrderService.getById(id);
        ThrowUtils.throwIf(oldCommodityOrder == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = commodityOrderService.updateById(commodityOrder);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取商品订单表（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<CommodityOrderVO> getCommodityOrderVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        CommodityOrder commodityOrder = commodityOrderService.getById(id);
        ThrowUtils.throwIf(commodityOrder == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(commodityOrderService.getCommodityOrderVO(commodityOrder, request));
    }

    /**
     * 分页获取商品订单表列表（仅管理员可用）
     *
     * @param commodityOrderQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<CommodityOrder>> listCommodityOrderByPage(@RequestBody CommodityOrderQueryRequest commodityOrderQueryRequest) {
        long current = commodityOrderQueryRequest.getCurrent();
        long size = commodityOrderQueryRequest.getPageSize();
        // 查询数据库
        Page<CommodityOrder> commodityOrderPage = commodityOrderService.page(new Page<>(current, size),
                commodityOrderService.getQueryWrapper(commodityOrderQueryRequest));
        return ResultUtils.success(commodityOrderPage);
    }

    /**
     * 分页获取商品订单表列表（封装类）
     *
     * @param commodityOrderQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<CommodityOrderVO>> listCommodityOrderVOByPage(@RequestBody CommodityOrderQueryRequest commodityOrderQueryRequest,
                                                               HttpServletRequest request) {
        long current = commodityOrderQueryRequest.getCurrent();
        long size = commodityOrderQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<CommodityOrder> commodityOrderPage = commodityOrderService.page(new Page<>(current, size),
                commodityOrderService.getQueryWrapper(commodityOrderQueryRequest));
        // 获取封装类
        return ResultUtils.success(commodityOrderService.getCommodityOrderVOPage(commodityOrderPage, request));
    }

    /**
     * 分页获取当前登录用户创建的商品订单表列表
     *
     * @param commodityOrderQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<CommodityOrderVO>> listMyCommodityOrderVOByPage(@RequestBody CommodityOrderQueryRequest commodityOrderQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(commodityOrderQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        commodityOrderQueryRequest.setUserId(loginUser.getId());
        long current = commodityOrderQueryRequest.getCurrent();
        long size = commodityOrderQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<CommodityOrder> commodityOrderPage = commodityOrderService.page(new Page<>(current, size),
                commodityOrderService.getQueryWrapper(commodityOrderQueryRequest));
        // 获取封装类
        return ResultUtils.success(commodityOrderService.getCommodityOrderVOPage(commodityOrderPage, request));
    }

    /**
     * 编辑商品订单表（给用户使用）
     *
     * @param commodityOrderEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editCommodityOrder(@RequestBody CommodityOrderEditRequest commodityOrderEditRequest, HttpServletRequest request) {
        if (commodityOrderEditRequest == null || commodityOrderEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        CommodityOrder commodityOrder = new CommodityOrder();
        BeanUtils.copyProperties(commodityOrderEditRequest, commodityOrder);
        // 数据校验
        commodityOrderService.validCommodityOrder(commodityOrder, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = commodityOrderEditRequest.getId();
        CommodityOrder oldCommodityOrder = commodityOrderService.getById(id);
        ThrowUtils.throwIf(oldCommodityOrder == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldCommodityOrder.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = commodityOrderService.updateById(commodityOrder);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
    /**
     * 根据 userId 和 payStatus 查询商品订单，返回日期和订单数量的列表
     *
     * @param userId    用户ID
     * @param payStatus 支付状态
     * @return 返回日期和订单数量的列表，用于 ECharts 热力图
     */
    @GetMapping("/getCommodityOrderHeatmapData")
    public BaseResponse<List<Map<String, Object>>> getCommodityOrderHeatmapData(
            @RequestParam Long userId,
            @RequestParam Integer payStatus) {
        // 构建查询条件
        CommodityOrderQueryRequest queryRequest = new CommodityOrderQueryRequest();
        queryRequest.setUserId(userId);
        queryRequest.setPayStatus(payStatus);

        // 查询符合条件的订单
        List<CommodityOrder> orderList = commodityOrderService.listByQuery(queryRequest);

        // 处理查询结果，生成日期和订单数量的列表
        List<Map<String, Object>> result = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // 统计每个日期的订单数量
        Map<String, Integer> dateCountMap = new HashMap<>();
        for (CommodityOrder order : orderList) {
            String dateStr = dateFormat.format(order.getCreateTime());
            dateCountMap.put(dateStr, dateCountMap.getOrDefault(dateStr, 0) + 1);
        }

        // 将统计结果转换为前端需要的格式
        for (Map.Entry<String, Integer> entry : dateCountMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", entry.getKey());
            item.put("value", entry.getValue());
            result.add(item);
        }

        return ResultUtils.success(result);
    }
    // endregion
}
