package com.freeejobs.IAM.service;

import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.security.cert.CertificateException;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freeejobs.IAM.model.IAM;
import com.freeejobs.IAM.repository.IAMRepository;
import com.freeejobs.IAM.model.User;
import com.freeejobs.IAM.repository.UserRepository;
import com.freeejobs.IAM.dto.UserDTO;
import com.freeejobs.IAM.dto.LoginDTO;
import com.freeejobs.IAM.constants.IAMConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import org.apache.commons.io.FileUtils;

@Service
public class IAMService {

	private static final Logger LOGGER = LogManager.getLogger(IAMService.class);

	@Autowired
	private IAMRepository iamRepository;

	@Autowired
	private UserRepository userRepository;

	public User getUserByUserId(long id) {
		return userRepository.findById(id);
	}

	public IAM getIAMByEmail(String email) {
		return iamRepository.findByEmail(email);
	}
	
	public IAM getIAMByUserId(long id) {
		return iamRepository.findByUserId(id);
	}

	public LoginDTO login(LoginDTO loginDTO) throws Exception {

		IAM userCred = getIAMByEmail(loginDTO.getEmail());
		Calendar currCal = Calendar.getInstance();
		Date currDate = currCal.getTime();

		if (userCred == null) {
			loginDTO.setLoginStatus(0);
		}
		else if (userCred.getFailedAttempt() >= IAMConstants.LOGIN.FAIL_ATTEMPT) {
			loginDTO.setLoginStatus(IAMConstants.LOGIN.STATUS_LOCKED);
		}
		else {
			String inputPwd = RSADecrypt(loginDTO.getPassword());
			loginDTO.setLoginStatus(getLoginStatus(inputPwd, userCred.getPassword()));
		}

		if(loginDTO.getLoginStatus() == IAMConstants.LOGIN.STATUS_SUCCESS) {
			if(userCred.getSessionTimeout() != null ) {
				if(currDate.before(userCred.getSessionTimeout())) {
					loginDTO.setLoginStatus(IAMConstants.LOGIN.STATUS_ACTIVE_SESSION);
				}
			}

			if (loginDTO.getLoginStatus() != IAMConstants.LOGIN.STATUS_ACTIVE_SESSION) {
				currCal.add(Calendar.MINUTE, IAMConstants.LOGIN.SESSION_DURATION);
				Date timeoutTime = currCal.getTime();
				userCred.setSessionTimeout(timeoutTime);
				loginDTO.setUserId(userCred.getUserId());
			}
			userCred.setFailedAttempt(IAMConstants.LOGIN.DEFAULT_ATTEMPT);
			loginDTO.setUserRole(userCred.getUserRole());
		}
		else if (loginDTO.getLoginStatus() == IAMConstants.LOGIN.STATUS_FAIL) {
			int failedAttempt = userCred.getFailedAttempt() + 1;
			userCred.setFailedAttempt(failedAttempt);
		}

		userCred.setDateUpdated(currDate);
		iamRepository.save(userCred);

		return loginDTO;

	}

	public IAM registerUser(UserDTO userDTO) throws Exception {
		userDTO.setPassword(RSADecrypt(userDTO.getPassword()));

		return addUser(userDTO, IAMConstants.USER.USER_ROLE_REGULAR);

	}

	public IAM registerAdmin(UserDTO userDTO) {

		return addUser(userDTO, IAMConstants.USER.USER_ROLE_ADMIN);
	}

	private IAM addUser(UserDTO userDTO, int userRole) {
		User user = new User();
		Date currDate = new Date();

		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setContactNo(userDTO.getContactNo());
		user.setGender(userDTO.getGender());
		user.setDOB(userDTO.getDOB());
		user.setProfessionalTitle(userDTO.getProfessionalTitle());
		user.setAboutMe(userDTO.getAboutMe());
		user.setAboutMeClient(userDTO.getAboutMeClient());
		user.setSkills(userDTO.getSkills());
		user.setDateCreated(currDate);
		user.setDateUpdated(currDate);


		long userId = registerUserProfile(user).getId();

		IAM iam = new IAM();

		iam.setEmail(userDTO.getEmail());
		iam.setPassword(userDTO.getPassword());
		iam.setUserId(userId);
		iam.setUserRole(userRole);
		iam.setFailedAttempt(IAMConstants.LOGIN.DEFAULT_ATTEMPT);
		iam.setDateCreated(currDate);
		iam.setDateUpdated(currDate);


		return registerUserCredential(iam);
	}

