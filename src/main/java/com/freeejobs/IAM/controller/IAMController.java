package com.freeejobs.IAM.controller;

import java.io.Console;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.freeejobs.IAM.model.IAM;
import com.freeejobs.IAM.model.User;
import com.freeejobs.IAM.service.IAMService;
import com.freeejobs.IAM.response.APIResponse;
import com.freeejobs.IAM.response.Status;
import com.freeejobs.IAM.dto.UserDTO;
import com.freeejobs.IAM.constants.IAMConstants;
import com.freeejobs.IAM.dto.LinkedInDTO;
import com.freeejobs.IAM.dto.LinkedInLoginDTO;
import com.freeejobs.IAM.dto.LoginDTO;

@RestController
@RequestMapping(value="/iam")
@CrossOrigin("https://freeejobs-web.herokuapp.com")
public class IAMController {

	private static Logger LOGGER = LogManager.getLogger(IAMController.class);
	
	@Autowired
	private IAMService IAMService;

	@RequestMapping(value="/userProfile", method= RequestMethod.GET)
	public APIResponse getUserByUserId(HttpServletResponse response,
			@RequestParam long userId) throws URISyntaxException {

		User userProfile = null;
		APIResponse resp = new APIResponse();
		Status responseStatus = new Status(Status.Type.OK, "Account login success.");
		
		try {
			if(!IAMService.isId(String.valueOf(userId))){
				responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to get user Profile. Invalid user Id.");
				LOGGER.error(responseStatus.toString());
			}else {
				System.out.println(userId);
				userProfile = IAMService.getUserByUserId(userId);
					if(userProfile == null) {
						//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
						//return null;
						responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to get user Profile.");
						
					} else {
						//response.setStatus(HttpServletResponse.SC_OK);
						responseStatus = new Status(Status.Type.OK, "Successfully get user Profile.");
					}
			}
		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to get user Profile, Exception.");
			LOGGER.error(e.getMessage(), e);
		}
		resp.setData(userProfile);
		resp.setStatus(responseStatus);
		return resp;
	}
	
	@RequestMapping(value="/getUserSessionTimeout", method= RequestMethod.GET)
	public APIResponse getUserSessionTimeout(HttpServletResponse response,
			@RequestParam long userId) throws URISyntaxException {

		IAM userIAM = null;
		APIResponse resp = new APIResponse();
		Status responseStatus = new Status(Status.Type.OK, "Account login success.");
		Date timeout = null;
		
		try {
			if(!IAMService.isId(String.valueOf(userId))){
				responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to get user session timeout. Invalid user Id.");
				LOGGER.error(responseStatus.toString());
			}else {
				System.out.println(userId);
				userIAM = IAMService.getIAMByUserId(userId);
					if(userIAM == null) {
						//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						//return null;
						responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to get user session timeout.");
						
					} else {
						//response.setStatus(HttpServletResponse.SC_OK);
						timeout = userIAM.getSessionTimeout();
						responseStatus = new Status(Status.Type.OK, "Successfully get user session timeout.");
					}
			}
		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to get user session timeout, Exception.");
			LOGGER.error(e.getMessage(), e);
		}
		resp.setData(timeout);
		resp.setStatus(responseStatus);
		return resp;
	}

