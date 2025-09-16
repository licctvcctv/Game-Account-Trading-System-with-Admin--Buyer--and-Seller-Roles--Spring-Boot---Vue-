package com.xiaobaitiao.springbootinit.model.dto.user;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 用户余额更新请求
 */
@Data
public class UserBalanceUpdateRequest implements Serializable {

    /**
     * 新的余额
     */
    private BigDecimal balance;

    private static final long serialVersionUID = 1L;
}
