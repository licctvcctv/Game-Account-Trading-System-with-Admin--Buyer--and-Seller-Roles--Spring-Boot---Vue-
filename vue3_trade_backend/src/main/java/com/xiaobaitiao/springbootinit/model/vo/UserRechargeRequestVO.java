package com.xiaobaitiao.springbootinit.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xiaobaitiao.springbootinit.model.entity.UserRechargeRequest;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 用户充值申请视图
 */
@Data
public class UserRechargeRequestVO implements Serializable {

    private Long id;

    private Long userId;

    private String userName;

    private BigDecimal amount;

    private String payChannel;

    private String proofUrl;

    private String applyRemark;

    private Integer status;

    private Long reviewerId;

    private String reviewerName;

    private String reviewMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reviewTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public static UserRechargeRequestVO objToVo(UserRechargeRequest request) {
        if (request == null) {
            return null;
        }
        UserRechargeRequestVO vo = new UserRechargeRequestVO();
        BeanUtils.copyProperties(request, vo);
        return vo;
    }
}