	@RequestMapping(value="/registerUser", method= RequestMethod.POST)
	public APIResponse registerUser(HttpServletResponse response,
			@RequestBody UserDTO userDTO) throws URISyntaxException {

		IAM iam = null;
		APIResponse resp = new APIResponse();
		Status responseStatus = new Status(Status.Type.OK, "Account login success.");
		List<String> errors = new ArrayList<String>();
		
		try {
			//TODO decrypt password
			if(!IAMService.isPassword(userDTO.getPassword())) {
				errors.add("Invalid password value");
			}
			if(IAMService.isBlank(userDTO.getFirstName())) {
				errors.add("Invalid first name value");
			}
			if(IAMService.isBlank(userDTO.getLastName())) {
				errors.add("Invalid first name value");
			}
			if(!IAMService.isContactNo(userDTO.getContactNo())) {
				errors.add("Invalid contactNo value");
			}
			if(!IAMService.isEmailAdd(userDTO.getEmail())) {
				errors.add("Invalid email value");
			}
			if(!IAMService.isGender(userDTO.getGender())) {
				errors.add("Invalid gender value");
			}
			//professional title, aboutme, aboutmeclient, skills not required this dont need check
			
			if(errors.isEmpty()) {
				System.out.println(userDTO.getFirstName());
				iam = IAMService.registerUser(userDTO);
					if(iam == null) {
						//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						//return null;
						responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to register User.");
						
					} else {
						//response.setStatus(HttpServletResponse.SC_OK);
						responseStatus = new Status(Status.Type.OK, "Successfully register User.");
					}
			}else {
				responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to register User. Invalid Register Object.");
				String listOfErrors = errors.stream().map(Object::toString)
                        .collect(Collectors.joining(", "));
				LOGGER.error(responseStatus.toString()+" "+listOfErrors);
			}
		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to register User, Exception.");
			LOGGER.error(e.getMessage(), e);
		}
		resp.setData(iam);
		resp.setStatus(responseStatus);
		return resp;
	}

	@RequestMapping(value="/registerAdmin", method= RequestMethod.POST)
	public APIResponse registerAdmin(HttpServletResponse response,
			@RequestBody UserDTO userDTO) throws URISyntaxException {

		IAM iam = null;
		APIResponse resp = new APIResponse();
		Status responseStatus = new Status(Status.Type.OK, "Account login success.");
		List<String> errors = new ArrayList<String>();
		
		try {
			//TODO decrypt password
			if(!IAMService.isPassword(userDTO.getPassword())) {
				errors.add("Invalid password value");
			}
			if(IAMService.isBlank(userDTO.getFirstName())) {
				errors.add("Invalid first name value");
			}
			if(IAMService.isBlank(userDTO.getLastName())) {
				errors.add("Invalid first name value");
			}
			if(!IAMService.isContactNo(userDTO.getContactNo())) {
				errors.add("Invalid contactNo value");
			}
			if(!IAMService.isEmailAdd(userDTO.getEmail())) {
				errors.add("Invalid email value");
			}
			if(!IAMService.isGender(userDTO.getGender())) {
				errors.add("Invalid gender value");
			}
			//professional title, aboutme, aboutmeclient, skills not required this dont need check
			
			if(errors.isEmpty()) {
				System.out.println(userDTO.getFirstName());
				iam = IAMService.registerAdmin(userDTO);
					if(iam == null) {
						//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						//return null;
						responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to register Admin.");
						
					} else {
						//response.setStatus(HttpServletResponse.SC_OK);
						responseStatus = new Status(Status.Type.OK, "Successfully register Admin.");
					}
			}else {
				responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to register Admin. Invalid Register Object.");
				String listOfErrors = errors.stream().map(Object::toString)
                        .collect(Collectors.joining(", "));
				LOGGER.error(responseStatus.toString()+" "+listOfErrors);
			}
		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to register Admin, Exception.");
			LOGGER.error(e.getMessage(), e);
		}
		resp.setData(iam);
		resp.setStatus(responseStatus);
		return resp;
	}

