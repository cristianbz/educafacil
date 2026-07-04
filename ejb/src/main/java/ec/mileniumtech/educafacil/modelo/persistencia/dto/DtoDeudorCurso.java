package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DtoDeudorCurso implements Serializable {

	private static final long serialVersionUID = 1L;
	private String apellidos;
	private String nombres;
	private String cedula;
	private double valorCurso;
	private double totalPagado;
	private double deuda;
}
