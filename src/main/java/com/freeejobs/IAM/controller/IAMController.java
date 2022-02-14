package com.freeejobs.IAM.controller;

import java.io.Console;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freeejobs.IAM.model.IAM;
import com.freeejobs.IAM.service.IAMService;
import com.freeejobs.IAM.dto.UserDTO;

@RestController
@RequestMapping(value="/iam")
@CrossOrigin
public class IAMController {

	@Autowired
	private IAMService IAMService;

	@RequestMapping(value="/userProfile", method= RequestMethod.GET)
	public IAM getUserByUserId(HttpServletResponse response,
			@RequestParam long userId) throws URISyntaxException {

		IAM userProfile = null;

		try {
			System.out.println(userId);
			userProfile = IAMService.getUserByUserId(userId);
				if(userProfile == null) {
					System.out.println("null");
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return null;
				} else {
					response.setStatus(HttpServletResponse.SC_OK);
				}



		} catch (Exception e) {
			System.out.println(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
		return userProfile;
	}

	@RequestMapping(value="/registerUser", method= RequestMethod.POST)
	public IAM registerUser(HttpServletResponse response,
			@RequestBody UserDTO userDTO) throws URISyntaxException {

		IAM userProfile = null;

		try {
			System.out.println(userDTO.getFirstName());
			userProfile = IAMService.registerUser(userDTO);
				if(userProfile == null) {
					System.out.println("null");
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return null;
				} else {
					response.setStatus(HttpServletResponse.SC_OK);
				}



		} catch (Exception e) {
			System.out.println(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
		return userProfile;
	}

}
