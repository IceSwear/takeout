package com.kk.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Return result to front end
 * @param <T>
 */
@Data
public class R<T> implements Serializable {

    //1:successful ，
    // 0 or other numbers: defeat
    private Integer code;

    //
    private String msg;

    // object data
    private T data;

    //dynamic data
    private Map map = new HashMap();

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
