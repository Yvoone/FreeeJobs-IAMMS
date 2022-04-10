package com.freeejobs.IAM.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum AuditEnum {
	INSERT("I","Insert"), UPDATE("U", "Update"), DELETE("D", "Delete");
	
	public static class Constants{
		private Constants() {
		}
		
	}
	
	private String code;
	private String description;
	
	AuditEnum(final String code, String description){
		this.code=code;
		this.description=description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