	@RequestMapping(value="/login", method= RequestMethod.POST)
	public APIResponse registerUser(HttpServletResponse response,
			@RequestBody LoginDTO loginDTO) throws URISyntaxException {

		LoginDTO login = null;
		APIResponse resp = new APIResponse();
		Status responseStatus = new Status(Status.Type.OK, "Account login success.");
		List<String> errors = new ArrayList<String>();
		try {
			//TODO decrypt password
			if(!IAMService.isPassword(String.valueOf(loginDTO.getPassword()))) {
				errors.add("Invalid password value");
			}
			if(!IAMService.isEmailAdd(loginDTO.getEmail())) {
				errors.add("Invalid email value");
			}
			
			if(errors.isEmpty()) {
				System.out.println(loginDTO.getEmail());
				login = IAMService.login(loginDTO);
					if(login.getLoginStatus()!=IAMConstants.LOGIN.STATUS_SUCCESS) {
						//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						//return null;
						responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to login, status: "+login.getLoginStatus());
						
					} else {

						System.out.println(login.getPassword());
						//response.setStatus(HttpServletResponse.SC_OK);
						responseStatus = new Status(Status.Type.OK, "Successfully logged in.");
					}
			}else {
				responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to login. Invalid login Object.");
				String listOfErrors = errors.stream().map(Object::toString)
                        .collect(Collectors.joining(", "));
				LOGGER.error(responseStatus.toString()+" "+listOfErrors);
			}	
		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to login, Exception.");
			LOGGER.error(e.getMessage(), e);
		}
		resp.setData(login);
		resp.setStatus(responseStatus);
		return resp;
	}
	
	@RequestMapping(value="/userProfileWithEmail", method= RequestMethod.GET)
	public APIResponse getUserProfileWithEmailByUserId(HttpServletResponse response,
			@RequestParam long userId) throws URISyntaxException {

		UserDTO userDto = null;
		APIResponse resp = new APIResponse();
		Status responseStatus = new Status(Status.Type.OK, "Account login success.");
		
		try {
			if(!IAMService.isId(String.valueOf(userId))){
				responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to get user Profile with Email. Invalid user Id.");
				LOGGER.error(responseStatus.toString());
			}else {
				System.out.println(userId);
				userDto = IAMService.getUserProfileWithEmailByUserId(userId);
				
					if(userDto == null) {
						//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						//return null;
						responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to get user Profile with Email.");
						
					} else {
						//response.setStatus(HttpServletResponse.SC_OK);
						responseStatus = new Status(Status.Type.OK, "Successfully get user Profile with Email.");
					}
			}	
			
		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to get user Profile with Email, Exception.");
			LOGGER.error(e.getMessage(), e);
		}
		resp.setData(userDto);
		resp.setStatus(responseStatus);
		return resp;
	}

	@PutMapping("/{id}/edit")
    public APIResponse updateUser(@PathVariable("id") Long id, @RequestBody UserDTO user) {
		User updateUser = null;
		APIResponse resp = new APIResponse();
		Status responseStatus = new Status(Status.Type.OK, "Account login success.");
		List<String> errors = new ArrayList<String>();
		
		try {
			if(!IAMService.isId(String.valueOf(user.getId()))) {
				errors.add("Invalid id value");
			}
			//wont check for pwd
			if(IAMService.isBlank(user.getFirstName())) {
				errors.add("Invalid first name value");
			}
			if(IAMService.isBlank(user.getLastName())) {
				errors.add("Invalid last name value");
			}
			if(!IAMService.isContactNo(user.getContactNo())) {
				errors.add("Invalid contactNo value");
			}
			if(!IAMService.isEmailAdd(user.getEmail())) {
				errors.add("Invalid email value");
			}
//			if(!IAMService.isGender(user.getGender())) {
//				errors.add("Invalid gender value");
//			}
			//professional title, aboutme, aboutmeclient, skills not required this dont need check
			
			if(errors.isEmpty()) {
				System.out.println(id);
				updateUser = IAMService.updateUser(user);
				
					if(updateUser == null) {
						//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						//return null;
						responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to update profile.");
						
					} else {
						//response.setStatus(HttpServletResponse.SC_OK);
						responseStatus = new Status(Status.Type.OK, "Successfully update profile.");
					}
			}else {
				responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to update profile. Invalid Register Object.");
				String listOfErrors = errors.stream().map(Object::toString)
                        .collect(Collectors.joining(", "));
				LOGGER.error(responseStatus.toString()+" "+listOfErrors);
			}
		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to update profile, Exception.");
			LOGGER.error(e.getMessage(), e);
		}
		resp.setData(updateUser);
		resp.setStatus(responseStatus);
		return resp;
    }
	
