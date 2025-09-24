package com.xiaobaitiao.springbootinit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaobaitiao.springbootinit.annotation.AuthCheck;
import com.xiaobaitiao.springbootinit.common.BaseResponse;
import com.xiaobaitiao.springbootinit.common.DeleteRequest;
import com.xiaobaitiao.springbootinit.common.ErrorCode;
import com.xiaobaitiao.springbootinit.common.ResultUtils;
import com.xiaobaitiao.springbootinit.constant.UserConstant;
import com.xiaobaitiao.springbootinit.exception.BusinessException;
import com.xiaobaitiao.springbootinit.exception.ThrowUtils;
import com.xiaobaitiao.springbootinit.model.dto.commodity.*;
import com.xiaobaitiao.springbootinit.model.dto.commodityOrder.CommodityOrderQueryRequest;
import com.xiaobaitiao.springbootinit.model.dto.commodityOrder.PayCommodityOrderRequest;
import com.xiaobaitiao.springbootinit.model.entity.Commodity;
import com.xiaobaitiao.springbootinit.model.entity.CommodityOrder;
import com.xiaobaitiao.springbootinit.model.entity.CommodityScore;
import com.xiaobaitiao.springbootinit.model.entity.User;
import com.xiaobaitiao.springbootinit.model.vo.CommodityVO;
import com.xiaobaitiao.springbootinit.service.CommodityOrderService;
import com.xiaobaitiao.springbootinit.service.CommodityScoreService;
import com.xiaobaitiao.springbootinit.service.CommodityService;
import com.xiaobaitiao.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品表接口
 *
 * 
 * 
 */
@RestController
@RequestMapping("/commodity")
@Slf4j
public class CommodityController {

    @Resource
    private CommodityService commodityService;
    @Resource
    private CommodityScoreService commodityScoreService;
    @Resource
    private UserService userService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private CommodityOrderService commodityOrderService;
    // region 增删改查

