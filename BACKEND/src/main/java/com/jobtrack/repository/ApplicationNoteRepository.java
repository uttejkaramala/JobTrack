package com.jobtrack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtrack.entity.ApplicationNote;

public interface ApplicationNoteRepository extends JpaRepository<ApplicationNote, Long> {

	List<ApplicationNote> findByApplicationId(Long applicationId);
	
	Optional<ApplicationNote> findByIdAndApplicationUserId(
	        Long id,
	        Long userId);

}