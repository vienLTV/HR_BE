package org.microboy.service;

import org.microboy.dto.TeamMemberDTO;

import java.util.List;
import java.util.UUID;

public interface TeamMemberService {

    TeamMemberDTO create(TeamMemberDTO teamMemberDTO);
    TeamMemberDTO update(TeamMemberDTO teamMemberDTO);
    void deleteById(UUID teamId, UUID employeeId);
    TeamMemberDTO findById(UUID teamId, UUID employeeId);
    List<TeamMemberDTO> findAll();
    List<TeamMemberDTO> findByEmployeeId(UUID employeeId);
}
