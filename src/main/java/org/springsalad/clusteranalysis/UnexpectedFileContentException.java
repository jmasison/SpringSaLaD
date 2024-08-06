package org.springsalad.clusteranalysis;

public class UnexpectedFileContentException extends RuntimeException {
	private static final long serialVersionUID = 2605309178717593766L;
	public UnexpectedFileContentException() {
		super();
	}
	public UnexpectedFileContentException(String message) {
	    super(message);
	}
	public UnexpectedFileContentException(String message, Throwable cause) {
		super(message, cause);
	}
	public UnexpectedFileContentException(Throwable cause) {
		super(cause);
	}

}
