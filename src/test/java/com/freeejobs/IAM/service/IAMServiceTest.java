package com.freeejobs.IAM.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.freeejobs.IAM.WebConfig;
import com.freeejobs.IAM.constants.IAMConstants;
import com.freeejobs.IAM.controller.IAMController;
import com.freeejobs.IAM.controller.IAMFixture;
import com.freeejobs.IAM.dto.LoginDTO;
import com.freeejobs.IAM.dto.UserDTO;
import com.freeejobs.IAM.model.IAM;
import com.freeejobs.IAM.model.IAMAudit;
import com.freeejobs.IAM.model.User;
import com.freeejobs.IAM.model.UserAudit;
import com.freeejobs.IAM.repository.IAMAuditRepository;
import com.freeejobs.IAM.repository.IAMRepository;
import com.freeejobs.IAM.repository.UserAuditRepository;
import com.freeejobs.IAM.repository.UserRepository;
import com.freeejobs.IAM.response.APIResponse;

import net.bytebuddy.utility.RandomString;

@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
@RunWith(JUnitPlatform.class)
@ExtendWith(MockitoExtension.class)
public class IAMServiceTest {
	@Mock
	private IAMRepository iamRepository;

	@Mock
	private UserRepository userRepository;
	
	@Mock
	private IAMAuditRepository iamAuditRepository;

	@Mock
	private UserAuditRepository userAuditRepository;
	
	@Value("${application.bucket.name}")
    private String bucketName;
	
	@Mock
    private AmazonS3 s3Client;
	
	@Mock
    JavaMailSender mailSender;

    @Mock
    MimeMessage mimeMessage;
	
	@InjectMocks
	private IAMService iamService;
	
	private IAM iamUser;
	private IAM iamUserEncrypted;
	private IAM iamAdmin;
	private User user;
	private LoginDTO loginDTOUser;
	private LoginDTO loginDTOAdmin;
	private LoginDTO loginDTOUser_failStatus;
	private LoginDTO loginDTOUser_encrypted;
	private UserDTO userDTO;
	private UserDTO userDTOEncryptedPw;
    private IAMAudit iamAudit;
    private UserAudit userAudit;
	private int numberOfListingPerPage=10;
	
	@BeforeEach
    void setUp() {
		iamUser = IAMFixture.createIAMUser();
		iamUserEncrypted = IAMFixture.createIAMUserEncryptedPw();
		iamAdmin = IAMFixture.createIAMAdmin();
		user = IAMFixture.createUser();
		loginDTOUser = IAMFixture.createLoginDTOUser();
		loginDTOAdmin = IAMFixture.createLoginDTOAdmin();
		loginDTOUser_failStatus = IAMFixture.createLoginDTOUser_failStatus();
		loginDTOUser_encrypted = IAMFixture.createLoginDTOUser_encrypted();
		userDTO = IAMFixture.createUserDTO();
		userDTOEncryptedPw = IAMFixture.createUserDTOEncryptedPassword();
		iamAudit = IAMFixture.createIAMAudit();
		userAudit = IAMFixture.createUserAudit();
		
    }
	
//	@Test
//    void testGetUserByUserId() throws URISyntaxException {    
//        HttpServletResponse response = mock(HttpServletResponse.class); 
//        Long id = Long.valueOf(1);
//        when(userRepository.findById(id)).thenReturn(user);
//
//        User iamRes = iamService.getUserByUserId(id);
//        assertEquals(user.getId(), iamRes.getId());
//    }
	@Test
    void testGetIAMByEmail() throws URISyntaxException {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        String email = "test@gmai.com";
        when(iamRepository.findByEmail(email)).thenReturn(iamUser);

        IAM iamRes = iamService.getIAMByEmail(email);
        assertEquals(iamUser.getId(), iamRes.getId());
    }
	@Test
    void testGetIAMByUserId() throws URISyntaxException {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        Long id = Long.valueOf(1);
        when(iamRepository.findByUserId(id)).thenReturn(iamUser);

        IAM iamRes = iamService.getIAMByUserId(id);
        assertEquals(iamUser.getId(), iamRes.getId());
    }
	
	@Test
    void testLogin_nullUser() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        Long id = Long.valueOf(1);
        when(iamService.getIAMByEmail(loginDTOUser.getEmail())).thenReturn(null);

