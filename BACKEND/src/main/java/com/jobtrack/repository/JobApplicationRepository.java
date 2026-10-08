package com.jobtrack.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jobtrack.entity.ApplicationStatus;
import com.jobtrack.entity.JobApplication;

public interface JobApplicationRepository
		extends JpaRepository<JobApplication, Long>, JpaSpecificationExecutor<JobApplication> {

	Page<JobApplication> findByUserId(Long userId, Pageable pageable);

	Optional<JobApplication> findByIdAndUserId(Long id, Long userId);

	List<JobApplication> findByUserIdAndNextFollowUpDateLessThanEqual(Long userId, LocalDate date);

	// methods for follow ups
	List<JobApplication> findByUserIdAndNextFollowUpDateLessThan(Long userId, LocalDate date);

	List<JobApplication> findByUserIdAndNextFollowUpDate(Long userId, LocalDate date);

	List<JobApplication> findByUserIdAndNextFollowUpDateGreaterThan(Long userId, LocalDate date);

	// methods for dashboard

	long countByUserId(Long userId);

	long countByUserIdAndStatus(Long userId, ApplicationStatus status);

	long countByUserIdAndNextFollowUpDateBefore(Long userId, LocalDate date);

	long countByUserIdAndNextFollowUpDate(Long userId, LocalDate date);

	long countByUserIdAndNextFollowUpDateAfter(Long userId, LocalDate date);

	@Query("""
			    SELECT a.source, COUNT(a)
			    FROM JobApplication a
			    WHERE a.user.id = :userId
			    GROUP BY a.source
			""")
	List<Object[]> countApplicationsBySource(@Param("userId") Long userId);

	@Query("""
			    SELECT a.workMode, COUNT(a)
			    FROM JobApplication a
			    WHERE a.user.id = :userId
			    GROUP BY a.workMode
			""")
	List<Object[]> countApplicationsByWorkMode(@Param("userId") Long userId);

	@Query("""
			    SELECT a.employmentType, COUNT(a)
			    FROM JobApplication a
			    WHERE a.user.id = :userId
			    GROUP BY a.employmentType
			""")
	List<Object[]> countApplicationsByEmploymentType(@Param("userId") Long userId);
}