package com.jobscheduler.repository;

import com.jobscheduler.entity.BinaryJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BinaryJobRepository extends JpaRepository<BinaryJob, Long> {

    List<BinaryJob> findByOriginalFilename(String originalFilename);

    @Query("SELECT b FROM BinaryJob b WHERE b.minioBucket = :bucket")
    List<BinaryJob> findByMinioBucket(@Param("bucket") String bucket);

    @Query("SELECT b FROM BinaryJob b WHERE b.fileSize > :minSize")
    List<BinaryJob> findByFileSizeGreaterThan(@Param("minSize") Long minSize);
}