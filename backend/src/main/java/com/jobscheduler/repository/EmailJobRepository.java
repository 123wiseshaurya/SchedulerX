package com.jobscheduler.repository;

import com.jobscheduler.entity.EmailJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailJobRepository extends JpaRepository<EmailJob, Long> {

    @Query("SELECT e FROM EmailJob e JOIN e.recipients r WHERE r = :email")
    List<EmailJob> findByRecipientEmail(@Param("email") String email);

    List<EmailJob> findByTemplate(String template);

    @Query("SELECT e FROM EmailJob e WHERE e.subject LIKE %:keyword%")
    List<EmailJob> findBySubjectContaining(@Param("keyword") String keyword);

    @Query("SELECT e FROM EmailJob e WHERE SIZE(e.recipients) > :count")
    List<EmailJob> findByRecipientCountGreaterThan(@Param("count") int count);
}