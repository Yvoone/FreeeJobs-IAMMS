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
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "firstName")
	private String firstName;

	@Column(name = "lastName")
	private String lastName;

	@Column(name = "contactNo")
	private String contactNo;

	@Column(name = "gender")
	private String gender;

	@Column(name = "professionalTitle")
	private String professionalTitle;

	@Column(name = "aboutMe", length=500)
	private String aboutMe;

	@Column(name = "aboutMeClient", length=500)
	private String aboutMeClient;

	@Column(name = "skills", length=500)
	private String skills;

	@Column(name = "linkedInAcct")
	private String linkedInAcct;
	
	@Column(name = "profilePicUrl", length=512)
	private String profilePicUrl;
	
	@Column(name = "resumeUrl", length=512)
	private String resumeUrl;

	@Column(name = "dob", columnDefinition="DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dob;

	@Column(name = "dateCreated", columnDefinition="DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreated;

	@Column(name = "dateUpdated", columnDefinition="DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateUpdated;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getProfessionalTitle() {
		return professionalTitle;
	}

	public void setProfessionalTitle(String professionalTitle) {
		this.professionalTitle = professionalTitle;
	}

	public String getAboutMe() {
		return aboutMe;
	}

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	public String getAboutMeClient() {
		return aboutMeClient;
	}

	public void setAboutMeClient(String aboutMeClient) {
		this.aboutMeClient = aboutMeClient;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public String getLinkedInAcct() {
		return linkedInAcct;
	}

	public void setLinkedInAcct(String linkedInAcct) {
		this.linkedInAcct = linkedInAcct;
	}

	public String getProfilePicUrl() {
		return profilePicUrl;
	}

	public void setProfilePicUrl(String profilePicUrl) {
		this.profilePicUrl = profilePicUrl;
	}

	public String getResumeUrl() {
		return resumeUrl;
	}

	public void setResumeUrl(String resumeUrl) {
		this.resumeUrl = resumeUrl;
	}

	public Date getDOB() {
		return dob;
	}

	public void setDOB(Date dob) {
		this.dob = dob;
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

	@Override
	public String toString() {
		return "User [firstName=" + firstName + ", lastName=" + lastName + ", contactNo=" + contactNo + ", gender="
				+ gender + ", professionalTitle=" + professionalTitle + ", aboutMe=" + aboutMe + ", aboutMeClient="
				+ aboutMeClient + ", skills=" + skills + ", linkedInAcct=" + linkedInAcct + ", profilePicUrl=" + profilePicUrl 
				+ ", resumeUrl=" + resumeUrl + ", dob=" + dob
				+ ", dateCreated=" + dateCreated + ", dateUpdated=" + dateUpdated + "]";
	}

}
