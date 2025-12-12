package org.microboy.service;

import org.microboy.dto.TeamDTO;
import org.microboy.enums.TeamRole;

import java.util.List;
import java.util.UUID;

public interface TeamService {

    TeamDTO createTeam(TeamDTO teamDTO);
    TeamDTO updateTeam(TeamDTO teamDTO, UUID id);
    void deleteTeamById(UUID id);
    List<TeamDTO> getTeams();
    TeamDTO getTeamById(UUID id);
    TeamDTO getTeamByEmployeeId(UUID employeeId);
    void addEmployeeToTeam(UUID teamId, UUID employeeId, TeamRole teamRole);

}
