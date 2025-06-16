package com.jobscheduler.repository;

import com.jobscheduler.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByStatus(Job.JobStatus status);

    @Query("SELECT j FROM Job j WHERE j.scheduledTime <= :now AND j.status = 'PENDING'")
    List<Job> findJobsReadyForExecution(@Param("now") LocalDateTime now);

    @Query("SELECT j FROM Job j WHERE j.nextRun <= :now AND j.status = 'PENDING' AND j.repeatPattern != 'ONCE'")
    List<Job> findRecurringJobsReadyForExecution(@Param("now") LocalDateTime now);

    Page<Job> findByStatusOrderByCreatedAtDesc(Job.JobStatus status, Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.name LIKE %:name%")
    List<Job> findByNameContaining(@Param("name") String name);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.status = :status")
    long countByStatus(@Param("status") Job.JobStatus status);

    @Query("SELECT j FROM Job j WHERE j.createdAt >= :startDate AND j.createdAt <= :endDate")
    List<Job> findJobsCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
}