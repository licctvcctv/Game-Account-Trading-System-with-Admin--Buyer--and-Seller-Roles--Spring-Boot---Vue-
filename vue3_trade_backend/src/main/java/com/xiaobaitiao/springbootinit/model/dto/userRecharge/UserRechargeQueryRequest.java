package com.xiaobaitiao.springbootinit.model.dto.userRecharge;

import com.xiaobaitiao.springbootinit.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户充值申请查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserRechargeQueryRequest extends PageRequest implements Serializable {

    /**
     * 申请ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 审核状态
     */
    private Integer status;

    /**
     * 审核人ID
     */
    private Long reviewerId;

    private static final long serialVersionUID = 1L;
}
