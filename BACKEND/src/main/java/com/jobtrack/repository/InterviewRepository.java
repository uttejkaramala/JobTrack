package com.jobtrack.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtrack.entity.Interview;

public interface InterviewRepository extends JpaRepository<Interview, Long>{

	List<Interview> findByApplicationId(Long applicationId);
	
    Optional<Interview> findByIdAndApplicationUserId(
            Long id,
            Long userId);
    
    List<Interview> findByApplicationUserIdAndInterviewDateAfterOrderByInterviewDateAsc(
	        Long userId,
	        LocalDateTime date);
}
