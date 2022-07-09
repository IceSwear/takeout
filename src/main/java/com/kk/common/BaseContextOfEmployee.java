package com.kk.common;

/**
 * @Description: save and get current employee's id
 * @Author:Spike Wong
 * @Date:2022/6/19
 */
public class BaseContextOfEmployee {
    private BaseContextOfEmployee() {
    }

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