	@RequestMapping(value="/upload", method= RequestMethod.POST)
    public APIResponse uploadFile(@RequestParam(value = "imageFile") MultipartFile file) {
		String uploadstatus = null;
        APIResponse resp = new APIResponse();
		Status responseStatus = new Status(Status.Type.OK, "Account login success.");
		if(IAMService.validateFileName(file.getOriginalFilename())) {
			uploadstatus = IAMService.uploadFile(file);
			responseStatus = new Status(Status.Type.OK, "Successfully upload image.");
		}else {
			uploadstatus = "Failed";
			responseStatus = new Status(Status.Type.OK, "Failed upload image, invalid filename.");
		}
        
		
		resp.setData(uploadstatus);
		resp.setStatus(responseStatus);
		return resp;
    }
	
	@RequestMapping(value="/logout", method= RequestMethod.GET)
	public APIResponse logout(HttpServletResponse response,
			@RequestParam long userId) throws URISyntaxException {

		IAM updateUserIAM = null;
		APIResponse resp = new APIResponse();
		Status responseStatus = new Status(Status.Type.OK, "Account login success.");
		String resStr = "failed";
		System.out.println(userId);
		
		try {
			if(!IAMService.isId(String.valueOf(userId))){
				responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to logout. Invalid user Id.");
				LOGGER.error(responseStatus.toString());
			}else {
				System.out.println(userId);
				updateUserIAM = IAMService.updateUserIAM(userId);
					if(updateUserIAM == null) {
						//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						//return null;
						responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to logout.");
						
					} else {
						//response.setStatus(HttpServletResponse.SC_OK);
						responseStatus = new Status(Status.Type.OK, "Successfully logged out.");
						resStr = "logged out";
					}
			}
		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to logout, Exception.");
			LOGGER.error(e.getMessage(), e);
		}
		resp.setData(resStr);
		resp.setStatus(responseStatus);
		return resp;
	}
	
	@RequestMapping(value="/getOTP", method= RequestMethod.GET)
	public APIResponse getOTP(HttpServletResponse response,
			@RequestParam long userId) throws URISyntaxException {

		String getOTP = null;
		APIResponse resp = new APIResponse();
		Status responseStatus = new Status(Status.Type.OK, "Account login success.");
		String resStr = "failed";
		
		try {
			if(!IAMService.isId(String.valueOf(userId))){
				responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to get OTP. Invalid user Id.");
				LOGGER.error(responseStatus.toString());
			}else {
				System.out.println(userId);
				getOTP = IAMService.generateOneTimePassword(userId);
					if(getOTP.equalsIgnoreCase("Failed")) {
						//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						//return null;
						responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to get OTP.");
						
					} else {
						//response.setStatus(HttpServletResponse.SC_OK);
						responseStatus = new Status(Status.Type.OK, "Successfully get OTP.");
						resStr = "sent";
					}
			}
		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to get OTP, Exception.");
			LOGGER.error(e.getMessage(), e);
		}
		resp.setData(resStr);
		resp.setStatus(responseStatus);
		return resp;
	}
	
	@RequestMapping(value="/validateOTP", method= RequestMethod.GET)
	public APIResponse validateOTP(HttpServletResponse response,
			@RequestParam long userId, @RequestParam String inputOtp) throws URISyntaxException {

		String validateOTP = null;
		APIResponse resp = new APIResponse();
		Status responseStatus = new Status(Status.Type.OK, "Account login success.");
		
		try {
			if(!IAMService.isId(String.valueOf(userId))){
				responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to validate OTP. Invalid user Id.");
				LOGGER.error(responseStatus.toString());
				validateOTP = "Failed";
			}else {
				System.out.println(userId);
				validateOTP = IAMService.validateOTP(inputOtp, userId);
					if(validateOTP.equalsIgnoreCase("Failed")) {
						//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						//return null;
						responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to validate OTP.");
						
					} else if(validateOTP.equalsIgnoreCase("Expired")){
						responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to validate OTP, Expired.");
					} else {
						//response.setStatus(HttpServletResponse.SC_OK);
						responseStatus = new Status(Status.Type.OK, "Successfully validate OTP.");
					}
			}
		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to validate OTP, Exception.");
			LOGGER.error(e.getMessage(), e);
			validateOTP = "Failed";
		}
		resp.setData(validateOTP);
		resp.setStatus(responseStatus);
		return resp;
	}

//    @DeleteMapping("/delete/{fileName}")
//    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
//        return new ResponseEntity<>(productService.deleteFile(fileName), HttpStatus.OK);
//    }
	
