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
import com.freeejobs.IAM.dto.UserDTO;
import com.freeejobs.IAM.dto.LoginDTO;

@RestController
@RequestMapping(value="/iam")
@CrossOrigin
public class IAMController {

	@Autowired
	private IAMService IAMService;

	@RequestMapping(value="/userProfile", method= RequestMethod.GET)
	public User getUserByUserId(HttpServletResponse response,
			@RequestParam long userId) throws URISyntaxException {

		User userProfile = null;

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

		IAM iam = null;

		try {
			System.out.println(userDTO.getFirstName());
			iam = IAMService.registerUser(userDTO);
				if(iam == null) {
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
		return iam;
	}

	@RequestMapping(value="/registerAdmin", method= RequestMethod.POST)
	public IAM registerAdmin(HttpServletResponse response,
			@RequestBody UserDTO userDTO) throws URISyntaxException {

		IAM iam = null;

		try {
			System.out.println(userDTO.getFirstName());
			iam = IAMService.registerAdmin(userDTO);
				if(iam == null) {
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
		return iam;
	}

	@RequestMapping(value="/login", method= RequestMethod.POST)
	public LoginDTO registerUser(HttpServletResponse response,
			@RequestBody LoginDTO loginDTO) throws URISyntaxException {

		LoginDTO login = null;

		try {
			System.out.println(loginDTO.getEmail());
			login = IAMService.login(loginDTO);
				if(login == null) {
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
		return login;
	}

	@PutMapping("/{id}/edit")
    public void updateUser(@PathVariable("id") Long id, @RequestBody User user) {
		IAMService.updateUser(user);
    }
}
