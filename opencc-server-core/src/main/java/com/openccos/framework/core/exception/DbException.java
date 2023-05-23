package com.openccos.framework.core.exception;

/**
 * bean绑定错误
 * @author kevin
 * @since 2010-9-14 上午11:17:43
 *
 */
public class DbException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public DbException() {
		super();
	}

	public DbException(String message, Throwable cause) {
		super(message, cause);
	}

	public DbException(String message) {
		super(message);
	}

	public DbException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
}