	private User registerUserProfile(User user) {
		return userRepository.save(user);
	}

	private IAM registerUserCredential(IAM iam) {
		return iamRepository.save(iam);
	}

	private int getLoginStatus(String enteredPwd, String savedPwd) {
		if (enteredPwd.equals(savedPwd)) {
			return IAMConstants.LOGIN.STATUS_SUCCESS;
		}
		else {
			return IAMConstants.LOGIN.STATUS_FAIL;
		}
	}
	
	public UserDTO getUserProfileWithEmailByUserId(long userId) {
		
		UserDTO userDto = new UserDTO();
		User userProfile = null;
		IAM iamProfile = null;

		userProfile = getUserByUserId(userId);
		iamProfile = getIAMByUserId(userId);
		
		if(userProfile != null && iamProfile != null) {
			userDto.setId(userProfile.getId());
			userDto.setFirstName(userProfile.getFirstName());
			userDto.setLastName(userProfile.getLastName());
			userDto.setAboutMe(userProfile.getAboutMe());
			userDto.setAboutMeClient(userProfile.getAboutMeClient());
			userDto.setContactNo(userProfile.getContactNo());
			userDto.setDOB(userProfile.getDOB());
			userDto.setProfessionalTitle(userProfile.getProfessionalTitle());
			userDto.setSkills(userProfile.getSkills());
			userDto.setGender(userProfile.getGender());
			userDto.setLinkedInAcct(userProfile.getLinkedInAcct());

			userDto.setEmail(iamProfile.getEmail());
		}
		return userDto;
	}

	public User updateUser(UserDTO userDto) {
		User user = getUserByUserId(userDto.getId());
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setContactNo(userDto.getContactNo());
//		user.setGender(userDto.getGender());
//		user.setDOB(user.getDOB());
		user.setProfessionalTitle(userDto.getProfessionalTitle());
		user.setAboutMe(userDto.getAboutMe());
		user.setAboutMeClient(userDto.getAboutMeClient());
		user.setSkills(userDto.getSkills());
		user.setDateUpdated(new Date());
		
		IAM iam = getIAMByUserId(userDto.getId());
		iam.setEmail(userDto.getEmail());
		iam.setDateUpdated(new Date());
		
		iamRepository.save(iam);
		
		return userRepository.save(user);
	}
	public boolean isId(String id) {
		return String.valueOf(id).matches("[0-9]+");
	}
	public boolean isPassword(String pwd) {
		String regexPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{7,}$";
		return String.valueOf(pwd).matches(regexPattern);
	}
	public boolean isContactNo(String contactNo) {
		String regexPattern = "^(?:\\\\+65)?[689][0-9]{7}$";
		return String.valueOf(contactNo).matches(regexPattern);
	}
	public boolean isEmailAdd(String email) {
		String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" 
		        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
		return String.valueOf(email).matches(regexPattern);
	}
	public boolean isGender(String gender) {
		if(gender.equalsIgnoreCase("FEMALE")||gender.equalsIgnoreCase("MALE")) {
			return true;
		}else {
			return false;
		}
	}
	
	//Encryption
	public String RSADecrypt(String plainText)throws Exception{
		KeyFactory keyFactory=KeyFactory.getInstance("RSA");
		//to get from s3 bucket later on
		PrivateKey privKey =keyFactory.generatePrivate(new PKCS8EncodedKeySpec(FileUtils.readFileToByteArray(new File("/Users/yvonnetia/Documents/GitHub/FreeeJobs-IAMMS/src/main/resources/keys/private.key"))));
    	
    	Cipher cipher = Cipher.getInstance("RSA");
 
        cipher.init(Cipher.DECRYPT_MODE, privKey);
 
        return new String(cipher.doFinal(Base64.getDecoder().decode(plainText.getBytes())));
        }

}
