package com.freeejobs.IAM.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.freeejobs.IAM.model.User;
import com.freeejobs.IAM.model.UserAudit;

@Repository
public interface UserAuditRepository extends JpaRepository<UserAudit, Long> {

}
