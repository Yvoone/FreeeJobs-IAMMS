package com.freeejobs.IAM.controller;

import java.lang.reflect.Field;
import java.util.Date;

import org.springframework.test.util.ReflectionTestUtils;

import com.freeejobs.IAM.constants.IAMConstants;
import com.freeejobs.IAM.dto.LoginDTO;
import com.freeejobs.IAM.dto.UserDTO;
import com.freeejobs.IAM.model.IAM;
import com.freeejobs.IAM.model.IAMAudit;
import com.freeejobs.IAM.model.User;
import com.freeejobs.IAM.model.UserAudit;

public class IAMFixture {
	
	public static IAM createIAMUser() {

		IAM iam = new IAM();
		iam.setId(Long.valueOf(1));
		iam.setPassword("password");
		iam.setEmail("test@gmai.com");
		iam.setUserId(Long.valueOf(1));
		iam.setUserRole(IAMConstants.USER.USER_ROLE_REGULAR);
		iam.setFailedAttempt(0);
		iam.setDateCreated(new Date());
		iam.setDateUpdated(new Date());
		iam.setSessionTimeout(new Date());
        

        return iam;
    }
	public static IAM createIAMUserEncryptedPw() {
		String DBEncryptedPw = "Ra2FAkmqOeKNmX7mMQcrKJLOs+PlOaqx/fNzeNCFLXA=";
        
		IAM iam = new IAM();
		iam.setId(Long.valueOf(1));
		iam.setPassword(DBEncryptedPw);
		iam.setEmail("test@gmai.com");
		iam.setUserId(Long.valueOf(1));
		iam.setUserRole(IAMConstants.USER.USER_ROLE_REGULAR);
		iam.setFailedAttempt(0);
		iam.setDateCreated(new Date());
		iam.setDateUpdated(new Date());
		iam.setSessionTimeout(new Date());
        

        return iam;
    }
	
	public static IAM createIAMAdmin() {

		IAM iam = new IAM();
		iam.setId(Long.valueOf(1));
		iam.setEmail("test@gmai.com");
		iam.setUserId(Long.valueOf(1));
		iam.setUserRole(IAMConstants.USER.USER_ROLE_ADMIN);
		iam.setFailedAttempt(0);
		iam.setDateCreated(new Date());
		iam.setDateUpdated(new Date());
		iam.setSessionTimeout(new Date());
        

        return iam;
    }
	
	public static LoginDTO createLoginDTOUser() {

		LoginDTO loginDTO = new LoginDTO();
		loginDTO.setId(Long.valueOf(1));
		loginDTO.setLoginStatus(IAMConstants.LOGIN.STATUS_SUCCESS);
		loginDTO.setUserId(Long.valueOf(1));
		loginDTO.setUserRole(IAMConstants.USER.USER_ROLE_REGULAR);
		ReflectionTestUtils.setField(loginDTO, "email", "test@gmai.com");
		ReflectionTestUtils.setField(loginDTO, "password", "password");
		ReflectionTestUtils.setField(loginDTO, "isLinkedInAcct", 1);
		ReflectionTestUtils.setField(loginDTO, "userId", Long.valueOf(1));
		ReflectionTestUtils.setField(loginDTO, "userRole", 1);
        

        return loginDTO;
    }
	public static LoginDTO createLoginDTOUser_failStatus() {

		LoginDTO loginDTO = new LoginDTO();
		loginDTO.setId(Long.valueOf(1));
		loginDTO.setLoginStatus(IAMConstants.LOGIN.STATUS_FAIL);
		loginDTO.setUserId(Long.valueOf(1));
		loginDTO.setUserRole(IAMConstants.USER.USER_ROLE_REGULAR);
		ReflectionTestUtils.setField(loginDTO, "email", "test@gmai.com");
		ReflectionTestUtils.setField(loginDTO, "password", "password");
        

        return loginDTO;
    }
	public static LoginDTO createLoginDTOUser_encrypted() {
		String encryptedFEPassword = "fbcvWO4i7R5uiBFy0/kmiT4puw8l+wqIefUUdAXDw/Ij4ANY3wvzaRk1hHiYqG3ltnp1IWcj4QdTStlAA6K7OckzGloY1qBF+FXVC4+8HVX43j1TP96eejjOlz67oeR7cirjbqW6k59DI7TMLFFV3mMYiUpMgB/yQQVUxQQ+Q6w257LNcWHSTWvCbGLRSXvaqR2oFPhomZQt5tEYVLE2JnZWH9lSvHFyQnne93EV4xdRu3j6rBErsup4HdrnQpxXY5S5okJ5Botarjtd854naxMjrRXsNPO9/OlASQebotoSDHhlXSD/eS9oiKMh9Vpx/lG0/iK86Zzr1otZWPDDkA==";
        
		LoginDTO loginDTO = new LoginDTO();
		loginDTO.setId(Long.valueOf(1));
		loginDTO.setLoginStatus(IAMConstants.LOGIN.STATUS_FAIL);
		loginDTO.setUserId(Long.valueOf(1));
		loginDTO.setUserRole(IAMConstants.USER.USER_ROLE_REGULAR);
		ReflectionTestUtils.setField(loginDTO, "email", "test@gmai.com");
		ReflectionTestUtils.setField(loginDTO, "password", encryptedFEPassword);
        

        return loginDTO;
    }
	public static LoginDTO createLoginDTOAdmin() {

		LoginDTO loginDTO = new LoginDTO();
		loginDTO.setId(Long.valueOf(1));
		loginDTO.setLoginStatus(IAMConstants.LOGIN.STATUS_SUCCESS);
		loginDTO.setUserId(Long.valueOf(1));
		loginDTO.setUserRole(IAMConstants.USER.USER_ROLE_ADMIN);

        return loginDTO;
    }
	
