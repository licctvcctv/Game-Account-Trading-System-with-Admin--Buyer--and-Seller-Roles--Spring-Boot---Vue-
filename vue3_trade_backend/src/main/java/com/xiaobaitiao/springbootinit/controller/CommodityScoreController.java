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
import com.xiaobaitiao.springbootinit.model.dto.commodityScore.CommodityScoreAddRequest;
import com.xiaobaitiao.springbootinit.model.dto.commodityScore.CommodityScoreEditRequest;
import com.xiaobaitiao.springbootinit.model.dto.commodityScore.CommodityScoreQueryRequest;
import com.xiaobaitiao.springbootinit.model.dto.commodityScore.CommodityScoreUpdateRequest;
import com.xiaobaitiao.springbootinit.model.entity.CommodityScore;
import com.xiaobaitiao.springbootinit.model.entity.User;
import com.xiaobaitiao.springbootinit.model.vo.CommodityScoreVO;
import com.xiaobaitiao.springbootinit.service.CommodityScoreService;
import com.xiaobaitiao.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 商品评分表接口
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@RestController
@RequestMapping("/commodityScore")
@Slf4j
public class CommodityScoreController {

    @Resource
    private CommodityScoreService commodityScoreService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建商品评分表
     *
     * @param commodityScoreAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addCommodityScore(@RequestBody CommodityScoreAddRequest commodityScoreAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(commodityScoreAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        CommodityScore commodityScore = new CommodityScore();
        BeanUtils.copyProperties(commodityScoreAddRequest, commodityScore);
        // 数据校验
        commodityScoreService.validCommodityScore(commodityScore, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        commodityScore.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = commodityScoreService.save(commodityScore);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newCommodityScoreId = commodityScore.getId();
        return ResultUtils.success(newCommodityScoreId);
    }

    /**
     * 删除商品评分表
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteCommodityScore(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        CommodityScore oldCommodityScore = commodityScoreService.getById(id);
        ThrowUtils.throwIf(oldCommodityScore == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldCommodityScore.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = commodityScoreService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新商品评分表（仅管理员可用）
     *
     * @param commodityScoreUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateCommodityScore(@RequestBody CommodityScoreUpdateRequest commodityScoreUpdateRequest) {
        if (commodityScoreUpdateRequest == null || commodityScoreUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        CommodityScore commodityScore = new CommodityScore();
        BeanUtils.copyProperties(commodityScoreUpdateRequest, commodityScore);
        // 数据校验
        commodityScoreService.validCommodityScore(commodityScore, false);
        // 判断是否存在
        long id = commodityScoreUpdateRequest.getId();
        CommodityScore oldCommodityScore = commodityScoreService.getById(id);
        ThrowUtils.throwIf(oldCommodityScore == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = commodityScoreService.updateById(commodityScore);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取商品评分表（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<CommodityScoreVO> getCommodityScoreVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        CommodityScore commodityScore = commodityScoreService.getById(id);
        ThrowUtils.throwIf(commodityScore == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(commodityScoreService.getCommodityScoreVO(commodityScore, request));
    }

    /**
     * 分页获取商品评分表列表（仅管理员可用）
     *
     * @param commodityScoreQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<CommodityScore>> listCommodityScoreByPage(@RequestBody CommodityScoreQueryRequest commodityScoreQueryRequest) {
        long current = commodityScoreQueryRequest.getCurrent();
        long size = commodityScoreQueryRequest.getPageSize();
        // 查询数据库
        Page<CommodityScore> commodityScorePage = commodityScoreService.page(new Page<>(current, size),
                commodityScoreService.getQueryWrapper(commodityScoreQueryRequest));
        return ResultUtils.success(commodityScorePage);
    }

    /**
     * 分页获取商品评分表列表（封装类）
     *
     * @param commodityScoreQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<CommodityScoreVO>> listCommodityScoreVOByPage(@RequestBody CommodityScoreQueryRequest commodityScoreQueryRequest,
                                                               HttpServletRequest request) {
        long current = commodityScoreQueryRequest.getCurrent();
        long size = commodityScoreQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<CommodityScore> commodityScorePage = commodityScoreService.page(new Page<>(current, size),
                commodityScoreService.getQueryWrapper(commodityScoreQueryRequest));
        // 获取封装类
        return ResultUtils.success(commodityScoreService.getCommodityScoreVOPage(commodityScorePage, request));
    }

    /**
     * 分页获取当前登录用户创建的商品评分表列表
     *
     * @param commodityScoreQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<CommodityScoreVO>> listMyCommodityScoreVOByPage(@RequestBody CommodityScoreQueryRequest commodityScoreQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(commodityScoreQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        commodityScoreQueryRequest.setUserId(loginUser.getId());
        long current = commodityScoreQueryRequest.getCurrent();
        long size = commodityScoreQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<CommodityScore> commodityScorePage = commodityScoreService.page(new Page<>(current, size),
                commodityScoreService.getQueryWrapper(commodityScoreQueryRequest));
        // 获取封装类
        return ResultUtils.success(commodityScoreService.getCommodityScoreVOPage(commodityScorePage, request));
    }

    /**
     * 编辑商品评分表（给用户使用）
     *
     * @param commodityScoreEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editCommodityScore(@RequestBody CommodityScoreEditRequest commodityScoreEditRequest, HttpServletRequest request) {
        if (commodityScoreEditRequest == null || commodityScoreEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        CommodityScore commodityScore = new CommodityScore();
        BeanUtils.copyProperties(commodityScoreEditRequest, commodityScore);
        // 数据校验
        commodityScoreService.validCommodityScore(commodityScore, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = commodityScoreEditRequest.getId();
        CommodityScore oldCommodityScore = commodityScoreService.getById(id);
        ThrowUtils.throwIf(oldCommodityScore == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldCommodityScore.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = commodityScoreService.updateById(commodityScore);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
    /**
     * 获取商品的平均评分
     *
     * @param commodityId 商品 ID
     * @return 平均评分
     */
    @GetMapping("/averageScore")
    public BaseResponse getAverageScore(@RequestParam Long commodityId) {
        Optional<Double> averageScoreByCommodityId = Optional.ofNullable(commodityScoreService.getAverageScoreBySpotId(commodityId));
        if (averageScoreByCommodityId.isPresent()) {
            double result = Math.round(averageScoreByCommodityId.get() * 100.0) / 100.0;
            return ResultUtils.success(result);
        }
        return ResultUtils.error(ErrorCode.PARAMS_ERROR,"商品评分为空，快来成为第一个评分的人吧");
    }
    // endregion
}
