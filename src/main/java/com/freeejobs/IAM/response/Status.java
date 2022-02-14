package com.freeejobs.IAM.response;

public class Status {

	private int statusCode;
	private String statusText;
	private String message;

	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusText() {
		return statusText;
	}
	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public Status() {}

	public Status(Status.Type status) {
    	this.statusCode = status.getCode();
    	this.statusText = status.getText();
    }

	public Status(Status.Type status, String message) {
		this.statusCode = status.getCode();
		this.statusText = status.getText();
		this.message = message;
	}

	@Override
	public String toString() {
		return "Status [statusCode=" + statusCode + ", statusText=" + statusText + ", message=" + message + "]";
	}

	public enum Type {

    	OK(200, "OK"),
    	CREATED(201, "Created"),
    	BAD_REQUEST(400, "Bad Request"),
    	UNAUTHORIZED(401, "Unauthorised"),
    	NOT_FOUND(404, "Not Found"),
    	INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    	private int code;
    	private String text;

    	Type(int code,  String text) {
    		this.code = code;
    		this.text = text;
    	}

		public int getCode() {
			return this.code;
		}

		public String getText() {
			return this.text;
		}

		public static Type fromCode(int code) {
			for (Type obj : Type.values()) {

				if (obj.getCode() == code) {
					return obj;
				}
			}

			throw new IllegalArgumentException("Invalid Input: " + code);
		}

		public static Type fromString(String text) {
			for (Type obj : Type.values()) {

				if (obj.getText().equalsIgnoreCase(text)) {
					return obj;
				}
			}

			throw new IllegalArgumentException("Invalid Input: " + text);
		}
    }
}
