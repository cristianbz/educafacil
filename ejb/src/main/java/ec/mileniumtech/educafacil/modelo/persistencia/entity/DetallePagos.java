package ec.mileniumtech.educafacil.modelo.persistencia.entity;
import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
/**
 * Entity implementation class for Entity: DetallePagos
 *
 */
@Entity
@Table(name = "detallepagos")
@Getter
@Setter
public class DetallePagos implements Serializable {

	
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="detallePagosSeq", sequenceName="detalle_pagos_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="cursoSeq")
	@Column(name="depa_id")
	private Integer depaId;
	
	@Column(name="depa_valor")
	private Double depaValor;
	
	@Column(name="depa_observacion")
	private String depaObservacion;
	
	@Column(name="depa_forma_pago")
	private String depaFormaPago;
	
	@Column(name="depa_usuario_inserto")
	private String depaUsuarioInserto;
	
	@Column(name="depa_usuario_actualizo")
	private String depaUsuarioActualizo;
	
	@Column(name="depa_fecha_inserto")
	private Date depaFechaInserto;
	
	@Column(name="depa_fecha_actualizo")
	private Date depaFechaActualizo;
	
	@Column(name="depa_estado")
	private boolean depaEstado;
   
	@ManyToOne
	@JoinColumn(name="pago_id")
	private Pagos pagos;
	
	@ManyToOne
	@JoinColumn(name="cata_id")
	private Catalogo catalogo;
}
