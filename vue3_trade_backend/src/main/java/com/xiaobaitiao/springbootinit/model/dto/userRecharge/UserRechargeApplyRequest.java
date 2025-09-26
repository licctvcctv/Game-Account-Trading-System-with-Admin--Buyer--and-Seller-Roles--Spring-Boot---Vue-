package com.xiaobaitiao.springbootinit.model.dto.userRecharge;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 用户充值申请请求体
 */
@Data
public class UserRechargeApplyRequest implements Serializable {

    /**
     * 充值金额
     */
    private BigDecimal amount;

    /**
     * 支付渠道（WECHAT/ALIPAY）
     */
    private String payChannel;

    /**
     * 支付凭证地址
     */
    private String proofUrl;

    /**
     * 申请备注
     */
    private String applyRemark;

    private static final long serialVersionUID = 1L;
}
