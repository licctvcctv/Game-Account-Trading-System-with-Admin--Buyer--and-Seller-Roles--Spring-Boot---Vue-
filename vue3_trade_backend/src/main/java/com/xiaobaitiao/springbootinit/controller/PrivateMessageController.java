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
import com.xiaobaitiao.springbootinit.model.dto.privateMessage.PrivateMessageAddRequest;
import com.xiaobaitiao.springbootinit.model.dto.privateMessage.PrivateMessageEditRequest;
import com.xiaobaitiao.springbootinit.model.dto.privateMessage.PrivateMessageQueryRequest;
import com.xiaobaitiao.springbootinit.model.dto.privateMessage.PrivateMessageUpdateRequest;
import com.xiaobaitiao.springbootinit.model.entity.PrivateMessage;
import com.xiaobaitiao.springbootinit.model.entity.User;
import com.xiaobaitiao.springbootinit.model.vo.PrivateMessageVO;
import com.xiaobaitiao.springbootinit.service.PrivateMessageService;
import com.xiaobaitiao.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 私信表接口
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@RestController
@RequestMapping("/privateMessage")
@Slf4j
public class PrivateMessageController {

    @Resource
    private PrivateMessageService privateMessageService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建私信表
     *
     * @param privateMessageAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPrivateMessage(@RequestBody PrivateMessageAddRequest privateMessageAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(privateMessageAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        PrivateMessage privateMessage = new PrivateMessage();
        BeanUtils.copyProperties(privateMessageAddRequest, privateMessage);
        // 数据校验
        privateMessageService.validPrivateMessage(privateMessage, true);
        // 写入数据库
        boolean result = privateMessageService.save(privateMessage);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newPrivateMessageId = privateMessage.getId();
        return ResultUtils.success(newPrivateMessageId);
    }

    /**
     * 删除私信表
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePrivateMessage(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        PrivateMessage oldPrivateMessage = privateMessageService.getById(id);
        ThrowUtils.throwIf(oldPrivateMessage == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅管理员可删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = privateMessageService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新私信表（仅管理员可用）
     *
     * @param privateMessageUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePrivateMessage(@RequestBody PrivateMessageUpdateRequest privateMessageUpdateRequest) {
        if (privateMessageUpdateRequest == null || privateMessageUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        PrivateMessage privateMessage = new PrivateMessage();
        BeanUtils.copyProperties(privateMessageUpdateRequest, privateMessage);
        // 数据校验
        privateMessageService.validPrivateMessage(privateMessage, false);
        // 判断是否存在
        long id = privateMessageUpdateRequest.getId();
        PrivateMessage oldPrivateMessage = privateMessageService.getById(id);
        ThrowUtils.throwIf(oldPrivateMessage == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = privateMessageService.updateById(privateMessage);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取私信表（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<PrivateMessageVO> getPrivateMessageVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        PrivateMessage privateMessage = privateMessageService.getById(id);
        ThrowUtils.throwIf(privateMessage == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(privateMessageService.getPrivateMessageVO(privateMessage, request));
    }

    /**
     * 分页获取私信表列表（仅管理员可用）
     *
     * @param privateMessageQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<PrivateMessage>> listPrivateMessageByPage(@RequestBody PrivateMessageQueryRequest privateMessageQueryRequest) {
        long current = privateMessageQueryRequest.getCurrent();
        long size = privateMessageQueryRequest.getPageSize();
        // 查询数据库
        Page<PrivateMessage> privateMessagePage = privateMessageService.page(new Page<>(current, size),
                privateMessageService.getQueryWrapper(privateMessageQueryRequest));
        return ResultUtils.success(privateMessagePage);
    }

    /**
     * 分页获取私信表列表（封装类）
     *
     * @param privateMessageQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PrivateMessageVO>> listPrivateMessageVOByPage(@RequestBody PrivateMessageQueryRequest privateMessageQueryRequest,
                                                               HttpServletRequest request) {
        long current = privateMessageQueryRequest.getCurrent();
        long size = privateMessageQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<PrivateMessage> privateMessagePage = privateMessageService.page(new Page<>(current, size),
                privateMessageService.getQueryWrapper(privateMessageQueryRequest));
        // 获取封装类
        return ResultUtils.success(privateMessageService.getPrivateMessageVOPage(privateMessagePage, request));
    }

    /**
     * 分页获取当前登录用户创建的私信表列表
     *
     * @param privateMessageQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<PrivateMessageVO>> listMyPrivateMessageVOByPage(@RequestBody PrivateMessageQueryRequest privateMessageQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(privateMessageQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        long current = privateMessageQueryRequest.getCurrent();
        long size = privateMessageQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<PrivateMessage> privateMessagePage = privateMessageService.page(new Page<>(current, size),
                privateMessageService.getQueryWrapper(privateMessageQueryRequest));
        // 获取封装类
        return ResultUtils.success(privateMessageService.getPrivateMessageVOPage(privateMessagePage, request));
    }

    /**
     * 编辑私信表（给用户使用）
     *
     * @param privateMessageEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPrivateMessage(@RequestBody PrivateMessageEditRequest privateMessageEditRequest, HttpServletRequest request) {
        if (privateMessageEditRequest == null || privateMessageEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        PrivateMessage privateMessage = new PrivateMessage();
        BeanUtils.copyProperties(privateMessageEditRequest, privateMessage);
        // 数据校验
        privateMessageService.validPrivateMessage(privateMessage, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = privateMessageEditRequest.getId();
        PrivateMessage oldPrivateMessage = privateMessageService.getById(id);
        ThrowUtils.throwIf(oldPrivateMessage == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅管理员可编辑
        if ( !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = privateMessageService.updateById(privateMessage);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
