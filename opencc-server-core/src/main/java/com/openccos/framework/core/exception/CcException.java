package com.openccos.framework.core.exception;

/**
 * 〈全局异常〉
 *
 * @author kevin
 * @since 1.0.0
 */
public class CcException extends RuntimeException {

    private static final long serialVersionUID = 8096609992852791423L;

//    private ExceptionMessage cm;

    public CcException() {
    }

    public CcException(String msg) {
        super(msg);
    }

    public CcException(int code, String msg) {
        super(msg);
    }

    public CcException(String msg, Throwable e) {
        super(msg, e);
    }

    public CcException(Throwable e) {
        super(e.getMessage(), e);
    }

//    public SwException(ExceptionMessage cm) {
//        super(cm.getMsg());
//    }
//
//    public SwException(ExceptionMessage cm, Throwable e) {
//        super(cm.getMsg(), e);
//    }
//
//    public SwException(ExceptionMessage cm, String msg) {
//        super(msg);
//    }


//    public ExceptionMessage getCm() {
//        return cm;
//    }

}
