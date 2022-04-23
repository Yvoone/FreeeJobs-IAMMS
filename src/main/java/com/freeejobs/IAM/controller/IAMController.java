package com.freeejobs.IAM.controller;

import java.io.Console;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import com.freeejobs.IAM.dto.LoginDTO;

@RestController
@RequestMapping(value="/iam")
@CrossOrigin
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
        String uploadstatus = IAMService.uploadFile(file);
        APIResponse resp = new APIResponse();
		Status responseStatus = new Status(Status.Type.OK, "Successfully upload image.");
		
		resp.setData(uploadstatus);
		resp.setStatus(responseStatus);
		return resp;
    }

//    @DeleteMapping("/delete/{fileName}")
//    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
//        return new ResponseEntity<>(productService.deleteFile(fileName), HttpStatus.OK);
//    }
}
