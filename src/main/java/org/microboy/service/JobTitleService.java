package org.microboy.service;

import org.microboy.dto.JobTitleDTO;

import java.util.List;
import java.util.UUID;

public interface JobTitleService {

	JobTitleDTO createJobTitle(JobTitleDTO jobTitleDTO);
	JobTitleDTO updateJobTitle(JobTitleDTO jobTitleDTO, UUID id);
	List<JobTitleDTO> findAllJobTitle();
	JobTitleDTO findJobTitleById(UUID id);
	void deleteJobTitleById(UUID id);
}
