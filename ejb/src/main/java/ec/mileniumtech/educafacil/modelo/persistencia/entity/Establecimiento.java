package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "establecimiento")
public class Establecimiento implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="establecimientoSeq", sequenceName="establecimiento_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="establecimientoSeq")
	@Column(name="esta_id")
	private Integer estaId;
   
	@Column(name="esta_nombre_comercial")
	private String estaNombreComercial;
	
	@Column(name="esta_ubicacion")
	private String estaUbicacion;
	
	@Column(name="esta_estado")
	private Boolean estaEstado;
	
    // Relación con la entidad Empresa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empm_id", referencedColumnName = "empm_id", 
                insertable = false, updatable = false)
    private EmpresaMatriz empresaMatriz;
    
	@OneToMany(mappedBy="establecimientos", fetch=FetchType.LAZY)
	private List<PuntoEmision> puntoEmisiones;

}
