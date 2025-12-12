package org.microboy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microboy.dto.TeamMemberDTO;
import org.microboy.entity.EmployeeCoreEntity;
import org.microboy.entity.TeamEntity;
import org.microboy.entity.TeamMemberEntity;
import org.microboy.entity.TeamMemberId;

import java.util.List;
import java.util.UUID;

/**
 * @author khanh_tran
 * Service implementation for managing team members.
 */
@RequestScoped
@Slf4j
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {

	private final ObjectMapper objectMapper;

	/**
	 * Creates a new team member.
	 *
	 * @param teamMemberDTO the team member DTO
	 * @return the created team member DTO
	 * @throws BadRequestException if the teamMemberDTO is null
	 */
	@Override
	@Transactional
	public TeamMemberDTO create(TeamMemberDTO teamMemberDTO) {
		validateRequestedTeamMember(teamMemberDTO);

		TeamMemberEntity teamMemberEntity = TeamMemberEntity.builder()
		                                                    .employeeId(teamMemberDTO.getEmployeeId())
		                                                    .teamId(teamMemberDTO.getTeamId())
		                                                    .role(teamMemberDTO.getRole())
		                                                    .build();

		TeamMemberEntity.persist(teamMemberEntity);
		log.info("Team Member Created: {}", teamMemberDTO);

		return teamMemberDTO;
	}

	/**
	 * Updates an existing team member.
	 *
	 * @param teamMemberDTO the team member DTO
	 * @return the updated team member DTO
	 * @throws BadRequestException if the teamMemberDTO is null
	 * @throws NotFoundException   if the team member does not exist
	 */
	@Override
	@Transactional
	public TeamMemberDTO update(TeamMemberDTO teamMemberDTO) {
		validateRequestedTeamMember(teamMemberDTO);

		TeamMemberId teamMemberId = new TeamMemberId(teamMemberDTO.getTeamId(), teamMemberDTO.getEmployeeId());
		TeamMemberEntity existingTeamMemberEntity = TeamMemberEntity.findById(teamMemberId);
		if (existingTeamMemberEntity == null) {
			throw new NotFoundException("Team Member Not Found");
		}

		existingTeamMemberEntity.role = teamMemberDTO.getRole();
		log.info("Team Member Updated: {}", teamMemberDTO);

		return teamMemberDTO;
	}

	private void validateRequestedTeamMember(TeamMemberDTO teamMemberDTO) {
		if (teamMemberDTO == null) {
			throw new BadRequestException("teamMemberDTO is null");
		}

		if (teamMemberDTO.getTeamId() == null || teamMemberDTO.getEmployeeId() == null) {
			throw new BadRequestException("teamId or employeeId is null");
		}

		TeamEntity existingTeam = TeamEntity.findById(teamMemberDTO.getTeamId());
		if (existingTeam == null) {
			throw new NotFoundException("team not found");
		}

		EmployeeCoreEntity existingEmployee = EmployeeCoreEntity.findById(teamMemberDTO.getEmployeeId());
		if (existingEmployee == null) {
			throw new NotFoundException("employee not found");
		}
	}

	/**
	 * Deletes a team member by ID.
	 *
	 * @param teamId && employeeId the team member ID
	 * @throws BadRequestException if the team member ID is null
	 * @throws NotFoundException   if the team member does not exist
	 */
	@Override
	@Transactional
	public void deleteById(UUID teamId, UUID employeeId) {
		if (teamId == null || employeeId == null) {
			throw new BadRequestException("team member id is null");
		}

		TeamMemberId teamMemberId = new TeamMemberId(teamId, employeeId);
		TeamMemberEntity existingTeamMemberEntity = TeamMemberEntity.findById(teamId);
		if (existingTeamMemberEntity == null) {
			throw new NotFoundException("Team Member Not Found");
		}

		TeamMemberEntity.deleteById(teamMemberId);
		log.info("Team Member Deleted: {}", teamMemberId);
	}

	/**
	 * Finds a team member by ID.
	 *
	 * @param teamId && employeeId the team member ID
	 * @return the found team member DTO
	 * @throws BadRequestException if the team member ID is null
	 * @throws NotFoundException   if the team member does not exist
	 */
	@Override
	public TeamMemberDTO findById(UUID teamId, UUID employeeId) {
		if (teamId == null || employeeId == null) {
			throw new BadRequestException("team member id is null");
		}

		TeamMemberId teamMemberId = new TeamMemberId(teamId, employeeId);
		TeamMemberEntity teamMemberEntity = TeamMemberEntity.findById(teamMemberId);
		if (teamMemberEntity == null) {
			throw new NotFoundException("Team Member Not Found");
		}

		return objectMapper.convertValue(teamMemberEntity, TeamMemberDTO.class);
	}

	/**
	 * Finds all team members.
	 *
	 * @return the list of team member DTOs
	 */
	@Override
	public List<TeamMemberDTO> findAll() {
		List<TeamMemberEntity> teamMemberEntities = TeamMemberEntity.listAll();
		return teamMemberEntities.stream()
		                         .map(teamMember -> objectMapper.convertValue(teamMember, TeamMemberDTO.class))
		                         .toList();
	}

	@Override
	public List<TeamMemberDTO> findByEmployeeId(UUID employeeId) {
		return List.of();
	}
}
