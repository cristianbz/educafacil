package ec.mileniumtech.educafacil.modelo.persistencia.entity;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
@Entity
@Getter
@Setter
@Table(name="tipoencuestapregunta")
@NamedQueries({
	@NamedQuery(name = TipoEncuestaPregunta.CARGAR_TIPO_ENCUESTA, query = "SELECT T FROM TipoEncuestaPregunta T ORDER BY T.teprId"),
	@NamedQuery(name = TipoEncuestaPregunta.CARGAR_PREGUNTA, query = "SELECT P FROM TipoEncuestaPregunta P WHERE P.pregunta.pregId = :codigo ORDER BY P.tipoEncuesta"),
	@NamedQuery(name = TipoEncuestaPregunta.CARGAR_POR_TIPO_ENCUESTA, query = "SELECT TEP FROM TipoEncuestaPregunta TEP WHERE TEP.tipoEncuesta.tipeId= :codigo AND TEP.teprEstado = TRUE"),
	@NamedQuery(name = TipoEncuestaPregunta.CARGAR_ENCUESTAS, query = "SELECT  TEP  FROM TipoEncuestaPregunta TEP WHERE TEP.tipoEncuesta.tipeId =:codigoTipo AND TEP.tipoEncuesta.tipeEstado = TRUE AND TEP.teprEstado = TRUE")
})

public class TipoEncuestaPregunta implements Serializable {
	
		
		private static final long serialVersionUID = 1L;
		public static final String CARGAR_TIPO_ENCUESTA="cargartipoencuesta";
		public static final String CARGAR_PREGUNTA="cargarpregunta";
		public static final String CARGAR_POR_TIPO_ENCUESTA="cargarPorTipoEncuesta";
		public static final String CARGAR_ENCUESTAS="cargarEncuestas";
		
		
		@Id
		@SequenceGenerator(name="tipoencuestapreguntaseq", sequenceName="tipoencuestapregunta_seq", allocationSize = 1)
	    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="tipoencuestapreguntaseq")
		@Column(name="tepr_id")
		private Integer teprId;
		
		@ManyToOne
		@JoinColumn(name="tipe_id",updatable = true, insertable = true)
		private TipoEncuesta tipoEncuesta;


		@ManyToOne
		@JoinColumn(name="preg_id",updatable = true, insertable = true)
		private Pregunta pregunta;

		@Column(name="tepr_estado")
		private boolean teprEstado;
		
	}
