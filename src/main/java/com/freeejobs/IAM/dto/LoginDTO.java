package com.freeejobs.IAM.dto;
import java.util.Date;

public class LoginDTO {

    private long id;
	private String password;
	private String email;
	private int isLinkedInAcct ;
	private int loginStatus;
	private long userId;
	private int userRole;
	private LinkedInDTO linkedInDTO;

    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public int getIsLinkedInAcct() {
		return isLinkedInAcct;
	}

	public int getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(int loginStatus) {
		this.loginStatus = loginStatus;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getUserRole() {
		return userRole;
	}

	public void setUserRole(int userRole) {
		this.userRole = userRole;
	}

	public LinkedInDTO getLinkedInDTO() {
		return linkedInDTO;
	}

	public void setLinkedInDTO(LinkedInDTO linkedInDTO) {
		this.linkedInDTO = linkedInDTO;
	}
}
