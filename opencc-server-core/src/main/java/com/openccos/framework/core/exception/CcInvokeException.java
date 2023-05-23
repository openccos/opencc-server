package com.openccos.framework.core.exception;

public class CcInvokeException extends CcException {
  public CcInvokeException(String msg, Throwable e) {
    super(msg, e);
  }

  public CcInvokeException(Throwable e) {
    super(e);
  }
}
