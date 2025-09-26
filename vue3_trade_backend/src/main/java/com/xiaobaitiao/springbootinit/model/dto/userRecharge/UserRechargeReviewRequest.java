package com.xiaobaitiao.springbootinit.model.dto.userRecharge;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户充值审核请求
 */
@Data
public class UserRechargeReviewRequest implements Serializable {

    /**
     * 充值申请ID
     */
    private Long id;

    /**
     * 审核状态 1-通过 2-拒绝
     */
    private Integer status;

    /**
     * 审核备注
     */
    private String reviewMessage;

    private static final long serialVersionUID = 1L;
}
