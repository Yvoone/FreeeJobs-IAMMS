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

	public Date getDOB() {
		return dob;
	}
}
