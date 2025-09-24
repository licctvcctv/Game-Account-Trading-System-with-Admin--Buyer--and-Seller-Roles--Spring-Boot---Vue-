package com.xiaobaitiao.springbootinit.model.dto.barrage;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建弹幕请求
 *
 * 
 * 
 */
@Data
public class BarrageAddRequest implements Serializable {


    /**
     * 弹幕文本
     */
    private String message;

    /**
     * 用户头像
     */
    private String userAvatar;




    private static final long serialVersionUID = 1L;
}