    /**
     * 创建商品表
     *
     * @param commodityAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addCommodity(@RequestBody CommodityAddRequest commodityAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(commodityAddRequest == null, ErrorCode.PARAMS_ERROR);
        Commodity commodity = new Commodity();
        BeanUtils.copyProperties(commodityAddRequest, commodity);
        // 数据校验
        commodityService.validCommodity(commodity, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        if (commodity.getTradeType() == null) {
            commodity.setTradeType(1);
        }
        if (commodity.getTradeType() == 2) {
            commodity.setRentStatus(0);
            commodity.setRentStartTime(null);
            commodity.setRentEndTime(null);
            commodity.setCommodityInventory(0);
        } else if (commodity.getCommodityInventory() == null || commodity.getCommodityInventory() <= 0) {
            commodity.setCommodityInventory(1);
        }
        checkSellerPermission(loginUser, commodity.getTradeType());
        commodity.setAdminId(loginUser.getId());
        // 写入数据库
        boolean result = commodityService.save(commodity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newCommodityId = commodity.getId();
        return ResultUtils.success(newCommodityId);
    }

    /**
     * 删除商品表
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteCommodity(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        Commodity oldCommodity = commodityService.getById(id);
        ThrowUtils.throwIf(oldCommodity == null, ErrorCode.NOT_FOUND_ERROR);
        // 管理员或发布者可删除
        boolean isAdmin = userService.isAdmin(request);
        User loginUser = null;
        try {
            loginUser = userService.getLoginUser(request);
        } catch (Exception ignored) {}
        boolean isOwner = loginUser != null && oldCommodity.getAdminId() != null && oldCommodity.getAdminId().equals(loginUser.getId());
        if (!isAdmin && !isOwner) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = commodityService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新商品表（仅管理员可用）
     *
     * @param commodityUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateCommodity(@RequestBody CommodityUpdateRequest commodityUpdateRequest) {
        if (commodityUpdateRequest == null || commodityUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Commodity commodity = new Commodity();
        BeanUtils.copyProperties(commodityUpdateRequest, commodity);
        // 数据校验
        commodityService.validCommodity(commodity, false);
        // 判断是否存在
        long id = commodityUpdateRequest.getId();
        Commodity oldCommodity = commodityService.getById(id);
        ThrowUtils.throwIf(oldCommodity == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = commodityService.updateById(commodity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取商品表（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<CommodityVO> getCommodityVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Commodity commodity = commodityService.getById(id);
        ThrowUtils.throwIf(commodity == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(commodityService.getCommodityVO(commodity, request));
    }

    /**
     * 分页获取商品表列表（仅管理员可用）
     *
     * @param commodityQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Commodity>> listCommodityByPage(@RequestBody CommodityQueryRequest commodityQueryRequest) {
        long current = commodityQueryRequest.getCurrent();
        long size = commodityQueryRequest.getPageSize();
        // 查询数据库
        Page<Commodity> commodityPage = commodityService.page(new Page<>(current, size),
                commodityService.getQueryWrapper(commodityQueryRequest));
        return ResultUtils.success(commodityPage);
    }

    /**
     * 分页获取商品表列表（封装类）
     *
     * @param commodityQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<CommodityVO>> listCommodityVOByPage(@RequestBody CommodityQueryRequest commodityQueryRequest,
                                                               HttpServletRequest request) {
        long current = commodityQueryRequest.getCurrent();
        long size = commodityQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Commodity> commodityPage = commodityService.page(new Page<>(current, size),
                commodityService.getQueryWrapper(commodityQueryRequest));
        // 获取封装类
        return ResultUtils.success(commodityService.getCommodityVOPage(commodityPage, request));
    }

    /**
     * 分页获取当前登录用户创建的商品表列表
     *
     * @param commodityQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<CommodityVO>> listMyCommodityVOByPage(@RequestBody CommodityQueryRequest commodityQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(commodityQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        commodityQueryRequest.setAdminId(loginUser.getId());
        long current = commodityQueryRequest.getCurrent();
        long size = commodityQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Commodity> commodityPage = commodityService.page(new Page<>(current, size),
                commodityService.getQueryWrapper(commodityQueryRequest));
        // 获取封装类
        return ResultUtils.success(commodityService.getCommodityVOPage(commodityPage, request));
    }

    /**
     * 编辑商品表（给用户使用）
     *
     * @param commodityEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editCommodity(@RequestBody CommodityEditRequest commodityEditRequest, HttpServletRequest request) {
        if (commodityEditRequest == null || commodityEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Commodity commodity = new Commodity();
        BeanUtils.copyProperties(commodityEditRequest, commodity);
        // 数据校验
        commodityService.validCommodity(commodity, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = commodityEditRequest.getId();
        Commodity oldCommodity = commodityService.getById(id);
        ThrowUtils.throwIf(oldCommodity == null, ErrorCode.NOT_FOUND_ERROR);
        // 判断是否需要跳过权限检查(更新浏览量或者收藏量，无需权限检查）
        boolean isSkipPermissionCheck = (commodityEditRequest.getFavourNum() != null && commodityEditRequest.getFavourNum() >= 0)
                || (commodityEditRequest.getViewNum() != null && commodityEditRequest.getViewNum() >= 0);

// 如果不需要跳过权限检查，则进行权限验证
        if (!isSkipPermissionCheck) {
            // 管理员或发布者可编辑
            boolean isAdmin = userService.isAdmin(loginUser);
            boolean isOwner = oldCommodity.getAdminId() != null && oldCommodity.getAdminId().equals(loginUser.getId());
            if (!isAdmin && !isOwner) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            if (!isAdmin) {
                Integer tradeType = commodity.getTradeType() != null ? commodity.getTradeType()
                        : (oldCommodity.getTradeType() == null ? 1 : oldCommodity.getTradeType());
                checkSellerPermission(loginUser, tradeType);
            }
        }

        // 操作数据库
        boolean result = commodityService.updateById(commodity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
    // 购买接口（根据余额情况自动支付或创建未支付订单）
    @PostMapping("/buy")
    public synchronized BaseResponse<Map<String, Object>> buyCommodity(@RequestBody BuyCommodityRequest buyRequest, HttpServletRequest request) {
        if (buyRequest == null || buyRequest.getCommodityId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }

        User loginUser = userService.getLoginUser(request);

        Commodity commodity = commodityService.getByIdWithLock(buyRequest.getCommodityId());
        if (commodity == null || commodity.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商品不存在");
        }
        if (commodity.getIsListed() != 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "商品未上架");
        }

        Integer tradeType = commodity.getTradeType() == null ? 1 : commodity.getTradeType();
        int purchaseNumber = Math.max(1, buyRequest.getBuyNumber() == null ? 1 : buyRequest.getBuyNumber());
        int rentalDuration = 0;
        String rentalUnit = null;
        if (tradeType == 1 && purchaseNumber <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "购买数量无效");
        }
        if (tradeType == 1) {
            Integer stock = commodity.getCommodityInventory();
            if (stock == null || stock < purchaseNumber) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "库存不足");
            }
        } else {
            rentalDuration = buyRequest.getRentalDuration() == null ? 0 : buyRequest.getRentalDuration();
            if (rentalDuration <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "请填写租用时长");
            }
            rentalUnit = org.apache.commons.lang3.StringUtils.defaultIfBlank(buyRequest.getRentalUnit(), "HOUR").toUpperCase();
            if (!"HOUR".equals(rentalUnit) && !"DAY".equals(rentalUnit)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的租用单位");
            }
            if (commodity.getRentStatus() != null && commodity.getRentStatus() == 1) {
                Date rentEnd = commodity.getRentEndTime();
                if (rentEnd == null || rentEnd.after(new Date())) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "该账号正在出租中");
                }
            }
        }

        long unitFactor = 1L;
        if (tradeType == 2) {
            unitFactor = "DAY".equals(rentalUnit) ? 24L : 1L;
        }
        BigDecimal totalAmount = tradeType == 1
                ? commodity.getPrice().multiply(new BigDecimal(purchaseNumber))
                : commodity.getPrice().multiply(new BigDecimal(rentalDuration * unitFactor));

        CommodityOrder order = new CommodityOrder();
        order.setUserId(loginUser.getId());
        order.setCommodityId(buyRequest.getCommodityId());
        order.setBuyNumber(tradeType == 1 ? purchaseNumber : 1);
        order.setPaymentAmount(totalAmount);
        order.setRemark(buyRequest.getRemark());
        order.setTradeType(tradeType);
        if (tradeType == 2) {
            order.setRentalDuration(rentalDuration);
            order.setRentalUnit(rentalUnit);
        }

        final int finalPurchaseNumber = purchaseNumber;
        final int finalRentalDuration = rentalDuration;
        final long finalUnitFactor = unitFactor;
        final BigDecimal finalTotalAmount = totalAmount;
        final int finalTradeType = tradeType;

        Map<String, Object> result = transactionTemplate.execute(status -> {
            try {
                User user = userService.getByIdWithLock(loginUser.getId());
                boolean balanceEnough = user.getBalance().compareTo(finalTotalAmount) >= 0;

                if (finalTradeType == 1 && commodity.getCommodityInventory() != null
                        && commodity.getCommodityInventory() < finalPurchaseNumber) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "库存不足");
                }

                if (finalTradeType == 2 && balanceEnough) {
                    LocalDateTime start = LocalDateTime.now();
                    LocalDateTime end = start.plusHours(finalRentalDuration * finalUnitFactor);
                    order.setRentStartTime(Date.from(start.atZone(ZoneId.systemDefault()).toInstant()));
                    order.setRentEndTime(Date.from(end.atZone(ZoneId.systemDefault()).toInstant()));
                }

                order.setPayStatus(balanceEnough ? 1 : 0);
                if (!commodityOrderService.save(order)) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "订单创建失败");
                }

                if (balanceEnough) {
                    if (finalTradeType == 1) {
                        commodity.setCommodityInventory(commodity.getCommodityInventory() - finalPurchaseNumber);
                    } else {
                        commodity.setRentStatus(1);
                        commodity.setRentStartTime(order.getRentStartTime());
                        commodity.setRentEndTime(order.getRentEndTime());
                    }
                    if (!commodityService.updateById(commodity)) {
                        throw new BusinessException(ErrorCode.OPERATION_ERROR, "商品状态更新失败");
                    }

                    user.setBalance(user.getBalance().subtract(finalTotalAmount));
                    if (!userService.updateById(user)) {
                        throw new BusinessException(ErrorCode.OPERATION_ERROR, "余额扣减失败");
                    }
                }

                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("orderId", order.getId());
                resultMap.put("payStatus", order.getPayStatus());
                resultMap.put("needPay", !balanceEnough);
                if (finalTradeType == 2) {
                    resultMap.put("rentalDuration", order.getRentalDuration());
                    resultMap.put("rentalUnit", order.getRentalUnit());
                    resultMap.put("rentStartTime", order.getRentStartTime());
                    resultMap.put("rentEndTime", order.getRentEndTime());
                }
                return resultMap;

            } catch (Exception e) {
                status.setRollbackOnly();
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "购买失败: " + e.getMessage());
            }
        });

        return ResultUtils.success(result);
    }


    // 支付接口（完成支付）
    @PostMapping("/pay")
    public synchronized BaseResponse<Boolean> payCommodityOrder(@RequestBody PayCommodityOrderRequest payRequest, HttpServletRequest request) {
        if (payRequest == null || payRequest.getCommodityOrderId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }

        User loginUser = userService.getLoginUser(request);

        CommodityOrder order = commodityOrderService.getByIdWithLock(payRequest.getCommodityOrderId());
        if (order == null || order.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }
        if (!order.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "无权操作此订单");
        }
        if (order.getPayStatus() != 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,
                    order.getPayStatus() == 1 ? "订单已完成支付" : "订单已过期");
        }

        Commodity commodity = commodityService.getByIdWithLock(order.getCommodityId());
        if (commodity == null || commodity.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商品不存在");
        }

        Integer tradeType = order.getTradeType() == null ? 1 : order.getTradeType();
        if (tradeType == 1) {
            if (commodity.getCommodityInventory() < order.getBuyNumber()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "库存不足");
            }
        } else {
            if (commodity.getRentStatus() != null && commodity.getRentStatus() == 1) {
                Date rentEnd = commodity.getRentEndTime();
                if (rentEnd == null || rentEnd.after(new Date())) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "该账号正在出租中");
                }
            }
            if (order.getRentalDuration() == null || order.getRentalDuration() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "租用信息不完整");
            }
        }

        User user = userService.getByIdWithLock(loginUser.getId());
        if (user.getBalance().compareTo(order.getPaymentAmount()) < 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "余额不足");
        }

        boolean result = transactionTemplate.execute(status -> {
            try {
                if (tradeType == 1) {
                    commodity.setCommodityInventory(commodity.getCommodityInventory() - order.getBuyNumber());
                } else {
                    long unitFactor = "DAY".equalsIgnoreCase(order.getRentalUnit()) ? 24L : 1L;
                    LocalDateTime start = LocalDateTime.now();
                    LocalDateTime end = start.plusHours(order.getRentalDuration() * unitFactor);
                    Date rentStart = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
                    Date rentEnd = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
                    order.setRentStartTime(rentStart);
                    order.setRentEndTime(rentEnd);
                    commodity.setRentStatus(1);
                    commodity.setRentStartTime(rentStart);
                    commodity.setRentEndTime(rentEnd);
                }
                if (!commodityService.updateById(commodity)) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "商品状态更新失败");
                }

                user.setBalance(user.getBalance().subtract(order.getPaymentAmount()));
                if (!userService.updateById(user)) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "余额扣减失败");
                }

                order.setPayStatus(1);
                if (!commodityOrderService.updateById(order)) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "订单状态更新失败");
                }

                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "支付失败: " + e.getMessage());
            }
        });

        return ResultUtils.success(result);
    }
    /**
     * 基于协同过滤的商品推荐接口
     *
     * @param userId 用户 ID
     * @return 推荐的商品列表
     */
    @GetMapping("/commodityRecommendation")
    public BaseResponse<List<Commodity>> recommendCommodities(@RequestParam Long userId) {

        // 1. 获取用户评分数据
        LambdaQueryWrapper<CommodityScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommodityScore::getUserId,userId);
        List<CommodityScore> userScores = commodityScoreService.list(queryWrapper);

