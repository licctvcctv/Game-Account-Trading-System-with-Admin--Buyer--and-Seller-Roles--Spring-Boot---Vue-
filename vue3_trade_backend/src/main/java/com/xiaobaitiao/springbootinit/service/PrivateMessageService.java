package com.xiaobaitiao.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaobaitiao.springbootinit.model.dto.privateMessage.PrivateMessageQueryRequest;
import com.xiaobaitiao.springbootinit.model.entity.PrivateMessage;
import com.xiaobaitiao.springbootinit.model.vo.PrivateMessageVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 私信表服务
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
public interface PrivateMessageService extends IService<PrivateMessage> {

    /**
     * 校验数据
     *
     * @param privateMessage
     * @param add 对创建的数据进行校验
     */
    void validPrivateMessage(PrivateMessage privateMessage, boolean add);

    /**
     * 获取查询条件
     *
     * @param privateMessageQueryRequest
     * @return
     */
    QueryWrapper<PrivateMessage> getQueryWrapper(PrivateMessageQueryRequest privateMessageQueryRequest);
    
    /**
     * 获取私信表封装
     *
     * @param privateMessage
     * @param request
     * @return
     */
    PrivateMessageVO getPrivateMessageVO(PrivateMessage privateMessage, HttpServletRequest request);

    /**
     * 分页获取私信表封装
     *
     * @param privateMessagePage
     * @param request
     * @return
     */
    Page<PrivateMessageVO> getPrivateMessageVOPage(Page<PrivateMessage> privateMessagePage, HttpServletRequest request);
}
