package org.microboy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microboy.dto.TeamDTO;
import org.microboy.entity.EmployeeCoreEntity;
import org.microboy.entity.TeamEntity;
import org.microboy.entity.TeamMemberEntity;
import org.microboy.enums.TeamRole;

import java.util.List;
import java.util.UUID;

/**
 * @author khanh_tran
 * Service implementation for managing teams.
 */
@ApplicationScoped
@Slf4j
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

	private final ObjectMapper objectMapper;

	/**
	 * Creates a new team.
	 *
	 * @param TeamDTO the data transfer object of the team to be created
	 * @return the created TeamDTO
	 * @throws BadRequestException if the provided teamDTO is null
	 */
	@Override
	@Transactional
	public TeamDTO createTeam(TeamDTO TeamDTO) {
		if (TeamDTO == null) {
			throw new BadRequestException("requested data is null");
		}
		TeamEntity teamEntity = objectMapper.convertValue(TeamDTO, TeamEntity.class);
		TeamEntity.persist(teamEntity);

		TeamDTO createdTeam = objectMapper.convertValue(teamEntity, TeamDTO.class);
		log.info("Created team: {}", createdTeam);

		return createdTeam;
	}

	/**
	 * Updates an existing team.
	 *
	 * @param teamDTO the data transfer object of the team to be updated
	 * @return the updated TeamDTO
	 * @throws BadRequestException if the provided teamDTO or its ID is null
	 * @throws NotFoundException   if the team to be updated does not exist
	 */
	@Override
	@Transactional
	public TeamDTO updateTeam(TeamDTO teamDTO, UUID id) {
		if (teamDTO == null || id == null) {
			throw new BadRequestException("requested data is null");
		}

		TeamEntity existingTeam = TeamEntity.findById(id);
		if (existingTeam == null) {
			throw new NotFoundException("team not found");
		}

		existingTeam.name = teamDTO.getName();
		existingTeam.description = teamDTO.getDescription();
		existingTeam.departmentId = teamDTO.getDepartmentId();
		existingTeam.email = teamDTO.getEmail();
		existingTeam.phoneNumber = teamDTO.getPhoneNumber();
		existingTeam.location = teamDTO.getLocation();

		log.info("Updated team: {}", teamDTO);
		return teamDTO;
	}

	/**
	 * Deletes a team by its ID.
	 *
	 * @param id the ID of the team to be deleted
	 * @throws BadRequestException if the provided ID is null
	 * @throws NotFoundException   if the team to be deleted does not exist
	 */
	@Override
	@Transactional
	public void deleteTeamById(UUID id) {
		if (id == null) {
			throw new BadRequestException("requested id is null");
		}

		TeamEntity team = TeamEntity.findById(id);
		if (team == null) {
			throw new NotFoundException("team not found");
		}

		TeamEntity.deleteById(id);
		log.info("Deleted team: {}", id);
	}

	/**
	 * Retrieves a list of all teams.
	 *
	 * @return a list of TeamDTO objects
	 */
	@Override
	public List<TeamDTO> getTeams() {
		List<TeamEntity> teamEntities = TeamEntity.listAll();
		return teamEntities.stream().map(team -> objectMapper.convertValue(team, TeamDTO.class)).toList();
	}

	/**
	 * Retrieves a team by its ID.
	 *
	 * @param id the ID of the team to be retrieved
	 * @return the retrieved TeamDTO
	 * @throws BadRequestException if the provided ID is null
	 * @throws NotFoundException   if the team to be retrieved does not exist
	 */
	@Override
	public TeamDTO getTeamById(UUID id) {
		if (id == null) {
			throw new BadRequestException("requested id is null");
		}

		TeamEntity team = TeamEntity.findById(id);
		if (team == null) {
			throw new NotFoundException("team not found");
		}

		return objectMapper.convertValue(team, TeamDTO.class);
	}

	@Override
	public TeamDTO getTeamByEmployeeId(UUID employeeId) {
		if (employeeId == null) {
			throw new BadRequestException("requested id is null");
		}

		TeamMemberEntity teamMember = TeamMemberEntity.findById(employeeId);
		if (teamMember == null) {
			return null;
		}

		TeamEntity team = TeamEntity.findById(teamMember.teamId);
		if (team == null) {
			return null;
		}

		return objectMapper.convertValue(team, TeamDTO.class);
	}

	@Override
	@Transactional
	public void addEmployeeToTeam(UUID teamId, UUID employeeId, TeamRole teamRole) {
		if (employeeId == null || teamId == null) {
			throw new BadRequestException("requested id is null");
		}

		TeamEntity team = TeamEntity.findById(teamId);
		if (team == null) {
			throw new NotFoundException("team not found");
		}

		EmployeeCoreEntity employee = EmployeeCoreEntity.findById(employeeId);
		if (employee == null) {
			throw new NotFoundException("employee not found");
		}

		TeamMemberEntity teamMember = TeamMemberEntity.builder()
		                                              .employeeId(employeeId).teamId(teamId).role(teamRole).build();
		TeamMemberEntity.persist(teamMember);
		log.info(String.format("Added employee %s to team %s with role %s", employeeId, teamId, teamRole));
	}
}
