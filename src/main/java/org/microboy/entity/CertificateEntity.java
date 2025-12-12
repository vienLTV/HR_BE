package org.microboy.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "certificate")
@Audited
@Getter
@Setter
public class CertificateEntity extends PanacheEntityBase {

    @Id
    @UuidGenerator
    @Column(name = "certificate_id")
    public UUID certificateId;

    @Column(name = "certificate_name")
    public String name;

    @Column(name = "description")
    public String description;

    @Column(name = "provider")
    public String provider;

    @Column(name = "level")
    public String level;

    @Column(name = "license_code")
    public String licenseCode;

    @Column(name = "issued_date")
    public LocalDate issuedDate;

    @Column(name = "expired_date")
    public LocalDate expiredDate;

    @Column(name = "certificate_img")
    public String certificateImg;

    @Column(name = "employee_id")
    public UUID employeeId;

    public static List<CertificateEntity> findAllByEmployeeId(UUID employeeId) {
        return find("employeeId", employeeId).list();
    }
}