        // 2. 获取所有商品的评分数据
        List<CommodityScore> allScores = commodityScoreService.list();

        // 3. 构建用户-商品评分矩阵
        Map<Long, Map<Long, Integer>> userCommodityRatingMap = new HashMap<>();
        for (CommodityScore score : allScores) {
            userCommodityRatingMap
                    .computeIfAbsent(score.getUserId(), k -> new HashMap<>())
                    .put(score.getCommodityId(), score.getScore());
        }

        // 4. 计算商品相似度
        Map<Long, Double> commoditySimilarityMap = calculateCommoditySimilarity(userScores, userCommodityRatingMap);

        // 5. 推荐商品
        List<Long> recommendedCommodityIds = recommendCommodities(userScores, commoditySimilarityMap);
        if(recommendedCommodityIds.isEmpty()){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"您还未购买或完成评分商品的行为，暂时无法推荐");
        }
        // 6. 查询推荐商品的详细信息
        return ResultUtils.success(commodityService.listByIds(recommendedCommodityIds));
    }

    /**
     * 计算商品相似度（基于余弦相似度）
     *
     * @param userScores              用户评分数据
     * @param userCommodityRatingMap 用户-商品评分矩阵
     * @return 商品相似度映射表
     */
    private Map<Long, Double> calculateCommoditySimilarity(List<CommodityScore> userScores, Map<Long, Map<Long, Integer>> userCommodityRatingMap) {
        Map<Long, Double> similarityMap = new HashMap<>();

        // 获取用户评分过的商品 ID
        Set<Long> ratedCommodityIds = userScores.stream()
                .map(CommodityScore::getCommodityId)
                .collect(Collectors.toSet());

        // 计算每个商品与用户评分过的商品的相似度
        for (Long commodityId : ratedCommodityIds) {
            double similarity = calculateCosineSimilarity(commodityId, userScores, userCommodityRatingMap);
            similarityMap.put(commodityId, similarity);
        }

        return similarityMap;
    }

    /**
     * 计算余弦相似度
     *
     * @param commodityId             商品 ID
     * @param userScores              用户评分数据
     * @param userCommodityRatingMap 用户-商品评分矩阵
     * @return 相似度值
     */
    private double calculateCosineSimilarity(Long commodityId, List<CommodityScore> userScores, Map<Long, Map<Long, Integer>> userCommodityRatingMap) {
        // 1. 获取目标商品的评分向量
        Map<Long, Integer> targetCommodityRatings = new HashMap<>();
        for (CommodityScore score : userScores) {
            if (score.getCommodityId().equals(commodityId)) {
                targetCommodityRatings.put(score.getUserId(), score.getScore());
            }
        }

        // 2. 遍历所有商品，计算与目标商品的相似度
        double dotProduct = 0.0; // 点积
        double targetNorm = 0.0; // 目标商品的向量模
        double otherNorm = 0.0;  // 其他商品的向量模

        for (Map.Entry<Long, Map<Long, Integer>> entry : userCommodityRatingMap.entrySet()) {
            Long userId = entry.getKey();
            Map<Long, Integer> ratings = entry.getValue();

            // 获取目标商品和当前商品的评分
            Integer targetRating = targetCommodityRatings.get(userId);
            Integer otherRating = ratings.get(commodityId);

            if (targetRating != null && otherRating != null) {
                dotProduct += targetRating * otherRating; // 累加点积
                targetNorm += Math.pow(targetRating, 2);  // 累加目标商品向量的平方
                otherNorm += Math.pow(otherRating, 2);   // 累加其他商品向量的平方
            }
        }

        // 3. 计算余弦相似度
        if (targetNorm == 0 || otherNorm == 0) {
            return 0.0; // 避免除零错误
        }
        return dotProduct / (Math.sqrt(targetNorm) * Math.sqrt(otherNorm));
    }

    /**
     * 推荐商品
     *
     * @param userScores          用户评分数据
     * @param commoditySimilarityMap 商品相似度映射表
     * @return 推荐的商品 ID 列表
     */
    private List<Long> recommendCommodities(List<CommodityScore> userScores, Map<Long, Double> commoditySimilarityMap) {
        // 根据相似度排序，推荐相似度高的商品
        return commoditySimilarityMap.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue())) // 按相似度降序排序
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    // endregion

    private void checkSellerPermission(User user, Integer tradeType) {
        if (user == null || tradeType == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        if (tradeType == 1) {
            if (user.getSellPermission() == null || user.getSellPermission() != 1) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "暂无出售权限，请先申请并通过审核");
            }
        } else if (tradeType == 2) {
            if (user.getRentPermission() == null || user.getRentPermission() != 1) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "暂无出租权限，请先申请并通过审核");
            }
        }
    }
}
