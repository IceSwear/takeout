package com.kk.model.entities;

import lombok.Data;

import java.util.Date;


@Data
public class Marketing {
    private Integer id;
    private String receiver;
    private String alias;
    private String email;
    private String area;
    private String company;
    private String doWhat;
    private String createdBy;
    private String updatedBy;
    private Date createTime;
    private Date updateTime;
    private String isDeleted;
}