        LoginDTO iamRes = iamService.login(loginDTOUser);
        assertEquals(loginDTOUser.getId(), iamRes.getId());
    }
	
	@Test
    void testLogin_exceedFailedAttempt() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        iamUser.setFailedAttempt(4);
        when(iamService.getIAMByEmail(loginDTOUser.getEmail())).thenReturn(iamUser);
        when(iamRepository.save(iamUser)).thenReturn(any(IAM.class));

        LoginDTO iamRes = iamService.login(loginDTOUser);
        assertEquals(loginDTOUser.getId(), iamRes.getId());
    }
	
	@Test
    void testLogin_success() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        String inputPwd = "password";
        String dbPwd = "password";
        when(iamService.getIAMByEmail(loginDTOUser_encrypted.getEmail())).thenReturn(iamUserEncrypted);
        ReflectionTestUtils.invokeMethod(iamService, "getLoginStatus", inputPwd, dbPwd);
        when(iamRepository.save(iamUserEncrypted)).thenReturn(any(IAM.class));

        LoginDTO iamRes = iamService.login(loginDTOUser_encrypted);
        assertEquals(loginDTOUser.getId(), iamRes.getId());
    }
	
	@Test
    void testLogin_sessionTimeout() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        String inputPwd = "password";
        String dbPwd = "password";
        Date dateAfter1Days = DateUtils.addDays(new Date(),+1);
        iamUserEncrypted.setSessionTimeout(dateAfter1Days);
        when(iamService.getIAMByEmail(loginDTOUser_encrypted.getEmail())).thenReturn(iamUserEncrypted);
        ReflectionTestUtils.invokeMethod(iamService, "getLoginStatus", inputPwd, dbPwd);
        when(iamRepository.save(iamUserEncrypted)).thenReturn(any(IAM.class));

        LoginDTO iamRes = iamService.login(loginDTOUser_encrypted);
        assertEquals(loginDTOUser.getId(), iamRes.getId());
    }
	@Test
    void testLogin_wrongPw() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        String inputPwd = "password";
        String dbPwd = "password";
        String wrongEncryptedFEPw ="JbSb46e18lxvA1R/QcLjSqcmCFYiF3nT/iHRxH95JKVN3/7uQsHCMku4EHt5n0FLTHxw16QExEIH9IK0LpD15fi7p7b0eG8ypDhkn5uaYeDzsRDMYoWJtaGiTMRQ39LcuWovdRmuNIDmbTU9+NS0iMUpBu4Gm68B/FhDfizEulKA+CbQeuPgu9pPjQsddmCF+5KZDprLC/Tl2C1twUJzoSYfHW3hNyW6BcreS+Ivkc3CRSkfb25ymPOf0sheKQeFPrk3LM75HgjRz9L8ipkfVhuEBVpKCFUIdEU77N1LAfUt45dm/irKguVBTbKUBtmEzCGOB3wuNxp42O0p91tpMA==";
        Date dateBefore1Days = DateUtils.addDays(new Date(),-1);
        iamUserEncrypted.setSessionTimeout(dateBefore1Days);
        ReflectionTestUtils.setField(loginDTOUser_encrypted, "password", wrongEncryptedFEPw);
        when(iamService.getIAMByEmail(loginDTOUser_encrypted.getEmail())).thenReturn(iamUserEncrypted);
        ReflectionTestUtils.invokeMethod(iamService, "getLoginStatus", inputPwd, dbPwd);
        when(iamRepository.save(iamUserEncrypted)).thenReturn(any(IAM.class));

        LoginDTO iamRes = iamService.login(loginDTOUser_encrypted);
        assertEquals(loginDTOUser.getId(), iamRes.getId());
    }
	
