package com.kk.model.entities;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Spike Wong
 * @since 2022-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DishFlavor implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    private Long dishId;
    private String name;
    private String value;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill =FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    @TableField(fill =FieldFill.INSERT_UPDATE)
    private Long updateUser;
    private String isDeleted;


}