	@RequestMapping(value="/registerLinkedInUser", method= RequestMethod.POST)
	public APIResponse registerLinkedInUser(HttpServletResponse response,
			@RequestBody LinkedInDTO linkedInDTO) throws URISyntaxException {

		IAM iam = null;
		APIResponse resp = new APIResponse();
		Status responseStatus = new Status(Status.Type.OK, "Account login success.");
		List<String> errors = new ArrayList<String>();
		
		try {
			if(IAMService.isBlank(linkedInDTO.getFirstName())) {
				errors.add("Invalid first name value");
			}
			if(IAMService.isBlank(linkedInDTO.getLastName())) {
				errors.add("Invalid first name value");
			}
			if(IAMService.isBlank(linkedInDTO.getLinkedInId())) {
				errors.add("Invalid LinkedIn Account");
			}
			if(errors.isEmpty()) {
				iam = IAMService.registerLinkedInUser(linkedInDTO);
					if(iam == null) {
						//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						//return null;
						responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to register LinkedIn User.");
						
					} else {
						//response.setStatus(HttpServletResponse.SC_OK);
						responseStatus = new Status(Status.Type.OK, "Successfully register User.");
					}
			} else {
				responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to register LinkedIn User. Invalid Register LinkedIn Object.");
				String listOfErrors = errors.stream().map(Object::toString)
                        .collect(Collectors.joining(", "));
				LOGGER.error(responseStatus.toString()+" "+listOfErrors);
			}
		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to register LinkedIn User, Exception.");
			LOGGER.error(e.getMessage(), e);
		}
		resp.setData(iam);
		resp.setStatus(responseStatus);
		return resp;
	}
	
	@RequestMapping(value="/linkedInLogin", method= RequestMethod.POST)
	public APIResponse linkedInLogin(HttpServletResponse response,
			@RequestBody LinkedInLoginDTO linkedInLoginDTO) throws URISyntaxException {

		LoginDTO login = null;
		APIResponse resp = new APIResponse();
		Status responseStatus = new Status(Status.Type.OK, "LinkedIn login success.");
		List<String> errors = new ArrayList<String>();
		try {
			// if(IAMService.isBlank(linkedInLoginDTO.getLinkedInId())) {
			// 	errors.add("Invalid linkedIn ID");
			// }
			
			if(errors.isEmpty()) {
				login = IAMService.linkedInLogin(linkedInLoginDTO);
					if(login.getLoginStatus()!=IAMConstants.LOGIN.STATUS_SUCCESS) {
						//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						//return null;
						responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to login with LinkedIn, status: "+login.getLoginStatus());
						
					} else {

						//response.setStatus(HttpServletResponse.SC_OK);
						responseStatus = new Status(Status.Type.OK, "Successfully logged in.");
					}
			}else {
				responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to login with LinkedIn. Invalid LinkedIn login Object.");
				String listOfErrors = errors.stream().map(Object::toString)
                        .collect(Collectors.joining(", "));
				LOGGER.error(responseStatus.toString()+" "+listOfErrors);
			}	
		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to login with LinkedIn, Exception.");
			LOGGER.error(e.getMessage(), e);
		}
		resp.setData(login);
		resp.setStatus(responseStatus);
		return resp;
	}
	
