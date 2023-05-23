package com.openccos.framework.core;
import java.util.Date;

/**
 * 〈返回结果集〉
 *
 * @author kevin
 * @since 1.0.0
 */
public class R extends SwMap {
    public static final String KEY_MSG = "msg";
    public static final String KEY_CODE = "code";
    public static final String KEY_DATA = "data";
    public static final String KEY_NOW = "now";
    public static final String KEY_EXCEPTION = "exception";

    protected R() {
        this(0);
    }

    protected R(int code) {
        put(KEY_CODE, code);
        put(KEY_NOW, new Date());
    }

    public static R success() {
        return new R();
    }

    public static R success(Object data) {
        return new R().put(KEY_DATA, data);
    }

    public static R success(String name, Object data) {
        return new R().put(name, data);
    }

    public static R error() {
        return error("内部服务器异常");
    }

    public static R error(int code, String msg) {
        R r = new R(code);
        r.put(KEY_MSG, msg);
        return r;
    }

    public static R error(String msg) {
        return R.error(-1, msg);
    }

    public static R errorf(String format, Object...args) {
        return R.error(-1, String.format(format, args));
    }

    public static R error(String msg, Throwable ex) {
        String exception = ex.getMessage();
        if (exception == null) {
            exception = ex.getClass().getName();
        }

        return R.error(-1, msg).set(KEY_EXCEPTION, exception);
    }

//    public static R error(ExceptionMessage cm, String msg) {
//        return R.error(cm.getCode(), msg);
//    }

    @Override
    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public R set(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public R putNotNull(String key, Object value) {
        if (value != null) {
            super.put(key, value);
        }

        return this;
    }

//    public void setMessage(ExceptionMessage cm) {
//        this.put(KEY_CODE, cm.getCode());
//        this.put(KEY_MSG, cm.getMsg());
//    }

    public R msg(String value) {
        super.put(KEY_MSG, value);
        return this;
    }

    public void setData(Object data) {
        this.put(KEY_DATA, data);
    }

    public int readCode() {
        return this.readInt(KEY_CODE);
    }

    public String readMsg() {
        return this.readString(KEY_MSG);
    }

    public boolean readSuccess() {
        return this.readInt("code", -1) == 0;
    }
}
