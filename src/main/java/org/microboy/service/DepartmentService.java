package org.microboy.service;

import org.microboy.dto.DepartmentDTO;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {

    DepartmentDTO createDepartment(DepartmentDTO departmentDTO);

    List<DepartmentDTO> findAllDepartment();

    List<DepartmentDTO> findDepartmentByParentId(UUID parentId);

    DepartmentDTO findDepartmentById(UUID id);

    DepartmentDTO updateDepartment(DepartmentDTO departmentDTO, UUID id);

    void deleteDepartmentById(UUID id);

}
