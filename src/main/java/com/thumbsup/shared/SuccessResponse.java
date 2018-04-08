package com.thumbsup.shared;

public class SuccessResponse{
	public boolean success ;
	
	public SuccessResponse(boolean success) {
		this.success = success ;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}