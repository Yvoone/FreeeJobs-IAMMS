package com.freeejobs.IAM.controller;

import java.io.Console;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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

	@Autowired
	private IAMService IAMService;

	@RequestMapping(value="/userProfile", method= RequestMethod.GET)
	public APIResponse getUserByUserId(HttpServletResponse response,
			@RequestParam long userId) throws URISyntaxException {

		User userProfile = null;
		APIResponse resp = new APIResponse();
		Status responseStatus = new Status(Status.Type.OK, "Account login success.");
		
		try {
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
			
				
			
		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to get user Profile, Exception.");
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
		
		try {
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
			
				
			
		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to register User, Exception.");
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
		
		try {
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
			
				
			
		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to register Admin, Exception.");
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
		
		try {
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
			
				
			
		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to login, Exception.");
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
			System.out.println(userId);
			userDto = IAMService.getUserProfileWithEmailByUserId(userId);
			
				if(userDto == null) {
					//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					//return null;
					responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to login.");
					
				} else {
					//response.setStatus(HttpServletResponse.SC_OK);
					responseStatus = new Status(Status.Type.OK, "Successfully logged in.");
				}
			
				
			
		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to login, Exception.");
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
		
		try {
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
			
				
			
		} catch (Exception e) {
			System.out.println(e);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			return null;
			responseStatus = new Status(Status.Type.INTERNAL_SERVER_ERROR, "Failed to update profile, Exception.");
		}
		resp.setData(updateUser);
		resp.setStatus(responseStatus);
		return resp;
    }
}
