package org.microboy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microboy.constants.ExceptionConstants;
import org.microboy.dto.JobTitleDTO;
import org.microboy.entity.JobTitleEntity;

/**
 * @author Khanh Tran
 */
@ApplicationScoped
@Slf4j
@RequiredArgsConstructor
public class JobTitleServiceImpl implements JobTitleService {

    private final ObjectMapper objectMapper;

    /**
     * Creates a new job title.
     *
     * @param JobTitleDTO The immutable object containing job title details.
     * @return The created job title as a DTO.
     */
    @Override
    @Transactional
    public JobTitleDTO createJobTitle(JobTitleDTO JobTitleDTO) {
        if (JobTitleDTO == null) {
            throw new BadRequestException("Requested JobTitle is null");
        }

        JobTitleEntity jobTitle = objectMapper.convertValue(
            JobTitleDTO,
            JobTitleEntity.class
        );
        JobTitleEntity.persist(jobTitle);

        JobTitleDTO createdJobTitle = objectMapper.convertValue(
            jobTitle,
            JobTitleDTO.class
        );
        log.info("New job title created successfully: {}", createdJobTitle);

        return createdJobTitle;
    }

    /**
     * Updates an existing job title.
     *
     * @param jobTitleDTO The DTO object containing updated job title details.
     * @param id             The ID of the job title to be updated.
     * @return The updated job title as a DTO.
     * @throws EntityNotFoundException If the job title with the given ID is not found.
     */
    @Override
    @Transactional
    public JobTitleDTO updateJobTitle(JobTitleDTO jobTitleDTO, UUID id) {
        if (jobTitleDTO == null || id == null) {
            throw new BadRequestException(
                "Requested JobTitle is null or id not given"
            );
        }

        JobTitleEntity jobTitle = JobTitleEntity.findById(id);
        if (jobTitle == null) {
            log.error(
                "Could not find job title with given id to update: {}",
                id
            );
            throw new EntityNotFoundException(
                ExceptionConstants.JOB_TITLE_NOT_FOUND
            );
        }

        jobTitle.title = jobTitleDTO.getTitle();
        jobTitle.description = jobTitleDTO.getDescription();
        log.info("Job title updated successfully: {}", jobTitleDTO);
        return jobTitleDTO;
    }

    /**
     * Retrieves all job titles.
     *
     * @return A list of job titles as DTOs.
     */
    @Override
    public List<JobTitleDTO> findAllJobTitle() {
        List<JobTitleEntity> jobTitleEntities = JobTitleEntity.listAll();
        return jobTitleEntities
            .stream()
            .map(jobTitleEntity ->
                objectMapper.convertValue(jobTitleEntity, JobTitleDTO.class)
            )
            .collect(Collectors.toList());
    }

    /**
     * Retrieves a job title by its ID.
     *
     * @param id The ID of the job title to retrieve.
     * @return The job title as a DTO.
     * @throws EntityNotFoundException If the job title with the given ID is not found.
     */
    @Override
    public JobTitleDTO findJobTitleById(UUID id) {
        if (id == null) {
            throw new BadRequestException("id not given");
        }

        JobTitleEntity jobTitle = JobTitleEntity.findById(id);
        if (jobTitle == null) {
            log.error("Could not find job title with given id: {}", id);
            throw new EntityNotFoundException(
                ExceptionConstants.JOB_TITLE_NOT_FOUND
            );
        }

        return objectMapper.convertValue(jobTitle, JobTitleDTO.class);
    }

    /**
     * Deletes a job title by its ID.
     *
     * @param id The ID of the job title to delete.
     */
    @Override
    @Transactional
    public void deleteJobTitleById(UUID id) {
        if (id == null) {
            throw new BadRequestException("id not given");
        }

        JobTitleEntity jobTitle = JobTitleEntity.findById(id);
        if (jobTitle == null) {
            log.error(
                "Could not find job title with given id to delete: {}",
                id
            );
            throw new EntityNotFoundException(
                ExceptionConstants.JOB_TITLE_NOT_FOUND
            );
        }

        JobTitleEntity.deleteById(id);
        log.info("Job title with given id deleted successfully: {}", id);
    }
}
