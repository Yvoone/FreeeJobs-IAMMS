package com.freeejobs.IAM.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.freeejobs.IAM.model.IAM;
import com.freeejobs.IAM.model.IAMAudit;

@Repository
public interface IAMAuditRepository extends JpaRepository<IAMAudit, Long> {

}