	@RequestMapping(value="/forgetPassword", method= RequestMethod.POST)
	public APIResponse forgetPassword(HttpServletResponse response,
			@RequestBody LoginDTO loginDTO) throws URISyntaxException {

		String updateUser = "failed";
		APIResponse resp = new APIResponse();
		Status responseStatus = new Status(Status.Type.OK, "login success.");
		try {
			if(IAMService.isEmailAdd(loginDTO.getEmail())) {
				responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to forget password. Invalid email.");
				LOGGER.error(responseStatus.toString());
			}else {
				IAM iamUpdated = IAMService.forgetPassword(loginDTO.getEmail());
				if(iamUpdated == null) {
					//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					//return null;
					responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to forget password.");
					
				} else {
					//response.setStatus(HttpServletResponse.SC_OK);
					responseStatus = new Status(Status.Type.OK, "Successfully forget password.");
					updateUser = "success";
				}
			}

		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to forget password, Exception.");
			LOGGER.error(e.getMessage(), e);
		}
		resp.setData(updateUser);
		resp.setStatus(responseStatus);
		return resp;
	}
	
	@RequestMapping(value="/getUsersToResetPassword", method= RequestMethod.GET)
	public APIResponse getUsersToResetPassword(HttpServletResponse response) throws URISyntaxException {

		
		List<IAM> usersToResetPassword = null;
		APIResponse resp = new APIResponse();
		Status responseStatus = new Status(Status.Type.OK, "Successfully get users to reset password.");
		
		try {
			usersToResetPassword = IAMService.getUsersToResetPassword();
		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to get user to reset password, Exception.");
			LOGGER.error(e.getMessage(), e);
		}
		resp.setData(usersToResetPassword);
		resp.setStatus(responseStatus);
		return resp;
	}
	
	@RequestMapping(value="/informResetPassword", method= RequestMethod.POST)
	public APIResponse informResetPassword(HttpServletResponse response,
			@RequestBody String email, @RequestBody long userId) throws URISyntaxException {

		String updateUser = "failed";
		APIResponse resp = new APIResponse();
		Status responseStatus = new Status(Status.Type.OK, "login success.");
		try {
			if(!IAMService.isEmailAdd(email)||!IAMService.isId(String.valueOf(userId))) {
				responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to reset password. Invalid email or id.");
				LOGGER.error(responseStatus.toString());
			}else {
				updateUser = IAMService.resetPassword(userId);
				if(updateUser.equalsIgnoreCase("failed")) {
					//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					//return null;
					responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to reset password..");
					updateUser = "failed";
				} else {
					//response.setStatus(HttpServletResponse.SC_OK);
					responseStatus = new Status(Status.Type.OK, "Successfully reset password..");
				}
			}

		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to reset password., Exception.");
			LOGGER.error(e.getMessage(), e);
		}
		resp.setData(updateUser);
		resp.setStatus(responseStatus);
		return resp;
	}
	
	@RequestMapping(value="/changePassword", method= RequestMethod.POST)
	public APIResponse changePassword(HttpServletResponse response,
			@RequestBody String email, @RequestBody String password) throws URISyntaxException {

		String updateUser = "failed";
		APIResponse resp = new APIResponse();
		Status responseStatus = new Status(Status.Type.OK, "login success.");
		try {
			if(!IAMService.isEmailAdd(email)) {
				responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to reset password. Invalid email or id.");
				LOGGER.error(responseStatus.toString());
			}else {
				updateUser = IAMService.changePassword(email, password);
				if(updateUser.equalsIgnoreCase("failed")) {
					//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					//return null;
					responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to reset password..");
					updateUser = "failed";
				} else {
					//response.setStatus(HttpServletResponse.SC_OK);
					responseStatus = new Status(Status.Type.OK, "Successfully reset password..");
				}
			}

		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to reset password., Exception.");
			LOGGER.error(e.getMessage(), e);
		}
		resp.setData(updateUser);
		resp.setStatus(responseStatus);
		return resp;
	}
}
