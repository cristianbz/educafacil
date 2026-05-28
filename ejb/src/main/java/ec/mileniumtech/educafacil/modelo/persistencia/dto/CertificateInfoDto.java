package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class CertificateInfoDto {
	private LocalDate issueDate;
    private LocalDate expirationDate;
    private String subject;
    private String issuer;
    private String serialNumber;
    private String signatureAlgorithm;
    private Integer version;
    private Boolean isValid;
    private Boolean isExpired;
}
