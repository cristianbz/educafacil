/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.service;

import java.util.List;
import java.util.stream.Collectors;

import ec.mileniumtech.educafacil.dao.MatriculaDao;
import ec.mileniumtech.educafacil.dao.PersonaDao;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoMapper;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.MatriculaDto;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.PersonaDto;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servicio para manejar la l&oacute;gica de personas y matr&iacute;culas.
 * <p>
 * Reemplaza los m&eacute;todos de persona/matr&iacute;cula que antes estaban en
 * {@link AdministracionService}. Incluye operaciones de consulta, creaci&oacute;n,
 * actualizaci&oacute;n y eliminaci&oacute;n de personas, as&iacute; como consulta de
 * matr&iacute;culas por estudiante.
 * </p>
 *
 * @author christian
 */
@Stateless
@LocalBean
public class PersonaService {

    private static final Logger log = LogManager.getLogger(PersonaService.class);

    @EJB
    private PersonaDao personaDao;

    @EJB
    private MatriculaDao matriculaDao;

    // =========================================================
    // Métodos de consulta
    // =========================================================

    /**
     * Busca una persona por cédula y correo y la devuelve como entidad.
     *
     * @param cedula documento de identidad
     * @param correo correo electrónico
     * @return la entidad Persona encontrada, o {@code null} si no existe
     */
    public Persona buscarPersonaPorCedulaCorreo(String cedula, String correo) {
        return personaDao.buscarPersonaPorCedulaCorreo(cedula, correo);
    }

    /**
     * Busca una persona por cédula y correo en formato DTO.
     */
    public PersonaDto buscarPersonaDto(String cedula, String correo) {
        Persona persona = personaDao.buscarPersonaPorCedulaCorreo(cedula, correo);
        return DtoMapper.entidadAPersonaDto(persona);
    }

    /**
     * Lista las matrículas de un estudiante en formato DTO.
     */
    public List<MatriculaDto> listarMatriculasEstudianteDto(int codigoEstudiante) {
        return matriculaDao.listaMatriculasEstudiante(codigoEstudiante).stream()
                .map(DtoMapper::entidadAMatriculaDto)
                .collect(Collectors.toList());
    }

    // =========================================================
    // Métodos de persistencia
    // =========================================================

    /**
     * Guarda una nueva persona desde un DTO.
     */
    public PersonaDto guardarPersona(PersonaDto personaDto) {
        Persona persona = DtoMapper.dtoAEntidadPersona(personaDto);
        personaDao.agregarPersona(persona);
        return DtoMapper.entidadAPersonaDto(persona);
    }

    /**
     * Actualiza una persona existente desde un DTO.
     */
    public PersonaDto actualizarPersona(PersonaDto personaDto) {
        Persona persona = DtoMapper.dtoAEntidadPersona(personaDto);
        personaDao.actualizarPersona(persona);
        return DtoMapper.entidadAPersonaDto(persona);
    }

    /**
     * Elimina una persona por su ID.
     */
    public void eliminarPersona(int id) {
        Persona persona = personaDao.buscarPersonaPorId(id);
        if (persona != null) {
            personaDao.remover(persona);
        }
    }
}
