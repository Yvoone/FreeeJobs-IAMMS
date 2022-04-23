package com.freeejobs.IAM.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.multipart.MultipartFile;

import com.freeejobs.IAM.WebConfig;
import com.freeejobs.IAM.constants.AuditEnum;
import com.freeejobs.IAM.dto.LoginDTO;
import com.freeejobs.IAM.dto.UserDTO;
import com.freeejobs.IAM.model.IAM;
import com.freeejobs.IAM.model.IAMAudit;
import com.freeejobs.IAM.model.User;
import com.freeejobs.IAM.model.UserAudit;
import com.freeejobs.IAM.response.APIResponse;
import com.freeejobs.IAM.service.IAMService;

@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
@RunWith(JUnitPlatform.class)
@ExtendWith(MockitoExtension.class)
public class IAMControllerTest {
	
	@Mock
	private IAMService iamService;
	
	@InjectMocks
    private IAMController iamController;
	
	private IAM iamUser;
	private IAM iamAdmin;
	private User user;
	private LoginDTO loginDTOUser;
	private LoginDTO loginDTOAdmin;
	private LoginDTO loginDTOUser_failStatus;
	private UserDTO userDTO;
    private IAMAudit iamAudit;
    private UserAudit userAudit;
	private int numberOfListingPerPage=10;
	
	@BeforeEach
    void setUp() {
		iamUser = IAMFixture.createIAMUser();
		iamAdmin = IAMFixture.createIAMAdmin();
		user = IAMFixture.createUser();
		loginDTOUser = IAMFixture.createLoginDTOUser();
		loginDTOAdmin = IAMFixture.createLoginDTOAdmin();
		loginDTOUser_failStatus = IAMFixture.createLoginDTOUser_failStatus();
		userDTO = IAMFixture.createUserDTO();
		iamAudit = IAMFixture.createIAMAudit();
		userAudit = IAMFixture.createUserAudit();
		
    }
	
	@Test
    void testGetUserByUserId() throws URISyntaxException {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        Long userId = Long.valueOf(1);
        when(iamService.isId(String.valueOf(userId))).thenReturn(true);
        when(iamService.getUserByUserId(userId)).thenReturn(user);

        APIResponse iamRes = iamController.getUserByUserId(response, userId);
        assertEquals(user.getId(), ((User) iamRes.getData()).getId());
    }
	
	@Test
    void testGetUserByUserId_NotId() throws URISyntaxException {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        Long userId = Long.valueOf(1);
        when(iamService.isId(String.valueOf(userId))).thenReturn(false);

        APIResponse iamRes = iamController.getUserByUserId(response, userId);
        assertNull(((User) iamRes.getData()));
    }
	
	@Test
    void testGetUserByUserId_null() throws URISyntaxException {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        Long userId = Long.valueOf(1);
        when(iamService.isId(String.valueOf(userId))).thenReturn(true);
        when(iamService.getUserByUserId(userId)).thenReturn(null);

        APIResponse iamRes = iamController.getUserByUserId(response, userId);
        assertNull(((User) iamRes.getData()));
    }
	
	@Test
    void testGetUserByUserId_exception() throws URISyntaxException {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        Long userId = Long.valueOf(1);
        when(iamService.isId(String.valueOf(userId))).thenReturn(true);
        when(iamService.getUserByUserId(userId)).thenThrow(UnexpectedRollbackException.class);

        APIResponse iamRes = iamController.getUserByUserId(response, userId);
        assertNull(((User) iamRes.getData()));
    }
	
	@Test
    void testRegisterUser() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        Long userId = Long.valueOf(1);
        when(iamService.isPassword(userDTO.getPassword())).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(false);
        when(iamService.isContactNo(userDTO.getContactNo())).thenReturn(true);
        when(iamService.isEmailAdd(userDTO.getEmail())).thenReturn(true);
        when(iamService.isGender(userDTO.getGender())).thenReturn(true);
        when(iamService.registerUser(userDTO)).thenReturn(iamUser);

