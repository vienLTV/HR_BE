package org.microboy.service;

import org.microboy.dto.CertificateDTO;

import java.util.List;
import java.util.UUID;

public interface CertificateService {

    CertificateDTO createCertificate(CertificateDTO certificateDTO);

    CertificateDTO updateCertificate(CertificateDTO certificateDTO, UUID id);

    List<CertificateDTO> findAllCertificate();

    List<CertificateDTO> findCertificateByEmployeeId(UUID employeeId);

    CertificateDTO findCertificateById(UUID id);

    void deleteCertificateById(UUID id);
}
