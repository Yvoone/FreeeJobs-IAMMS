package com.freeejobs.IAM.service;

import java.util.Date;
import java.util.List;

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

	public LoginDTO login(LoginDTO loginDTO) {

		IAM userCred = getIAMByEmail(loginDTO.getEmail());

		if (userCred == null) {
			loginDTO.setLoginStatus(0);
		}
		else {
			loginDTO.setLoginStatus(getLoginStatus(loginDTO.getPassword(), userCred.getPassword()));
		}

		if(loginDTO.getLoginStatus() == 1) {
			loginDTO.setUserId(userCred.getUserId());
		}

		return loginDTO;

	}

	public IAM registerUser(UserDTO userDTO) {
		User user = new User();
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setContactNo(userDTO.getContactNo());
		user.setGender(userDTO.getGender());
		user.setDOB(userDTO.getDOB());
		user.setProfessionalTitle(userDTO.getProfessionalTitle());
		user.setAboutMe(userDTO.getAboutMe());
		user.setSkills(userDTO.getSkills());

		long userId = registerUserProfile(user).getId();

		IAM iam = new IAM();

		iam.setEmail(userDTO.getEmail());
		iam.setPassword(userDTO.getPassword());
		iam.setUserId(userId);

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
			return 1;
		}
		else {
			return 0;
		}
	}
	
	public User updateUser(User user) {
		User oldUser = userRepository.findById(user.getId());
		user.setFirstName(user.getFirstName());
		user.setLastName(user.getLastName());
		user.setContactNo(user.getContactNo());
		user.setGender(oldUser.getGender());
		user.setDOB(user.getDOB());
		user.setProfessionalTitle(user.getProfessionalTitle());
		user.setAboutMe(user.getAboutMe());
		user.setSkills(user.getSkills());
		user.setDateCreated(oldUser.getDateCreated());
		user.setDateUpdated(new Date());
		return userRepository.save(user);
	}

}
