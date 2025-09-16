package com.xiaobaitiao.springbootinit.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 卖家权限申请请求
 */
@Data
public class SellerApplyRequest implements Serializable {

    /**
     * 申请类型：SELL 或 RENT
     */
    private String applyType;

    private static final long serialVersionUID = 1L;
}
