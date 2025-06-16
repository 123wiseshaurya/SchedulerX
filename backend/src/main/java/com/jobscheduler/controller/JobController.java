package com.jobscheduler.controller;

import com.jobscheduler.dto.BinaryJobRequest;
import com.jobscheduler.dto.EmailJobRequest;
import com.jobscheduler.dto.JobResponse;
import com.jobscheduler.entity.Job;
import com.jobscheduler.service.JobService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobController {

    @Autowired
    private JobService jobService;

    @PostMapping("/binary")
    public ResponseEntity<JobResponse> createBinaryJob(@Valid @RequestBody BinaryJobRequest request) {
        JobResponse response = jobService.createBinaryJob(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/email")
    public ResponseEntity<JobResponse> createEmailJob(@Valid @RequestBody EmailJobRequest request) {
        JobResponse response = jobService.createEmailJob(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllJobs() {
        List<JobResponse> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<JobResponse>> getJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<JobResponse> jobs = jobService.getJobs(pageable);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
        Optional<JobResponse> job = jobService.getJobById(id);
        return job.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<JobResponse>> getJobsByStatus(@PathVariable Job.JobStatus status) {
        List<JobResponse> jobs = jobService.getJobsByStatus(status);
        return ResponseEntity.ok(jobs);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<JobResponse> updateJobStatus(@PathVariable Long id, 
                                                      @RequestParam Job.JobStatus status) {
        try {
            JobResponse response = jobService.updateJobStatus(id, status);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<JobResponse> executeJobNow(@PathVariable Long id) {
        try {
            JobResponse response = jobService.executeJobNow(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        try {
            jobService.deleteJob(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<JobService.JobStatistics> getJobStatistics() {
        JobService.JobStatistics stats = jobService.getJobStatistics();
        return ResponseEntity.ok(stats);
    }
}