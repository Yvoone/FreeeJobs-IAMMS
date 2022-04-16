package com.freeejobs.IAM.dto;
import java.util.Date;

public class UserDTO {

    private long id;
	private String password;
	private String firstName;
	private String lastName;
	private String email;
	private String contactNo;
	private String gender;
	private String professionalTitle;
	private String aboutMe;
	private String aboutMeClient;
	private String skills;
	private String linkedInAcct;
	private String profilePicUrl;
	private String resumeUrl;
	private Date dob;

    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getContactNo() {
		return contactNo;
	}

	public String getGender() {
		return gender;
	}

	public String getProfessionalTitle() {
		return professionalTitle;
	}

	public String getAboutMe() {
		return aboutMe;
	}

	public String getAboutMeClient() {
		return aboutMeClient;
	}

	public String getSkills() {
		return skills;
	}

	public String getLinkedInAcct() {
		return linkedInAcct;
	}
	
	public String getProfilePicUrl() {
		return profilePicUrl;
	}

	public String getResumeUrl() {
		return resumeUrl;
	}

	public Date getDOB() {
		return dob;
	}

	public void setDOB(Date dob) {
		this.dob = dob;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setProfessionalTitle(String professionalTitle) {
		this.professionalTitle = professionalTitle;
	}

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	public void setAboutMeClient(String aboutMeClient) {
		this.aboutMeClient = aboutMeClient;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public void setLinkedInAcct(String linkedInAcct) {
		this.linkedInAcct = linkedInAcct;
	}

	public void setProfilePicUrl(String profilePicUrl) {
		this.profilePicUrl = profilePicUrl;
	}

	public void setResumeUrl(String resumeUrl) {
		this.resumeUrl = resumeUrl;
	}
}