//	@Test
//    void testAddUser() throws Exception {    
//        HttpServletResponse response = mock(HttpServletResponse.class); 
//        String encryptedFEPw ="JbSb46e18lxvA1R/QcLjSqcmCFYiF3nT/iHRxH95JKVN3/7uQsHCMku4EHt5n0FLTHxw16QExEIH9IK0LpD15fi7p7b0eG8ypDhkn5uaYeDzsRDMYoWJtaGiTMRQ39LcuWovdRmuNIDmbTU9+NS0iMUpBu4Gm68B/FhDfizEulKA+CbQeuPgu9pPjQsddmCF+5KZDprLC/Tl2C1twUJzoSYfHW3hNyW6BcreS+Ivkc3CRSkfb25ymPOf0sheKQeFPrk3LM75HgjRz9L8ipkfVhuEBVpKCFUIdEU77N1LAfUt45dm/irKguVBTbKUBtmEzCGOB3wuNxp42O0p91tpMA==";
//        userDTO.setPassword(encryptedFEPw);
//        PowerMockito.doReturn(user).when(iamService, "registerUserProfile", user);
//        ReflectionTestUtils.invokeMethod(iamService, "registerUserProfile", user);
//        PowerMockito.doReturn(iamUser).when(iamService, "registerUserCredential", iamUser);
//        //ReflectionTestUtils.invokeMethod(iamService, "registerUserCredential", iamUser);
//        ReflectionTestUtils.invokeMethod(iamService, "addUser", userDTO, IAMConstants.USER.USER_ROLE_REGULAR);
////        when(iamService.addUser(userDTO, IAMConstants.USER.USER_ROLE_REGULAR)).thenReturn(iamUser);
////
////        IAM iamRes = iamService.addUser(userDTO, IAMConstants.USER.USER_ROLE_REGULAR);
//        //assertEquals(loginDTOUser.getId(), iamRes.getId());
//    }
	
	@Test
    void testGetUserProfileWithEmailByUserId() throws Exception {    
		Long userId = Long.valueOf(1);
        when(iamService.getUserByUserId(userId)).thenReturn(user);
        when(iamService.getIAMByUserId(userId)).thenReturn(iamUser);

        UserDTO iamRes = iamService.getUserProfileWithEmailByUserId(userId);
        assertEquals(userDTO.getId(), iamRes.getId());
    }
	
	@Test
    void testupdateUser() throws Exception {    
		Long userId = Long.valueOf(1);
        when(iamService.getUserByUserId(userId)).thenReturn(user);
        when(iamService.getIAMByUserId(userId)).thenReturn(iamUser);
        when(iamRepository.save(iamUser)).thenReturn(iamUser);
        when(userRepository.save(user)).thenReturn(user);
        
        User iamRes = iamService.updateUser(userDTO);
        assertEquals(user.getId(), iamRes.getId());
    }
	
	@Test
    void testIsId() {    

        boolean valid = iamService.isId("1");
        
        assertTrue(valid);
    }
	@Test
    void testIsNotId() {    

        boolean valid = iamService.isId("abc");
        
        assertFalse(valid);
    }
	
	@Test
    void testIsBlank() {    

        boolean valid = iamService.isBlank("");
        
        assertTrue(valid);
    }
	@Test
    void testIsNotBlank() {    

        boolean valid = iamService.isBlank("ABC");
        
        assertFalse(valid);
    }
	
	@Test
    void testIsPasswordValid() {    

        boolean valid = iamService.isPassword("P@ssw0rd!");
        
        assertTrue(valid);
    }
	@Test
    void testIsPasswordInvalid() {    

        boolean valid = iamService.isPassword("abc");
        
        assertFalse(valid);
    }
	
	@Test
    void testIsContactNoValid() {    

        boolean valid = iamService.isContactNo("99999999");
        
        assertTrue(valid);
    }
	@Test
    void testIsContactNoInvalid() {    

        boolean valid = iamService.isContactNo("00000000");
        
        assertFalse(valid);
    }
	
	@Test
    void testIsEmailValid() {    

        boolean valid = iamService.isEmailAdd("abc@gmail.com");
        
        assertTrue(valid);
    }
	@Test
    void testIsEmailInvalid() {    

        boolean valid = iamService.isEmailAdd("abc");
        
        assertFalse(valid);
    }
	
	@Test
    void testIsGenderValid() {    

        boolean valid = iamService.isGender("female");
        
        assertTrue(valid);
    }
	@Test
    void testIsGenderInvalid() {    

        boolean valid = iamService.isGender("f");
        
        assertFalse(valid);
    }
	
	@Test
    void testValidateFileName_true() {    

        boolean valid = iamService.validateFileName("1.jpg");
        
        assertTrue(valid);
    }
	@Test
    void testValidateFileName_false() {    

        boolean valid = iamService.validateFileName("abc.pdf");
        
        assertFalse(valid);
    }
	
	@Test
    void testRSADecrypt_Exception() throws Exception {
		
		String res = iamService.rsaDecrypt("abc");
		
		assertEquals(res,"");
    }
	
	@Test
    void testUploadFile() throws Exception {
		MultipartFile multipartFile = new MockMultipartFile("helloWorld.txt", "Hello World".getBytes());
		
        String res = iamService.uploadFile(multipartFile);
    }
	@Test
    void testGenerateIv() throws Exception {
		iamService.generateIv();
    }
	
	@Test
    void testRegisterUserProfile() throws Exception {
		Method method = IAMService.class.getDeclaredMethod("registerUserProfile", User.class);
		method.setAccessible(true);
		//IAMService iamService = new IAMService();
		User userRes = (User) method.invoke(iamService, user);
		verify(userRepository, Mockito.times(1)).save(user);
    }
	
	
	@Test
    void testRegisterUserCredential() throws Exception {
		Method method = IAMService.class.getDeclaredMethod("registerUserCredential", IAM.class);
		method.setAccessible(true);
		//IAMService iamService = new IAMService();
		User userRes = (User) method.invoke(iamService, iamUser);
		verify(iamRepository, Mockito.times(1)).save(iamUser);
    }
	
	@Test
    void testUpdateUserIAM() throws Exception {
		Long userId = Long.valueOf(1);
		Date timeout = new Date();
		when(iamService.getIAMByUserId(userId)).thenReturn(iamUser);
		iamUser.setSessionTimeout(timeout);
        when(iamRepository.save(iamUser)).thenReturn(iamUser);
        
        IAM iamRes = iamService.updateUserIAM(userId);
        assertEquals(iamUser.getId(), iamRes.getId());
    }
	
	@Test
	void testValidateOTP_Verified() throws Exception {
		Long userId = Long.valueOf(1);
		Calendar currCal = Calendar.getInstance();
		Date otpReqTime = DateUtils.addMinutes(currCal.getTime(), 5);
        iamUser.setOtpPassword("ZrFQULIY3ki5SCkTAY7177oFxZrIajB6xTpVkvYjUc0=");
        iamUser.setOtpRequestedTime(otpReqTime);
		when(iamService.getIAMByUserId(userId)).thenReturn(iamUser);
		String otpRes = iamService.validateOTP("lKtuWgkS", userId);
		assertEquals(otpRes, "Verified");
	}
	
	@Test
	void testValidateOTP_Expired() throws Exception {
		Long userId = Long.valueOf(1);
		Calendar currCal = Calendar.getInstance();
		Date otpReqTime = DateUtils.addMinutes(currCal.getTime(), -5);
        iamUser.setOtpPassword("ZrFQULIY3ki5SCkTAY7177oFxZrIajB6xTpVkvYjUc0=");
        iamUser.setOtpRequestedTime(otpReqTime);
		when(iamService.getIAMByUserId(userId)).thenReturn(iamUser);
		String otpRes = iamService.validateOTP("lKtuWgkS", userId);
		assertEquals(otpRes, "Expired");
	}
	
	@Test
	void testValidateOTP_Failed() throws Exception {
		Long userId = Long.valueOf(1);
		Calendar currCal = Calendar.getInstance();
		Date otpReqTime = DateUtils.addMinutes(currCal.getTime(), 5);
        iamUser.setOtpPassword("ZrFQULIY3ki5SCkTAY7177oFxZrIajB6xTpVkvYjUc0=");
        iamUser.setOtpRequestedTime(otpReqTime);
		when(iamService.getIAMByUserId(userId)).thenReturn(iamUser);
		String otpRes = iamService.validateOTP("abx", userId);
		assertEquals(otpRes, "Failed");
	}
	
	@Test
	void testGenerateOneTimePassword() throws Exception {
		Long userId = Long.valueOf(1);
		Calendar currCal = Calendar.getInstance();
		Date otpReqTime = DateUtils.addMinutes(currCal.getTime(), 5);
//		RandomString mockRandom = mock(RandomString.class);
//		when((String) mockRandom.make(8)).thenReturn("lKtuWgkS");
        iamUser.setOtpPassword("ZrFQULIY3ki5SCkTAY7177oFxZrIajB6xTpVkvYjUc0=");
        iamUser.setOtpRequestedTime(otpReqTime);
		when(iamService.getIAMByUserId(userId)).thenReturn(iamUser);
		
//		mimeMessage = mock(MimeMessage.class);
//		mailSender = mock(JavaMailSender.class);
//		MimeMessageHelper messageHelper = mock(MimeMessageHelper.class);
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        //when(new MimeMessageHelper(mimeMessage)).thenReturn(messageHelper);
//        when(iamService.getUserByUserId(userId)).thenReturn(user);
//		IAMService mockIamService = mock(IAMService.class);
//		doThrow(new NullPointerException()).when(mockIamService).sendOTPEmail(iamUser,"lKtuWgkS");
        
		String otpRes = iamService.generateOneTimePassword(userId);
		assertEquals(otpRes, "Failed");
	}
//	@Test
//    void testRegisterUser() throws Exception {
//		IAM iamRes = iamService.registerUser(userDTOEncryptedPw);
//		when(userRepository.save(user)).thenReturn(user);
//		assertEquals(userDTOEncryptedPw.getId(), iamRes.getId());
//    }
//	
//	@Test
//    void testAddUser() throws Exception {
//		when(iamRepository.save(iamUser)).thenReturn(iamUser);
//        when(userRepository.save(user)).thenReturn(user);
//		IAM iamRes = iamService.addUser(userDTO,IAMConstants.USER.USER_ROLE_ADMIN);
//		//when(userRepository.save(user)).thenReturn(user);
//		assertEquals(iamRes.getEmail(), userDTO.getEmail());
//    }
	
	
}

