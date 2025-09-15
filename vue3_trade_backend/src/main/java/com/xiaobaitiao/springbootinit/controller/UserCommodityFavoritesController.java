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
import com.xiaobaitiao.springbootinit.model.dto.userCommodityFavorites.UserCommodityFavoritesAddRequest;
import com.xiaobaitiao.springbootinit.model.dto.userCommodityFavorites.UserCommodityFavoritesEditRequest;
import com.xiaobaitiao.springbootinit.model.dto.userCommodityFavorites.UserCommodityFavoritesQueryRequest;
import com.xiaobaitiao.springbootinit.model.dto.userCommodityFavorites.UserCommodityFavoritesUpdateRequest;
import com.xiaobaitiao.springbootinit.model.entity.UserCommodityFavorites;
import com.xiaobaitiao.springbootinit.model.entity.User;
import com.xiaobaitiao.springbootinit.model.vo.UserCommodityFavoritesVO;
import com.xiaobaitiao.springbootinit.service.UserCommodityFavoritesService;
import com.xiaobaitiao.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户商品收藏表接口
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@RestController
@RequestMapping("/userCommodityFavorites")
@Slf4j
public class UserCommodityFavoritesController {

    @Resource
    private UserCommodityFavoritesService userCommodityFavoritesService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建用户商品收藏表
     *
     * @param userCommodityFavoritesAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUserCommodityFavorites(@RequestBody UserCommodityFavoritesAddRequest userCommodityFavoritesAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userCommodityFavoritesAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        UserCommodityFavorites userCommodityFavorites = new UserCommodityFavorites();
        BeanUtils.copyProperties(userCommodityFavoritesAddRequest, userCommodityFavorites);
        // 数据校验
        userCommodityFavoritesService.validUserCommodityFavorites(userCommodityFavorites, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        userCommodityFavorites.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = userCommodityFavoritesService.save(userCommodityFavorites);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newUserCommodityFavoritesId = userCommodityFavorites.getId();
        return ResultUtils.success(newUserCommodityFavoritesId);
    }

    /**
     * 删除用户商品收藏表
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUserCommodityFavorites(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserCommodityFavorites oldUserCommodityFavorites = userCommodityFavoritesService.getById(id);
        ThrowUtils.throwIf(oldUserCommodityFavorites == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldUserCommodityFavorites.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = userCommodityFavoritesService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新用户商品收藏表（仅管理员可用）
     *
     * @param userCommodityFavoritesUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserCommodityFavorites(@RequestBody UserCommodityFavoritesUpdateRequest userCommodityFavoritesUpdateRequest) {
        if (userCommodityFavoritesUpdateRequest == null || userCommodityFavoritesUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        UserCommodityFavorites userCommodityFavorites = new UserCommodityFavorites();
        BeanUtils.copyProperties(userCommodityFavoritesUpdateRequest, userCommodityFavorites);
        // 数据校验
        userCommodityFavoritesService.validUserCommodityFavorites(userCommodityFavorites, false);
        // 判断是否存在
        long id = userCommodityFavoritesUpdateRequest.getId();
        UserCommodityFavorites oldUserCommodityFavorites = userCommodityFavoritesService.getById(id);
        ThrowUtils.throwIf(oldUserCommodityFavorites == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = userCommodityFavoritesService.updateById(userCommodityFavorites);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户商品收藏表（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserCommodityFavoritesVO> getUserCommodityFavoritesVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        UserCommodityFavorites userCommodityFavorites = userCommodityFavoritesService.getById(id);
        ThrowUtils.throwIf(userCommodityFavorites == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(userCommodityFavoritesService.getUserCommodityFavoritesVO(userCommodityFavorites, request));
    }

    /**
     * 分页获取用户商品收藏表列表（仅管理员可用）
     *
     * @param userCommodityFavoritesQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserCommodityFavorites>> listUserCommodityFavoritesByPage(@RequestBody UserCommodityFavoritesQueryRequest userCommodityFavoritesQueryRequest) {
        long current = userCommodityFavoritesQueryRequest.getCurrent();
        long size = userCommodityFavoritesQueryRequest.getPageSize();
        // 查询数据库
        Page<UserCommodityFavorites> userCommodityFavoritesPage = userCommodityFavoritesService.page(new Page<>(current, size),
                userCommodityFavoritesService.getQueryWrapper(userCommodityFavoritesQueryRequest));
        return ResultUtils.success(userCommodityFavoritesPage);
    }

    /**
     * 分页获取用户商品收藏表列表（封装类）
     *
     * @param userCommodityFavoritesQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserCommodityFavoritesVO>> listUserCommodityFavoritesVOByPage(@RequestBody UserCommodityFavoritesQueryRequest userCommodityFavoritesQueryRequest,
                                                               HttpServletRequest request) {
        long current = userCommodityFavoritesQueryRequest.getCurrent();
        long size = userCommodityFavoritesQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<UserCommodityFavorites> userCommodityFavoritesPage = userCommodityFavoritesService.page(new Page<>(current, size),
                userCommodityFavoritesService.getQueryWrapper(userCommodityFavoritesQueryRequest));
        // 获取封装类
        return ResultUtils.success(userCommodityFavoritesService.getUserCommodityFavoritesVOPage(userCommodityFavoritesPage, request));
    }

    /**
     * 分页获取当前登录用户创建的用户商品收藏表列表
     *
     * @param userCommodityFavoritesQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<UserCommodityFavoritesVO>> listMyUserCommodityFavoritesVOByPage(@RequestBody UserCommodityFavoritesQueryRequest userCommodityFavoritesQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(userCommodityFavoritesQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        userCommodityFavoritesQueryRequest.setUserId(loginUser.getId());
        long current = userCommodityFavoritesQueryRequest.getCurrent();
        long size = userCommodityFavoritesQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<UserCommodityFavorites> userCommodityFavoritesPage = userCommodityFavoritesService.page(new Page<>(current, size),
                userCommodityFavoritesService.getQueryWrapper(userCommodityFavoritesQueryRequest));
        // 获取封装类
        return ResultUtils.success(userCommodityFavoritesService.getUserCommodityFavoritesVOPage(userCommodityFavoritesPage, request));
    }

    /**
     * 编辑用户商品收藏表（给用户使用）
     *
     * @param userCommodityFavoritesEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editUserCommodityFavorites(@RequestBody UserCommodityFavoritesEditRequest userCommodityFavoritesEditRequest, HttpServletRequest request) {
        if (userCommodityFavoritesEditRequest == null || userCommodityFavoritesEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        UserCommodityFavorites userCommodityFavorites = new UserCommodityFavorites();
        BeanUtils.copyProperties(userCommodityFavoritesEditRequest, userCommodityFavorites);
        // 数据校验
        userCommodityFavoritesService.validUserCommodityFavorites(userCommodityFavorites, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = userCommodityFavoritesEditRequest.getId();
        UserCommodityFavorites oldUserCommodityFavorites = userCommodityFavoritesService.getById(id);
        ThrowUtils.throwIf(oldUserCommodityFavorites == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldUserCommodityFavorites.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = userCommodityFavoritesService.updateById(userCommodityFavorites);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
