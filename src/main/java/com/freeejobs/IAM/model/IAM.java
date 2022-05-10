package com.freeejobs.IAM.model;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "iam")
public class IAM {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "password")
	private String password;
	
	@Column(name = "tempPassword")
	private String tempPassword;

	@Column(name = "email")
	private String email;

	@Column(name = "userId")
	private long userId;

	@Column(name = "userRole")
	private int userRole;

	@Column(name = "failedAttempt")
	private int failedAttempt;
	
	@Column(name = "resetPwInd")
	private int resetPwInd;

	@Column(name = "dateCreated", columnDefinition="DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreated;

	@Column(name = "dateUpdated", columnDefinition="DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateUpdated;

	@Column(name = "sessionTimeout", columnDefinition="DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date sessionTimeout;
	
	@Column(name = "otp_password")
	private String otpPassword;
	
	@Column(name = "otp_requested_time", columnDefinition="DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date otpRequestedTime;
	
	@Column(name = "linkedInId")
	private String linkedInId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTempPassword() {
		return tempPassword;
	}

	public void setTempPassword(String tempPassword) {
		this.tempPassword = tempPassword;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public Date getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(Date sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}


	public int getUserRole() {
		return userRole;
	}

	public void setUserRole(int userRole) {
		this.userRole = userRole;
	}

	public int getFailedAttempt() {
		return failedAttempt;
	}

	public void setFailedAttempt(int failedAttempt) {
		this.failedAttempt = failedAttempt;
	}

	public int getResetPwInd() {
		return resetPwInd;
	}

	public void setResetPwInd(int resetPwInd) {
		this.resetPwInd = resetPwInd;
	}

	public String getOtpPassword() {
		return otpPassword;
	}

	public void setOtpPassword(String otpPassword) {
		this.otpPassword = otpPassword;
	}

	public Date getOtpRequestedTime() {
		return otpRequestedTime;
	}

	public void setOtpRequestedTime(Date otpRequestedTime) {
		this.otpRequestedTime = otpRequestedTime;
	}

	public String getLinkedInId() {
		return linkedInId;
	}

	public void setLinkedInId(String linkedInId) {
		this.linkedInId = linkedInId;
	}

	@Override
	public String toString() {
		return "IAM [password=" + password + ", email=" + email + ", userId=" + userId + ", userRole=" + userRole
				+ ", failedAttempt=" + failedAttempt + ", dateCreated=" + dateCreated + ", dateUpdated=" + dateUpdated
				+ ", sessionTimeout=" + sessionTimeout + "]";
	}


}
