package com.freeejobs.IAM.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.freeejobs.IAM.model.IAM;
import com.freeejobs.IAM.model.User;

@Repository
public interface IAMRepository extends JpaRepository<IAM, Long> {
	public IAM findById(long id);
	public IAM findByEmail(String email);
	public List<IAM> findAll();
	public IAM findByUserId(long id);
	public IAM findByLinkedInId(String id);
	
	@Query("select t from IAM t where t.resetPwInd = ?1")
	public List<IAM> findUsersWithResetInd(int resetPwInd);
}
