package com.freeejobs.IAM.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freeejobs.IAM.model.IAM;
import com.freeejobs.IAM.repository.IAMRepository;
import com.freeejobs.IAM.dto.UserDTO;

@Service
public class IAMService {

	private static final Logger LOGGER = LogManager.getLogger(IAMService.class);

	@Autowired
	private IAMRepository iamRepository;

	public IAM getUserByUserId(long id) {
		return iamRepository.findById(id);
	}

	public IAM registerUser(UserDTO userDTO) {
		IAM user = new IAM();
		user.setPassword(userDTO.getPassword());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setEmail(userDTO.getEmail());
		user.setContactNo(userDTO.getContactNo());
		user.setGender(userDTO.getGender());
		user.setDOB(userDTO.getDOB());
		user.setProfessionalTitle(userDTO.getProfessionalTitle());
		user.setAboutMe(userDTO.getAboutMe());
		user.setSkills(userDTO.getSkills());

		return iamRepository.save(user);
	}
}
