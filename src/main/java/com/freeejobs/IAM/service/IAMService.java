package com.freeejobs.IAM.service;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.security.cert.CertificateException;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.freeejobs.IAM.model.IAM;
import com.freeejobs.IAM.model.IAMAudit;
import com.freeejobs.IAM.repository.IAMAuditRepository;
import com.freeejobs.IAM.repository.IAMRepository;
import com.freeejobs.IAM.repository.UserAuditRepository;
import com.freeejobs.IAM.model.User;
import com.freeejobs.IAM.model.UserAudit;
import com.freeejobs.IAM.repository.UserRepository;

import net.bytebuddy.utility.RandomString;

import com.freeejobs.IAM.dto.UserDTO;
import com.freeejobs.IAM.dto.LinkedInDTO;
import com.freeejobs.IAM.dto.LinkedInLoginDTO;
import com.freeejobs.IAM.dto.LoginDTO;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.freeejobs.IAM.constants.AuditEnum;
import com.freeejobs.IAM.constants.IAMConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
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
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.web.client.RestTemplate;


@Service
public class IAMService {
	
	private final static String keyFileLocation="https://freeejobs-keys.s3.ap-southeast-1.amazonaws.com/macos/";

	private static final Logger LOGGER = LogManager.getLogger(IAMService.class);

	@Autowired
	private IAMRepository iamRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private IAMAuditRepository iamAuditRepository;

	@Autowired
	private UserAuditRepository userAuditRepository;
	
	@Autowired 
	private JavaMailSender mailSender;
	
	@Value("${application.bucket.name}")
    private String bucketName;
	
	@Autowired
    private AmazonS3 s3Client;

	public User getUserByUserId(long id) {
		return userRepository.findById(id);
	}

	public IAM getIAMByEmail(String email) {
		return iamRepository.findByEmail(email);
	}
	
	public IAM getIAMByUserId(long id) {
		return iamRepository.findByUserId(id);
	}
	
	public IAM getIAMByLinkedInId(String linkedInId) {
		return iamRepository.findByLinkedInId(linkedInId);
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
			String inputPwd = rsaDecrypt(loginDTO.getPassword());
			String dbPwd = aesDecryption(userCred.getPassword());
			loginDTO.setLoginStatus(getLoginStatus(inputPwd, dbPwd));
		}

		if(loginDTO.getLoginStatus() == IAMConstants.LOGIN.STATUS_SUCCESS && userCred!=null) {
			if(userCred.getSessionTimeout() != null&& currDate.before(userCred.getSessionTimeout())) {
				loginDTO.setLoginStatus(IAMConstants.LOGIN.STATUS_ACTIVE_SESSION);
			}

			if (loginDTO.getLoginStatus() != IAMConstants.LOGIN.STATUS_ACTIVE_SESSION) {
				currCal.add(Calendar.MINUTE, IAMConstants.LOGIN.SESSION_DURATION);
				Date timeoutTime = currCal.getTime();
				userCred.setSessionTimeout(timeoutTime);
				loginDTO.setUserId(userCred.getUserId());
			}
			if (userCred != null) {
				userCred.setFailedAttempt(IAMConstants.LOGIN.DEFAULT_ATTEMPT);
			}
			
			loginDTO.setUserRole(userCred.getUserRole());
		}
		else if (loginDTO.getLoginStatus() == IAMConstants.LOGIN.STATUS_FAIL && userCred!=null) {
			int failedAttempt = userCred.getFailedAttempt() + 1;
			userCred.setFailedAttempt(failedAttempt);
			
		}
		