        APIResponse iamRes = iamController.registerUser(response, userDTO);
        assertEquals(iamUser.getId(), ((IAM) iamRes.getData()).getId());
    }
	
	@Test
    void testRegisterUser_notPassword() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        Long userId = Long.valueOf(1);
        when(iamService.isPassword(userDTO.getPassword())).thenReturn(false);

        APIResponse iamRes = iamController.registerUser(response, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testRegisterUser_firstNameBlank() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        Long userId = Long.valueOf(1);
        when(iamService.isPassword(userDTO.getPassword())).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(true);

        APIResponse iamRes = iamController.registerUser(response, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testRegisterUser_lastNameBlank() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        Long userId = Long.valueOf(1);
        when(iamService.isPassword(userDTO.getPassword())).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(true);

        APIResponse iamRes = iamController.registerUser(response, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testRegisterUser_notContactNo() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        Long userId = Long.valueOf(1);
        when(iamService.isPassword(userDTO.getPassword())).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(false);
        when(iamService.isContactNo(userDTO.getContactNo())).thenReturn(false);

        APIResponse iamRes = iamController.registerUser(response, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testRegisterUser_notEmailAdd() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        Long userId = Long.valueOf(1);
        when(iamService.isPassword(userDTO.getPassword())).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(false);
        when(iamService.isContactNo(userDTO.getContactNo())).thenReturn(true);
        when(iamService.isEmailAdd(userDTO.getEmail())).thenReturn(false);

        APIResponse iamRes = iamController.registerUser(response, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testRegisterUser_notGender() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        Long userId = Long.valueOf(1);
        when(iamService.isPassword(userDTO.getPassword())).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(false);
        when(iamService.isContactNo(userDTO.getContactNo())).thenReturn(true);
        when(iamService.isEmailAdd(userDTO.getEmail())).thenReturn(true);
        when(iamService.isGender(userDTO.getGender())).thenReturn(false);

        APIResponse iamRes = iamController.registerUser(response, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testRegisterUser_null() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        Long userId = Long.valueOf(1);
        when(iamService.isPassword(userDTO.getPassword())).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(false);
        when(iamService.isContactNo(userDTO.getContactNo())).thenReturn(true);
        when(iamService.isEmailAdd(userDTO.getEmail())).thenReturn(true);
        when(iamService.isGender(userDTO.getGender())).thenReturn(true);
        when(iamService.registerUser(userDTO)).thenReturn(null);

        APIResponse iamRes = iamController.registerUser(response, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testRegisterUser_exception() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        Long userId = Long.valueOf(1);
        when(iamService.isPassword(userDTO.getPassword())).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(false);
        when(iamService.isContactNo(userDTO.getContactNo())).thenReturn(true);
        when(iamService.isEmailAdd(userDTO.getEmail())).thenReturn(true);
        when(iamService.isGender(userDTO.getGender())).thenReturn(true);
        when(iamService.registerUser(userDTO)).thenThrow(UnexpectedRollbackException.class);

        APIResponse iamRes = iamController.registerUser(response, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	
	@Test
    void testRegisterAdmin() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        when(iamService.isPassword(userDTO.getPassword())).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(false);
        when(iamService.isContactNo(userDTO.getContactNo())).thenReturn(true);
        when(iamService.isEmailAdd(userDTO.getEmail())).thenReturn(true);
        when(iamService.isGender(userDTO.getGender())).thenReturn(true);
        when(iamService.registerAdmin(userDTO)).thenReturn(iamUser);

        APIResponse iamRes = iamController.registerAdmin(response, userDTO);
        assertEquals(iamUser.getId(), ((IAM) iamRes.getData()).getId());
    }
	
	@Test
    void testRegisterAdmin_notPassword() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        when(iamService.isPassword(userDTO.getPassword())).thenReturn(false);

        APIResponse iamRes = iamController.registerAdmin(response, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testRegisterAdmin_firstNameBlank() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        when(iamService.isPassword(userDTO.getPassword())).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(true);

        APIResponse iamRes = iamController.registerAdmin(response, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testRegisterAdmin_lastNameBlank() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        when(iamService.isPassword(userDTO.getPassword())).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(true);

        APIResponse iamRes = iamController.registerAdmin(response, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testRegisterAdmin_notContactNo() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        when(iamService.isPassword(userDTO.getPassword())).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(false);
        when(iamService.isContactNo(userDTO.getContactNo())).thenReturn(false);

        APIResponse iamRes = iamController.registerAdmin(response, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testRegisterAdmin_notEmailAdd() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        when(iamService.isPassword(userDTO.getPassword())).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(false);
        when(iamService.isContactNo(userDTO.getContactNo())).thenReturn(true);
        when(iamService.isEmailAdd(userDTO.getEmail())).thenReturn(false);

        APIResponse iamRes = iamController.registerAdmin(response, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testRegisterAdmin_notGender() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        when(iamService.isPassword(userDTO.getPassword())).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(false);
        when(iamService.isContactNo(userDTO.getContactNo())).thenReturn(true);
        when(iamService.isEmailAdd(userDTO.getEmail())).thenReturn(true);
        when(iamService.isGender(userDTO.getGender())).thenReturn(false);

        APIResponse iamRes = iamController.registerAdmin(response, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testRegisterAdmin_null() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        when(iamService.isPassword(userDTO.getPassword())).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(false);
        when(iamService.isContactNo(userDTO.getContactNo())).thenReturn(true);
        when(iamService.isEmailAdd(userDTO.getEmail())).thenReturn(true);
        when(iamService.isGender(userDTO.getGender())).thenReturn(true);
        when(iamService.registerAdmin(userDTO)).thenReturn(null);

        APIResponse iamRes = iamController.registerAdmin(response, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testRegisterAdmin_exception() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        when(iamService.isPassword(userDTO.getPassword())).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(false);
        when(iamService.isContactNo(userDTO.getContactNo())).thenReturn(true);
        when(iamService.isEmailAdd(userDTO.getEmail())).thenReturn(true);
        when(iamService.isGender(userDTO.getGender())).thenReturn(true);
        when(iamService.registerAdmin(userDTO)).thenThrow(UnexpectedRollbackException.class);

        APIResponse iamRes = iamController.registerAdmin(response, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	
	@Test
    void testLogin() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        when(iamService.isPassword(loginDTOUser.getPassword())).thenReturn(true);
        when(iamService.isEmailAdd(loginDTOUser.getEmail())).thenReturn(true);
        when(iamService.login(loginDTOUser)).thenReturn(loginDTOUser);

        APIResponse iamRes = iamController.registerUser(response, loginDTOUser);
        assertEquals(loginDTOUser.getId(), ((LoginDTO) iamRes.getData()).getId());
    }
	
	@Test
    void testLogin_notPassword() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        when(iamService.isPassword(loginDTOUser.getPassword())).thenReturn(false);

        APIResponse iamRes = iamController.registerUser(response, loginDTOUser);
        assertNull(((LoginDTO) iamRes.getData()));
    }
	@Test
    void testLogin_notEmailAdd() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        when(iamService.isPassword(loginDTOUser.getPassword())).thenReturn(true);
        when(iamService.isEmailAdd(loginDTOUser.getEmail())).thenReturn(false);

        APIResponse iamRes = iamController.registerUser(response, loginDTOUser);
        assertNull(((LoginDTO) iamRes.getData()));
    }
	@Test
    void testLogin_failStatus() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        when(iamService.isPassword(loginDTOUser.getPassword())).thenReturn(true);
        when(iamService.isEmailAdd(loginDTOUser.getEmail())).thenReturn(true);
        when(iamService.login(loginDTOUser)).thenReturn(loginDTOUser_failStatus);

        APIResponse iamRes = iamController.registerUser(response, loginDTOUser);
        assertEquals(loginDTOUser.getId(), ((LoginDTO) iamRes.getData()).getId());
    }
	@Test
    void testLogin_exception() throws Exception {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        when(iamService.isPassword(loginDTOUser.getPassword())).thenReturn(true);
        when(iamService.isEmailAdd(loginDTOUser.getEmail())).thenReturn(true);
        when(iamService.login(loginDTOUser)).thenThrow(UnexpectedRollbackException.class);

        APIResponse iamRes = iamController.registerUser(response, loginDTOUser);
        assertNull(((LoginDTO) iamRes.getData()));
    }
	
	@Test
    void testGetUserProfileWithEmailByUserId() throws URISyntaxException {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        Long userId = Long.valueOf(1);
        when(iamService.isId(String.valueOf(userId))).thenReturn(true);
        when(iamService.getUserProfileWithEmailByUserId(userId)).thenReturn(userDTO);

        APIResponse iamRes = iamController.getUserProfileWithEmailByUserId(response, userId);
        assertEquals(user.getId(), ((UserDTO) iamRes.getData()).getId());
    }
	
	@Test
    void testGetUserProfileWithEmailByUserId_NotId() throws URISyntaxException {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        Long userId = Long.valueOf(1);
        when(iamService.isId(String.valueOf(userId))).thenReturn(false);

        APIResponse iamRes = iamController.getUserProfileWithEmailByUserId(response, userId);
        assertNull(((UserDTO) iamRes.getData()));
    }
	
	@Test
    void testGetUserProfileWithEmailByUserId_null() throws URISyntaxException {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        Long userId = Long.valueOf(1);
        when(iamService.isId(String.valueOf(userId))).thenReturn(true);
        when(iamService.getUserProfileWithEmailByUserId(userId)).thenReturn(null);

        APIResponse iamRes = iamController.getUserProfileWithEmailByUserId(response, userId);
        assertNull(((UserDTO) iamRes.getData()));
    }
	
	@Test
    void testGetUserProfileWithEmailByUserId_exception() throws URISyntaxException {    
        HttpServletResponse response = mock(HttpServletResponse.class); 
        Long userId = Long.valueOf(1);
        when(iamService.isId(String.valueOf(userId))).thenReturn(true);
        when(iamService.getUserProfileWithEmailByUserId(userId)).thenThrow(UnexpectedRollbackException.class);

        APIResponse iamRes = iamController.getUserProfileWithEmailByUserId(response, userId);
        assertNull(((UserDTO) iamRes.getData()));
    }
	@Test
    void testUpdateUser() throws Exception {    
        Long id = Long.valueOf(1); 
        when(iamService.isId(String.valueOf(userDTO.getId()))).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(false);
        when(iamService.isContactNo(userDTO.getContactNo())).thenReturn(true);
        when(iamService.isEmailAdd(userDTO.getEmail())).thenReturn(true);
        when(iamService.updateUser(userDTO)).thenReturn(user);

        APIResponse iamRes = iamController.updateUser(id, userDTO);
        assertEquals(user.getId(), ((User) iamRes.getData()).getId());
    }
	
	@Test
    void testUpdateUser_notId() throws Exception {    
        Long id = Long.valueOf(1); 
        when(iamService.isId(String.valueOf(userDTO.getId()))).thenReturn(false);

        APIResponse iamRes = iamController.updateUser(id, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testUpdateUser_firstNameBlank() throws Exception {    
        Long id = Long.valueOf(1); 
        when(iamService.isId(String.valueOf(userDTO.getId()))).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(true);

        APIResponse iamRes = iamController.updateUser(id, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testUpdateUser_lastNameBlank() throws Exception {    
        Long id = Long.valueOf(1); 
        when(iamService.isId(String.valueOf(userDTO.getId()))).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(true);

        APIResponse iamRes = iamController.updateUser(id, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testUpdateUser_notContactNo() throws Exception {    
        Long id = Long.valueOf(1); 
        when(iamService.isId(String.valueOf(userDTO.getId()))).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(false);
        when(iamService.isContactNo(userDTO.getContactNo())).thenReturn(false);

        APIResponse iamRes = iamController.updateUser(id, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testUpdateUser_notEmailAdd() throws Exception {    
        Long id = Long.valueOf(1); 
        when(iamService.isId(String.valueOf(userDTO.getId()))).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(false);
        when(iamService.isContactNo(userDTO.getContactNo())).thenReturn(true);
        when(iamService.isEmailAdd(userDTO.getEmail())).thenReturn(false);

        APIResponse iamRes = iamController.updateUser(id, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testUpdateUser_null() throws Exception {    
        Long id = Long.valueOf(1); 
        when(iamService.isId(String.valueOf(userDTO.getId()))).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(false);
        when(iamService.isContactNo(userDTO.getContactNo())).thenReturn(true);
        when(iamService.isEmailAdd(userDTO.getEmail())).thenReturn(true);
        when(iamService.updateUser(userDTO)).thenReturn(null);

        APIResponse iamRes = iamController.updateUser(id, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	@Test
    void testUpdateUser_exception() throws Exception {    
        Long id = Long.valueOf(1); 
        when(iamService.isId(String.valueOf(userDTO.getId()))).thenReturn(true);
        when(iamService.isBlank(userDTO.getFirstName())).thenReturn(false);
        when(iamService.isBlank(userDTO.getLastName())).thenReturn(false);
        when(iamService.isContactNo(userDTO.getContactNo())).thenReturn(true);
        when(iamService.isEmailAdd(userDTO.getEmail())).thenReturn(true);
        when(iamService.updateUser(userDTO)).thenThrow(UnexpectedRollbackException.class);

        APIResponse iamRes = iamController.updateUser(id, userDTO);
        assertNull(((User) iamRes.getData()));
    }
	
	@Test
    void testUploadFile() throws Exception {    
        MultipartFile file = null;
        when(iamService.uploadFile(file)).thenReturn("File uploaded : ");

        APIResponse iamRes = iamController.uploadFile(file);
        assertEquals("File uploaded : ", ((String) iamRes.getData()));
    }
	
	@Test
    void testAuditEnum() {    

		AuditEnum.INSERT.setCode("T");
		AuditEnum.INSERT.setDescription("Test");

        assertEquals(AuditEnum.INSERT.getDescription(), "Test");
        assertEquals(AuditEnum.INSERT.getCode(), "T");

    }
	
	@Test
    void testInsertUserAudit() {    

		Date date = new Date();
		UserAudit userAudit = new UserAudit();
		userAudit.setId(1);
		userAudit.setOpsType(AuditEnum.INSERT.getCode());
		userAudit.setAuditData(user.toString());
		userAudit.setCreatedBy(String.valueOf(String.valueOf(user.getId())));
		userAudit.setDateCreated(date);

        assertEquals(userAudit.getId(), 1);
        assertEquals(userAudit.getAuditData(), user.toString());
        assertEquals(userAudit.getDateCreated(), date);
        assertEquals(userAudit.getCreatedBy(), String.valueOf(user.getId()));
        assertEquals(userAudit.getOpsType(),AuditEnum.INSERT.getCode());

    }
	
	@Test
    void testInsertIAMAudit() {    

		Date date = new Date();
		IAMAudit iamAudit = new IAMAudit();
		iamAudit.setId(1);
		iamAudit.setOpsType(AuditEnum.INSERT.getCode());
		iamAudit.setAuditData(iamUser.toString());
		iamAudit.setCreatedBy(String.valueOf(String.valueOf(iamUser.getId())));
		iamAudit.setDateCreated(date);

        assertEquals(iamAudit.getId(), 1);
        assertEquals(iamAudit.getAuditData(), iamUser.toString());
        assertEquals(iamAudit.getDateCreated(), date);
        assertEquals(iamAudit.getCreatedBy(), String.valueOf(iamUser.getId()));
        assertEquals(iamAudit.getOpsType(),AuditEnum.INSERT.getCode());

    }
	
	@Test
    void testDTO() {    

        assertNotNull(userDTO.getLinkedInAcct());
        assertNotNull(userDTO.getResumeUrl());
        assertNotNull(userDTO.getDOB());
        
        assertNotNull(loginDTOUser.getUserId());
        assertNotNull(loginDTOUser.getIsLinkedInAcct());
        assertNotNull(loginDTOUser.getUserRole());

    }
	
	
	
	

}
