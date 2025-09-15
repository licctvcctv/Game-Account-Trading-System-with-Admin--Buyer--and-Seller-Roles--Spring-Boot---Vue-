package com.xiaobaitiao.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaobaitiao.springbootinit.common.ErrorCode;
import com.xiaobaitiao.springbootinit.constant.CommonConstant;
import com.xiaobaitiao.springbootinit.exception.ThrowUtils;
import com.xiaobaitiao.springbootinit.mapper.PrivateMessageMapper;
import com.xiaobaitiao.springbootinit.model.dto.privateMessage.PrivateMessageQueryRequest;
import com.xiaobaitiao.springbootinit.model.entity.PrivateMessage;
import com.xiaobaitiao.springbootinit.model.vo.PrivateMessageVO;
import com.xiaobaitiao.springbootinit.service.PrivateMessageService;
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
 * 私信表服务实现
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@Service
@Slf4j
public class PrivateMessageServiceImpl extends ServiceImpl<PrivateMessageMapper, PrivateMessage> implements PrivateMessageService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param privateMessage
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validPrivateMessage(PrivateMessage privateMessage, boolean add) {
        ThrowUtils.throwIf(privateMessage == null, ErrorCode.PARAMS_ERROR);
        Long senderId = privateMessage.getSenderId();
        Long recipientId = privateMessage.getRecipientId();
        String content = privateMessage.getContent();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(content), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(senderId == null || senderId <= 0, ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(recipientId == null || recipientId <= 0, ErrorCode.PARAMS_ERROR);
        }

    }

    /**
     * 获取查询条件
     *
     * @param privateMessageQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<PrivateMessage> getQueryWrapper(PrivateMessageQueryRequest privateMessageQueryRequest) {
        QueryWrapper<PrivateMessage> queryWrapper = new QueryWrapper<>();
        if (privateMessageQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = privateMessageQueryRequest.getId();
        Long senderId = privateMessageQueryRequest.getSenderId();
        Long recipientId = privateMessageQueryRequest.getRecipientId();
        String content = privateMessageQueryRequest.getContent();
        Integer alreadyRead = privateMessageQueryRequest.getAlreadyRead();
        String type = privateMessageQueryRequest.getType();
        Integer isRecalled = privateMessageQueryRequest.getIsRecalled();
        String sortField = privateMessageQueryRequest.getSortField();
        String sortOrder = privateMessageQueryRequest.getSortOrder();
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(type), "type", type);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(isRecalled), "isRecalled", isRecalled);
        queryWrapper.eq(ObjectUtils.isNotEmpty(alreadyRead), "alreadyRead", alreadyRead);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(senderId), "senderId", senderId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(recipientId), "recipientId", recipientId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取私信表封装
     *
     * @param privateMessage
     * @param request
     * @return
     */
    @Override
    public PrivateMessageVO getPrivateMessageVO(PrivateMessage privateMessage, HttpServletRequest request) {
        // 对象转封装类
        return PrivateMessageVO.objToVo(privateMessage);
    }

    /**
     * 分页获取私信表封装
     *
     * @param privateMessagePage
     * @param request
     * @return
     */
    @Override
    public Page<PrivateMessageVO> getPrivateMessageVOPage(Page<PrivateMessage> privateMessagePage, HttpServletRequest request) {
        List<PrivateMessage> privateMessageList = privateMessagePage.getRecords();
        Page<PrivateMessageVO> privateMessageVOPage = new Page<>(privateMessagePage.getCurrent(), privateMessagePage.getSize(), privateMessagePage.getTotal());
        if (CollUtil.isEmpty(privateMessageList)) {
            return privateMessageVOPage;
        }
        // 对象列表 => 封装对象列表
        List<PrivateMessageVO> privateMessageVOList = privateMessageList.stream().map(PrivateMessageVO::objToVo).collect(Collectors.toList());
        privateMessageVOPage.setRecords(privateMessageVOList);
        return privateMessageVOPage;
    }

}