		if(userCred!=null) {
			userCred.setDateUpdated(currDate);
			iamRepository.save(userCred);
			insertAudit(userCred, AuditEnum.UPDATE.getCode());
		}
		return loginDTO;

	}

	public IAM registerUser(UserDTO userDTO) throws Exception {
		userDTO.setPassword(aesEncryption(rsaDecrypt(userDTO.getPassword())));
		return addUser(userDTO, IAMConstants.USER.USER_ROLE_REGULAR);

	}

	public IAM registerAdmin(UserDTO userDTO) throws Exception {
		userDTO.setPassword(aesEncryption(rsaDecrypt(userDTO.getPassword())));
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
		if(userDTO.getLinkedInAcct() != null && !userDTO.getLinkedInAcct().isEmpty()) {
			user.setLinkedInAcct(userDTO.getLinkedInAcct());
		}
		if(userDTO.getProfilePicUrl() != null && !userDTO.getProfilePicUrl().isEmpty()) {
			user.setProfilePicUrl(userDTO.getProfilePicUrl());
		}
		if(userDTO.getResumeUrl() != null && !userDTO.getResumeUrl().isEmpty()) {
			user.setResumeUrl(userDTO.getResumeUrl());
		}
		user.setDateCreated(currDate);
		user.setDateUpdated(currDate);

		User addedUser = registerUserProfile(user);
		long userId = addedUser.getId();
		insertAudit(addedUser, AuditEnum.INSERT.getCode());

		IAM iam = new IAM();

		iam.setEmail(userDTO.getEmail());
		iam.setPassword(userDTO.getPassword());
		iam.setUserId(userId);
		iam.setUserRole(userRole);
		iam.setFailedAttempt(IAMConstants.LOGIN.DEFAULT_ATTEMPT);
		iam.setDateCreated(currDate);
		iam.setDateUpdated(currDate);


		IAM addedIAM = registerUserCredential(iam);
		insertAudit(addedIAM, AuditEnum.INSERT.getCode());
		return addedIAM;
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
			userDto.setProfilePicUrl(userProfile.getProfilePicUrl());
			userDto.setResumeUrl(userProfile.getResumeUrl());
			
			userDto.setEmail(iamProfile.getEmail());
		}
		return userDto;
	}

	public User updateUser(UserDTO userDto) {
		User user = getUserByUserId(userDto.getId());
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setContactNo(userDto.getContactNo());
		user.setProfessionalTitle(userDto.getProfessionalTitle());
		user.setAboutMe(userDto.getAboutMe());
		user.setAboutMeClient(userDto.getAboutMeClient());
		user.setSkills(userDto.getSkills());
		user.setDateUpdated(new Date());
		if(userDto.getProfilePicUrl() != null && !userDto.getProfilePicUrl().isEmpty()) {
			user.setProfilePicUrl(userDto.getProfilePicUrl());
		}
		if(userDto.getResumeUrl() != null && !userDto.getResumeUrl().isEmpty()) {
			user.setResumeUrl(userDto.getResumeUrl());
		}
		
		IAM iam = getIAMByUserId(userDto.getId());
		iam.setEmail(userDto.getEmail());
		iam.setDateUpdated(new Date());
		
		IAM updatedIAM = iamRepository.save(iam);
		insertAudit(updatedIAM, AuditEnum.UPDATE.getCode());
		
		User updatedUser = userRepository.save(user);
		insertAudit(updatedUser, AuditEnum.UPDATE.getCode());
		return updatedUser;
	}
	
	public IAM updateUserIAM(long userId) {
		IAM iam = getIAMByUserId(userId);
		iam.setSessionTimeout(new Date());
		return iamRepository.save(iam);
	}
	public boolean isId(String id) {
		return String.valueOf(id).matches("[0-9]+");
	}
	public boolean isPassword(String pwd) {
		String regexPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{7,}$";
		return String.valueOf(pwd).matches(regexPattern);
	}
	public boolean isContactNo(String contactNo) {
		String regexPattern = "^(?:\\+65)?[689][0-9]{7}$";
		return String.valueOf(contactNo).matches(regexPattern);
	}
	public boolean isEmailAdd(String email) {
		String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*+@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)$";
		return String.valueOf(email).matches(regexPattern);
	}
	public boolean isGender(String gender) {
		return (gender.equalsIgnoreCase("FEMALE")||gender.equalsIgnoreCase("MALE"));
	}
	
	public boolean isBlank(String value) {
		return StringUtils.isBlank(value);
	}
	
	public boolean validateFileName(String name) {
		String[] splitted = name.split("\\.");
		return String.valueOf(splitted[0]).matches("[0-9]+") && (splitted[1].equalsIgnoreCase("JPG")||splitted[1].equalsIgnoreCase("PDF"));
	}
	
	//Decryption
	public String rsaDecrypt(String plainText) throws NoSuchAlgorithmException{
		KeyFactory keyFactory=KeyFactory.getInstance("RSA");
		//to get from s3 bucket later on
		try {
			URL url = new URL(keyFileLocation+"private.key");
			
            byte[] privateKeyData = IOUtils.toByteArray(url);

			PrivateKey privKey =keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyData));

			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
 
        	cipher.init(Cipher.DECRYPT_MODE, privKey);
			
			return new String(cipher.doFinal(Base64.getDecoder().decode(plainText.getBytes())));
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);

			return "";
        }
		// PrivateKey privKey =keyFactory.generatePrivate(new PKCS8EncodedKeySpec(FileUtils.readFileToByteArray(new File(keyFileLocation+"private.key"))));
     }
	
	public String aesEncryption(String plainText)
			throws Exception {
		
		SecretKey secretKey = getKeyFromFile();
		
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

		//byte[] IV = generateIv();
		byte[] bytesIV = new byte[16];
		new SecureRandom().nextBytes(bytesIV);

	    /* KEY + IV setting */
	    IvParameterSpec iv = new IvParameterSpec(bytesIV);

		cipher.init(Cipher.ENCRYPT_MODE, secretKey,iv);

		byte[] encryptedByte = cipher.doFinal(plainText.getBytes());
		
		byte[] cipherTextWithIv = ByteBuffer.allocate(bytesIV.length + encryptedByte.length)
                .put(bytesIV)
                .put(encryptedByte)
                .array();
		
		Base64.Encoder encoder = Base64.getEncoder();
		
		String encryptedText = encoder.encodeToString(cipherTextWithIv);
		
		return encryptedText;
	}

	public String aesDecryption(String encryptedText)
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
	
	private SecretKey getKeyFromFile() throws IOException, KeyStoreException, NoSuchAlgorithmException, java.security.cert.CertificateException, UnrecoverableKeyException {
		InputStream inputStream = null;
		SecretKey secretKey = null;
		try {
			URL url = new URL(keyFileLocation+"FreeeJobsKeyStore.jceks");
			
			inputStream = url.openStream();
			
			byte[] secretKeyInBytes = new byte[inputStream.available()];
			
			String pwd = getImageHash();

			char[] pwdArray = pwd.toCharArray();
			
			KeyStore ks1 = KeyStore.getInstance("JCEKS");
			ks1.load(inputStream, pwdArray);
		    // ks1.load(new FileInputStream(file), pwdArray);
		    Key ssoSigningKey = ks1.getKey("secretKey", pwdArray);
		    //get AES key extracted from keystore in bytes and string
		    secretKeyInBytes = ssoSigningKey.getEncoded();
			secretKey = new SecretKeySpec(secretKeyInBytes, 0, secretKeyInBytes.length, "AES");
		}catch(Exception e) {
			
		}finally {
			if(inputStream!=null) {
				inputStream.close();
			}
			
		}
		return secretKey;		
	}
	
	public String getImageHash() {
	  	String hashedFile ="";
	  	try {
	  		//hash the png
			  	URL url = new URL(keyFileLocation+"branding-iss.png");
			
            	byte[] fileContent = IOUtils.toByteArray(url);
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
				LOGGER.error(e.getMessage(), e);
			}
			
	  	return hashedFile;
	}
	
	public IAMAudit insertAudit(IAM iam, String opsType) {
		IAMAudit newAuditEntry = new IAMAudit();
		newAuditEntry.setAuditData(iam.toString());
		newAuditEntry.setOpsType(opsType);
		newAuditEntry.setDateCreated(new Date());
		newAuditEntry.setCreatedBy(String.valueOf(iam.getUserId()));
		
		return iamAuditRepository.save(newAuditEntry);
	}
	
	public UserAudit insertAudit(User user, String opsType) {
		UserAudit newAuditEntry = new UserAudit();
		newAuditEntry.setAuditData(user.toString());
		newAuditEntry.setOpsType(opsType);
		newAuditEntry.setDateCreated(new Date());
		newAuditEntry.setCreatedBy(String.valueOf(user.getId()));
		
		return userAuditRepository.save(newAuditEntry);
	}
	
	public String uploadFile(MultipartFile file) {
        File fileObj = convertMultiPartFileToFile(file);
        s3Client.putObject(new PutObjectRequest(bucketName, file.getOriginalFilename(), fileObj));
        boolean fileUploaded = fileObj.delete();
        if(fileUploaded) {
        	return "File uploaded : " + file.getOriginalFilename();
        }else {
        	return "File upload failed : " + file.getOriginalFilename();
        }
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return convertedFile;
    }
    
    public String generateOneTimePassword(long userId) {
    	String OTP = RandomString.make(8);
        String encryptedOTP;
		try {
			Calendar currCal = Calendar.getInstance();
			Date otpReqTime = DateUtils.addMinutes(currCal.getTime(), 5);
			encryptedOTP = aesEncryption(OTP);
			IAM iam = getIAMByUserId(userId);
			iam.setOtpPassword(encryptedOTP);
			iam.setOtpRequestedTime(otpReqTime);
	         
	        iamRepository.save(iam);
	         
	        sendOTPEmail(iam, OTP);
	        return "Success";
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return "Failed";
		}
        
    }
     
    public void sendOTPEmail(IAM iam, String OTP) {
    	Properties props = new Properties();
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.starttls.enable", "true");
    	MimeMessage message = mailSender.createMimeMessage();              
        MimeMessageHelper helper = new MimeMessageHelper(message);
        
        User user = getUserByUserId(iam.getUserId());
         
        try {
			helper.setFrom("contact@freeejobs.com", "FreeeJobs Support");
		
        helper.setTo(iam.getEmail());
         
        String subject = "Here's your One Time Password (OTP) - Expire in 5 minutes!";
         
        String content = "<p>Hello " + user.getFirstName()+ "</p>"
                + "<p>For security reason, you're required to use the following "
                + "One Time Password to login:</p>"
                + "<p><b>" + OTP + "</b></p>"
                + "<br>"
                + "<p>Note: this OTP is set to expire in 5 minutes.</p>";
         
        helper.setSubject(subject);
         
        helper.setText(content, true);
         
        mailSender.send(message);  
        } catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
        	LOGGER.error(e.getMessage(), e);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage(), e);
		}
    }
 
    public void clearOTP(long userId) {
    	Calendar currCal = Calendar.getInstance();
    	currCal.add(Calendar.MINUTE, IAMConstants.LOGIN.SESSION_DURATION);
		Date timeoutTime = currCal.getTime();
    	IAM iam = getIAMByUserId(userId);
    	
    	iam.setOtpPassword(null);
    	iam.setOtpRequestedTime(null);
    	iam.setSessionTimeout(timeoutTime);
    	iamRepository.save(iam);
    }  
    
    public String validateOTP(String inputOTP, long userId) throws Exception {
    	IAM iam = getIAMByUserId(userId);
    	//check if otp expired first a not
    	if(iam.getOtpRequestedTime().before(new Date())) {
    		return "Expired";
    	}else {
    		if(inputOTP.equals(aesDecryption(iam.getOtpPassword()))) {
    			clearOTP(userId);
    			return "Verified";
    		}else {
    			return "Failed";
    		}
    	}
    }  
	
    public IAM registerLinkedInUser(LinkedInDTO linkedInDTO) throws Exception {
		return addLinkedInUser(linkedInDTO, IAMConstants.USER.USER_ROLE_REGULAR);
	}
    
    private IAM addLinkedInUser(LinkedInDTO linkedInDTO, int userRole) {
		User user = new User();
		Date currDate = new Date();

		user.setFirstName(linkedInDTO.getFirstName());
		user.setLastName(linkedInDTO.getLastName());
		user.setDateCreated(currDate);
		user.setDateUpdated(currDate);

		User addedUser = registerUserProfile(user);
		long userId = addedUser.getId();
		insertAudit(addedUser, AuditEnum.INSERT.getCode());

		IAM iam = new IAM();

		iam.setLinkedInId(linkedInDTO.getLinkedInId());
		iam.setUserId(userId);
		iam.setUserRole(userRole);
		iam.setFailedAttempt(IAMConstants.LOGIN.DEFAULT_ATTEMPT);
		iam.setDateCreated(currDate);
		iam.setDateUpdated(currDate);

		IAM addedIAM = registerUserCredential(iam);
		insertAudit(addedIAM, AuditEnum.INSERT.getCode());
		return addedIAM;
	}
    
    public LinkedInLoginDTO linkedInLogin(LinkedInLoginDTO linkedInLoginDTO) throws Exception {

		IAM userCred = null;
		//getIAMByLinkedInId(linkedInLoginDTO.getLinkedInId());
		getLinkedInProfile(linkedInLoginDTO);
		Calendar currCal = Calendar.getInstance();
		Date currDate = currCal.getTime();

		if (userCred == null) {
			linkedInLoginDTO.setLoginStatus(0);
		}
		else if (userCred.getFailedAttempt() >= IAMConstants.LOGIN.FAIL_ATTEMPT) {
			linkedInLoginDTO.setLoginStatus(IAMConstants.LOGIN.STATUS_LOCKED);
		}
		else {
			linkedInLoginDTO.setLoginStatus(IAMConstants.LOGIN.STATUS_SUCCESS);
		}

		if(linkedInLoginDTO.getLoginStatus() == IAMConstants.LOGIN.STATUS_SUCCESS && userCred!=null) {
			if(userCred.getSessionTimeout() != null&& currDate.before(userCred.getSessionTimeout())) {
				linkedInLoginDTO.setLoginStatus(IAMConstants.LOGIN.STATUS_ACTIVE_SESSION);
			}

			if (linkedInLoginDTO.getLoginStatus() != IAMConstants.LOGIN.STATUS_ACTIVE_SESSION) {
				currCal.add(Calendar.MINUTE, IAMConstants.LOGIN.SESSION_DURATION);
				Date timeoutTime = currCal.getTime();
				userCred.setSessionTimeout(timeoutTime);
				linkedInLoginDTO.setUserId(userCred.getUserId());
			}
			if (userCred != null) {
				userCred.setFailedAttempt(IAMConstants.LOGIN.DEFAULT_ATTEMPT);
			}
			
			linkedInLoginDTO.setUserRole(userCred.getUserRole());
		}
		else if (linkedInLoginDTO.getLoginStatus() == IAMConstants.LOGIN.STATUS_FAIL && userCred!=null) {
			int failedAttempt = userCred.getFailedAttempt() + 1;
			userCred.setFailedAttempt(failedAttempt);
			
		}
		
		if(userCred!=null) {
			userCred.setDateUpdated(currDate);
			iamRepository.save(userCred);
			insertAudit(userCred, AuditEnum.UPDATE.getCode());
		}
		return linkedInLoginDTO;

	}

	public void getLinkedInProfile(LinkedInLoginDTO linkedInLoginDTO) throws ParseException {
		String grant_type = "authorization_code";
		String code = linkedInLoginDTO.getAuthCode();
		System.out.println("<---------------------Testing----------------------------->");
		System.out.println(code);
		String redirect_uri = "https://freeejobs-web.herokuapp.com/login";
		String client_id = "86dyp3ax33yxnv";
		String client_secret = "yTTIjfaLrA18ryK2";

		RestTemplate restTemplate = new RestTemplate();

		String result = restTemplate.getForObject("https://www.linkedin.com/oauth/v2/accessToken?grant_type={grant_type}&code={code}&redirect_uri={redirect_uri}&client_id={client_id}&client_secret={client_secret}", 
		 					String.class,
							grant_type,
							code,
							redirect_uri,
							client_id,
							client_secret);

		System.out.println("<---------------------Testing----------------------------->");
		System.out.println(result);

		JSONParser parser = new JSONParser();  
		JSONObject json = (JSONObject) parser.parse(result);
		String oauth2_access_token = (String) json.get("access_token");

		String result2 = restTemplate.getForObject("https://api.linkedin.com/v2/me?oauth2_access_token={oauth2_access_token}", 
		 					String.class,
							oauth2_access_token);
		System.out.println("<---------------------Testing----------------------------->");
		System.out.println(result2);
		
		String result3 = restTemplate.getForObject("https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))?oauth2_access_token={oauth2_access_token}", 
		 					String.class,
							oauth2_access_token);
		
		System.out.println("<---------------------Testing----------------------------->");
		System.out.println(result3);

		
		
		

	}
	

}
