package com.kk.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @Description: mybatis-plus meta data handler,it's for mybatis-plus auto-fill field
 * @Author:Spike Wong
 * @Date:2022/6/19
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("public field auto-INSERT");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        if (!Objects.isNull(BaseContextOfEmployee.getCurrentId())) {
            metaObject.setValue("createUser", BaseContextOfEmployee.getCurrentId());
            metaObject.setValue("updateUser", BaseContextOfEmployee.getCurrentId());
        } else {
            metaObject.setValue("createUser", BaseContextOfUser.getCurrentId());
            metaObject.setValue("updateUser", BaseContextOfUser.getCurrentId());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("public field auto-Update");
        log.info(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        if (!Objects.isNull(BaseContextOfEmployee.getCurrentId())) {
            metaObject.setValue("updateUser",
                    BaseContextOfEmployee.getCurrentId());
        } else {
            metaObject.setValue("updateUser", BaseContextOfUser.getCurrentId());
        }

    }

}