	public static UserDTO createUserDTO() {
		
		UserDTO userDTO = new UserDTO();
		userDTO.setId(Long.valueOf(1));
		userDTO.setFirstName("test");
		userDTO.setLastName("ing");
		userDTO.setContactNo("99999999");
		userDTO.setEmail("test@gmai.com");
		userDTO.setGender("male");
		userDTO.setProfessionalTitle("tester");
		userDTO.setAboutMe("i am a tester");
		userDTO.setAboutMeClient("i am testing");
		userDTO.setSkills("QA");
		userDTO.setLinkedInAcct("test");
		userDTO.setDOB(new Date());
		userDTO.setPassword("Password");
		userDTO.setResumeUrl("test");
		
        

        return userDTO;
    }
	
	public static UserDTO createUserDTOEncryptedPassword() {
		String encryptedFEPassword = "fbcvWO4i7R5uiBFy0/kmiT4puw8l+wqIefUUdAXDw/Ij4ANY3wvzaRk1hHiYqG3ltnp1IWcj4QdTStlAA6K7OckzGloY1qBF+FXVC4+8HVX43j1TP96eejjOlz67oeR7cirjbqW6k59DI7TMLFFV3mMYiUpMgB/yQQVUxQQ+Q6w257LNcWHSTWvCbGLRSXvaqR2oFPhomZQt5tEYVLE2JnZWH9lSvHFyQnne93EV4xdRu3j6rBErsup4HdrnQpxXY5S5okJ5Botarjtd854naxMjrRXsNPO9/OlASQebotoSDHhlXSD/eS9oiKMh9Vpx/lG0/iK86Zzr1otZWPDDkA==";
        
		UserDTO userDTO = new UserDTO();
		userDTO.setId(Long.valueOf(1));
		userDTO.setFirstName("test");
		userDTO.setLastName("ing");
		userDTO.setContactNo("99999999");
		userDTO.setEmail("test@gmai.com");
		userDTO.setGender("male");
		userDTO.setProfessionalTitle("tester");
		userDTO.setAboutMe("i am a tester");
		userDTO.setAboutMeClient("i am testing");
		userDTO.setSkills("QA");
		userDTO.setLinkedInAcct("test");
		userDTO.setDOB(new Date());
		userDTO.setPassword(encryptedFEPassword);
		userDTO.setResumeUrl("test");
		userDTO.setProfilePicUrl("test");
		
        

        return userDTO;
    }
	
	public static User createUser() {

		User user = new User();
		user.setId(Long.valueOf(1));
		user.setFirstName("test");
		user.setLastName("ing");
		user.setContactNo("99999999");
		user.setGender("male");
		user.setProfessionalTitle("tester");
		user.setAboutMe("i am a tester");
		user.setAboutMeClient("i am testing");
		user.setSkills("QA");
		user.setLinkedInAcct("test");
		user.setDOB(new Date());
		user.setDateCreated(new Date());
		user.setDateUpdated(new Date());
        

        return user;
    }
	
	public static IAMAudit createIAMAudit() {

		IAMAudit iamAudit = new IAMAudit();
		iamAudit.setAuditData("Audit");
		iamAudit.setCreatedBy("SYSTEM");
		iamAudit.setDateCreated(new Date());
		iamAudit.setId(1);
        

        return iamAudit;
    }
	public static UserAudit createUserAudit() {

		UserAudit userAudit = new UserAudit();
		userAudit.setAuditData("Audit");
		userAudit.setCreatedBy("SYSTEM");
		userAudit.setDateCreated(new Date());
		userAudit.setId(1);
        

        return userAudit;
    }

}
