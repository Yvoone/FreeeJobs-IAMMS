package com.freeejobs.IAM.service;

import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import org.apache.commons.io.FileUtils;

@Service
public class IAMService {
	
	private final String keyFileLocation="location...";

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
			String dbPwd = AESDecryption(userCred.getPassword());
			loginDTO.setLoginStatus(getLoginStatus(inputPwd, dbPwd));
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
		//userDTO.setPassword(RSADecrypt(userDTO.getPassword()));
		userDTO.setPassword(AESEncryption(RSADecrypt(userDTO.getPassword())));
		return addUser(userDTO, IAMConstants.USER.USER_ROLE_REGULAR);

	}

	public IAM registerAdmin(UserDTO userDTO) throws Exception {
		userDTO.setPassword(AESEncryption(RSADecrypt(userDTO.getPassword())));
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
	
	//Decryption
	public String RSADecrypt(String plainText)throws Exception{
		KeyFactory keyFactory=KeyFactory.getInstance("RSA");
		//to get from s3 bucket later on
		PrivateKey privKey =keyFactory.generatePrivate(new PKCS8EncodedKeySpec(FileUtils.readFileToByteArray(new File(keyFileLocation+"private.key"))));
    	
    	Cipher cipher = Cipher.getInstance("RSA");
 
        cipher.init(Cipher.DECRYPT_MODE, privKey);
 
        return new String(cipher.doFinal(Base64.getDecoder().decode(plainText.getBytes())));
     }
	
	public String AESEncryption(String plainText)
			throws Exception {
		
		SecretKey secretKey = getKeyFromFile();
		
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

		byte[] IV = generateIv();

		cipher.init(Cipher.ENCRYPT_MODE, secretKey,new IvParameterSpec(IV));

		byte[] encryptedByte = cipher.doFinal(plainText.getBytes());
		
		byte[] cipherTextWithIv = ByteBuffer.allocate(IV.length + encryptedByte.length)
                .put(IV)
                .put(encryptedByte)
                .array();
		
		Base64.Encoder encoder = Base64.getEncoder();
		
		String encryptedText = encoder.encodeToString(cipherTextWithIv);
		
		return encryptedText;
	}

	public String AESDecryption(String encryptedText)
			throws Exception {
		SecretKey secretKey = getKeyFromFile();
    	
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		
		Base64.Decoder decoder = Base64.getDecoder();
		
		byte[] encryptedTextByte = decoder.decode(encryptedText);

		ByteBuffer bb = ByteBuffer.wrap(encryptedTextByte);

        byte[] IV = new byte[16];
        bb.get(IV);

        byte[] cipherText = new byte[bb.remaining()];
        bb.get(cipherText);
        
//		IvParameterSpec ivParameterSpec = generateIv();

		cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(IV));
		
		

		byte[] result = cipher.doFinal(cipherText);

		return new String(result);
	}
	
	public byte[] generateIv() {
	    byte[] iv = new byte[16];
	    new SecureRandom().nextBytes(iv);
	    return iv;
	}
	
	private SecretKey getKeyFromFile() throws Exception {
		
		File file =new File(keyFileLocation+"FreeeJobsKeyStore.jceks");
		
		byte[] secretKeyInBytes = new byte[(int) file.length()];
		
		String pwd = getImageHash();

		char[] pwdArray = pwd.toCharArray();
		
		KeyStore ks1 = KeyStore.getInstance("JCEKS");
	    ks1.load(new FileInputStream(file), pwdArray);
	    Key ssoSigningKey = ks1.getKey("secretKey", pwdArray);
	    //get AES key extracted from keystore in bytes and string
	    secretKeyInBytes = ssoSigningKey.getEncoded();
		SecretKey secretKey = new SecretKeySpec(secretKeyInBytes, 0, secretKeyInBytes.length, "AES");
		return secretKey;		
	}
	
	public String getImageHash() {
	  	String hashedFile ="";
	  	try {
	  		//hash the png
				byte[] fileContent = FileUtils.readFileToByteArray(new File(keyFileLocation+"branding-iss.png"));
				MessageDigest digest = MessageDigest.getInstance("SHA-512");
				 
				byte[] inputBytes = fileContent;
				 
				byte[] hashBytes = digest.digest(inputBytes);
				
		        //get salt - first 3 char of hashed png
		        String myField = new String(hashBytes, 0, 3);
		        System.out.println(myField);
		        
		        //append salt to back of hashed png
		        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		        outputStream.write(hashBytes);
		        outputStream.write(myField.getBytes());
		        
		        //hash the concatenated values
		        byte[] hashsaltedHashBytes = digest.digest(outputStream.toByteArray( ));
		        
		        //get string of salted hash file - convert byte array to hex string
				StringBuffer stringBuffer = new StringBuffer();
		        for (int i = 0; i < hashsaltedHashBytes.length; i++) {
		            stringBuffer.append(Integer.toString((hashsaltedHashBytes[i] & 0xff) + 0x100, 16)
		                    .substring(1));
		        }
		        hashedFile = stringBuffer.toString();
		        
		        System.out.println(hashedFile);
		        
		        
				
			} catch (IOException | NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	  	return hashedFile;
	}	
	

}
