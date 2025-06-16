package com.jobscheduler.repository;

import com.jobscheduler.entity.Job;
import com.jobscheduler.entity.JobExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobExecutionRepository extends JpaRepository<JobExecution, Long> {

    List<JobExecution> findByJobOrderByStartedAtDesc(Job job);

    Page<JobExecution> findByJobOrderByStartedAtDesc(Job job, Pageable pageable);

    List<JobExecution> findByStatus(Job.JobStatus status);

    @Query("SELECT je FROM JobExecution je WHERE je.startedAt >= :startDate AND je.startedAt <= :endDate")
    List<JobExecution> findExecutionsBetween(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(je.durationMs) FROM JobExecution je WHERE je.job = :job AND je.status = 'COMPLETED'")
    Double findAverageExecutionTimeForJob(@Param("job") Job job);
}