import React, { useState, useEffect, useCallback } from 'react';

// ─── Configuración ─────────────────────────────────────────────
const API_BASE = '/educafacil-web/api/v1/public';
const ESTADOS_CIVILES = [
  { codigo: 'EC01', nombre: 'Soltero/a' },
  { codigo: 'EC02', nombre: 'Casado/a' },
  { codigo: 'EC03', nombre: 'Divorciado/a' },
  { codigo: 'EC04', nombre: 'Viudo/a' },
  { codigo: 'EC05', nombre: 'Unión Libre' },
];
const CARGOS = [
  { codigo: 'CO01', nombre: 'Empleado Privado' },
  { codigo: 'CO02', nombre: 'Empleado Público' },
  { codigo: 'CO03', nombre: 'Independiente' },
  { codigo: 'CO04', nombre: 'Empresario' },
  { codigo: 'CO05', nombre: 'Desempleado' },
];
const NIVELES_ESTUDIO = [
  { codigo: 'NE01', nombre: 'Primaria' },
  { codigo: 'NE02', nombre: 'Secundaria' },
  { codigo: 'NE03', nombre: 'Universitario' },
  { codigo: 'NE04', nombre: 'Postgrado' },
];
const INGRESOS = [
  { codigo: 'IM01', nombre: 'Menos de $400' },
  { codigo: 'IM02', nombre: '$400 - $800' },
  { codigo: 'IM03', nombre: '$800 - $1,200' },
  { codigo: 'IM04', nombre: '$1,200 - $2,000' },
  { codigo: 'IM05', nombre: 'Más de $2,000' },
];

// ─── Estado inicial ────────────────────────────────────────────
const INITIAL_FORM = {
  documentoIdentidad: '',
  nombres: '',
  apellidos: '',
  telefonoMobil: '',
  telefonoCasa: '',
  correoElectronico: '',
  domicilio: '',
  estadoCivil: '',
  fechaNacimiento: '',
  provincia: '',
  ciudad: '',
  nacionalidad: 'Ecuatoriana',
  cargasFamiliares: false,
  cargoOcupa: '',
  nivelEstudio: '',
  ultimoCurso: '',
  direccionTrabajo: '',
  ingresosMensuales: '',
  telefonoTrabajo: '',
  ofertaCursoId: '',
  medioInformacion: '',
  paraQueCurso: '',
  motivacionCurso: '',
  facturacionEmpresa: false,
  empresaRuc: '',
  empresaRazonSocial: '',
  empresaDireccion: '',
  empresaTelefono: '',
};

export default function MatriculaForm() {
  const [step, setStep] = useState(1);               // 1=cedula, 2=formulario, 3=confirmacion
  const [form, setForm] = useState(INITIAL_FORM);
  const [cursos, setCursos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [matriculaCreada, setMatriculaCreada] = useState(null);

  // ─── 1. Cargar cursos al montar ─────────────────────────────
  useEffect(() => {
    fetch(`${API_BASE}/ofertas`)
      .then(res => res.json())
      .then(data => {
        if (data.success) setCursos(data.data);
        else setError('Error al cargar cursos');
      })
      .catch(() => setError('Error de conexión'));
  }, []);

  // ─── 2. Buscar persona por cédula ───────────────────────────
  const buscarPersona = useCallback(async () => {
    const cedula = form.documentoIdentidad.trim();
    if (cedula.length < 10) {
      setError('La cédula debe tener al menos 10 dígitos');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const res = await fetch(`${API_BASE}/personas/${cedula}`);
      const json = await res.json();

      if (res.status === 200 && json.success) {
        const p = json.data;
        setForm(prev => ({
          ...prev,
          nombres: p.nombres || '',
          apellidos: p.apellidos || '',
          telefonoMobil: p.telefonoMobil || '',
          telefonoCasa: p.telefonoCasa || '',
          correoElectronico: p.correoElectronico || '',
          domicilio: p.domicilio || '',
          estadoCivil: p.estadoCivil || '',
          fechaNacimiento: p.fechaNacimiento || '',
          provincia: p.provincia || '',
          ciudad: p.ciudad || '',
          nacionalidad: p.nacionalidad || 'Ecuatoriana',
          cargasFamiliares: p.cargasFamiliares || false,
          cargoOcupa: p.estudiante?.cargoOcupa || '',
          nivelEstudio: p.estudiante?.nivelEstudio || '',
          ultimoCurso: p.estudiante?.ultimoCurso || '',
          direccionTrabajo: p.estudiante?.direccionTrabajo || '',
          ingresosMensuales: p.estudiante?.ingresosMensuales || '',
          telefonoTrabajo: p.estudiante?.telefonoTrabajo || '',
        }));
        setStep(2);
      } else if (res.status === 404) {
        // No existe — formulario vacío
        setStep(2);
      } else {
        setError(json.message || 'Error al buscar persona');
      }
    } catch {
      setError('Error de conexión con el servidor');
    } finally {
      setLoading(false);
    }
  }, [form.documentoIdentidad]);

  // ─── 3. Manejar cambios en campos ───────────────────────────
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  // ─── 4. Enviar matrícula ────────────────────────────────────
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const res = await fetch(`${API_BASE}/matriculas`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...form,
          fechaNacimiento: form.fechaNacimiento
            ? new Date(form.fechaNacimiento).toISOString()
            : null,
          provincia: form.provincia ? Number(form.provincia) : null,
          ciudad: form.ciudad ? Number(form.ciudad) : null,
          ofertaCursoId: Number(form.ofertaCursoId),
        }),
      });

      const json = await res.json();

      if (res.status === 201 && json.success) {
        setMatriculaCreada(json.data);
        setStep(3);
      } else if (res.status === 409) {
        setError('Ya estás matriculado en este curso');
      } else if (res.status === 400) {
        const msg = json.details
          ? json.details.join(', ')
          : json.message;
        setError(msg);
      } else {
        setError(json.message || 'Error al crear matrícula');
      }
    } catch {
      setError('Error de conexión con el servidor');
    } finally {
      setLoading(false);
    }
  };

  // ─── Renderizado por paso ───────────────────────────────────
  return (
    <div className="matricula-container">
      <h1>Formulario de Matrícula</h1>

      {error && (
        <div className="alert alert-danger">
          <strong>Error:</strong> {error}
          <button onClick={() => setError(null)} className="btn-close">&times;</button>
        </div>
      )}

      {/* ─── PASO 1: Ingresar cédula ─────────────────────── */}
      {step === 1 && (
        <div className="step-cedula">
          <label htmlFor="cedula">Número de cédula / RUC</label>
          <div className="input-group">
            <input
              id="cedula"
              name="documentoIdentidad"
              type="text"
              maxLength={13}
              value={form.documentoIdentidad}
              onChange={handleChange}
              onKeyDown={(e) => e.key === 'Enter' && buscarPersona()}
              placeholder="Ingrese su cédula"
              autoFocus
            />
            <button onClick={buscarPersona} disabled={loading}>
              {loading ? 'Buscando...' : 'Buscar'}
            </button>
          </div>
          <p className="help-text">Ingrese su cédula para verificar si ya está registrado</p>
        </div>
      )}

      {/* ─── PASO 2: Formulario completo ─────────────────── */}
      {step === 2 && (
        <form onSubmit={handleSubmit} className="matricula-form">
          <fieldset>
            <legend>Datos Personales</legend>
            <div className="form-grid">
              <div className="form-group">
                <label htmlFor="nombres">Nombres *</label>
                <input id="nombres" name="nombres" value={form.nombres}
                       onChange={handleChange} required />
              </div>
              <div className="form-group">
                <label htmlFor="apellidos">Apellidos *</label>
                <input id="apellidos" name="apellidos" value={form.apellidos}
                       onChange={handleChange} required />
              </div>
              <div className="form-group">
                <label htmlFor="telefonoMobil">Celular *</label>
                <input id="telefonoMobil" name="telefonoMobil" value={form.telefonoMobil}
                       onChange={handleChange} required />
              </div>
              <div className="form-group">
                <label htmlFor="correoElectronico">Correo electrónico</label>
                <input id="correoElectronico" name="correoElectronico" type="email"
                       value={form.correoElectronico} onChange={handleChange} />
              </div>
              <div className="form-group full-width">
                <label htmlFor="domicilio">Dirección</label>
                <input id="domicilio" name="domicilio" value={form.domicilio}
                       onChange={handleChange} />
              </div>
              <div className="form-group">
                <label htmlFor="estadoCivil">Estado Civil</label>
                <select id="estadoCivil" name="estadoCivil" value={form.estadoCivil}
                        onChange={handleChange}>
                  <option value="">Seleccione...</option>
                  {ESTADOS_CIVILES.map(ec => (
                    <option key={ec.codigo} value={ec.codigo}>{ec.nombre}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label htmlFor="fechaNacimiento">Fecha de Nacimiento</label>
                <input id="fechaNacimiento" name="fechaNacimiento" type="date"
                       value={form.fechaNacimiento} onChange={handleChange} />
              </div>
            </div>
          </fieldset>

          <fieldset>
            <legend>Datos Académicos / Laborales</legend>
            <div className="form-grid">
              <div className="form-group">
                <label htmlFor="cargoOcupa">Cargo que ocupa</label>
                <select id="cargoOcupa" name="cargoOcupa" value={form.cargoOcupa}
                        onChange={handleChange}>
                  <option value="">Seleccione...</option>
                  {CARGOS.map(c => (
                    <option key={c.codigo} value={c.codigo}>{c.nombre}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label htmlFor="nivelEstudio">Nivel de Estudio</label>
                <select id="nivelEstudio" name="nivelEstudio" value={form.nivelEstudio}
                        onChange={handleChange}>
                  <option value="">Seleccione...</option>
                  {NIVELES_ESTUDIO.map(ne => (
                    <option key={ne.codigo} value={ne.codigo}>{ne.nombre}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label htmlFor="ingresosMensuales">Ingresos Mensuales</label>
                <select id="ingresosMensuales" name="ingresosMensuales"
                        value={form.ingresosMensuales} onChange={handleChange}>
                  <option value="">Seleccione...</option>
                  {INGRESOS.map(im => (
                    <option key={im.codigo} value={im.codigo}>{im.nombre}</option>
                  ))}
                </select>
              </div>
            </div>
          </fieldset>

          <fieldset>
            <legend>Curso</legend>
            <div className="form-grid">
              <div className="form-group full-width">
                <label htmlFor="ofertaCursoId">Curso *</label>
                <select id="ofertaCursoId" name="ofertaCursoId"
                        value={form.ofertaCursoId} onChange={handleChange} required>
                  <option value="">Seleccione un curso...</option>
                  {cursos.map(c => (
                    <option key={c.id} value={c.id}>
                      {c.curso?.nombre} — {new Date(c.fechaInicio).toLocaleDateString()}
                      {' '}(${c.valor}) {c.descuento > 0 ? `- ${c.descuento}% desc.` : ''}
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label htmlFor="medioInformacion">¿Cómo nos conoció?</label>
                <input id="medioInformacion" name="medioInformacion"
                       value={form.medioInformacion} onChange={handleChange} />
              </div>
              <div className="form-group">
                <label htmlFor="motivacionCurso">Motivación</label>
                <input id="motivacionCurso" name="motivacionCurso"
                       value={form.motivacionCurso} onChange={handleChange} />
              </div>
            </div>
          </fieldset>

          <fieldset>
            <legend>Facturación a Empresa</legend>
            <div className="form-group checkbox-group">
              <input id="facturacionEmpresa" name="facturacionEmpresa" type="checkbox"
                     checked={form.facturacionEmpresa} onChange={handleChange} />
              <label htmlFor="facturacionEmpresa">Facturar a empresa</label>
            </div>
            {form.facturacionEmpresa && (
              <div className="form-grid">
                <div className="form-group">
                  <label htmlFor="empresaRuc">RUC Empresa</label>
                  <input id="empresaRuc" name="empresaRuc" value={form.empresaRuc}
                         onChange={handleChange} />
                </div>
                <div className="form-group">
                  <label htmlFor="empresaRazonSocial">Razón Social</label>
                  <input id="empresaRazonSocial" name="empresaRazonSocial"
                         value={form.empresaRazonSocial} onChange={handleChange} />
                </div>
                <div className="form-group full-width">
                  <label htmlFor="empresaDireccion">Dirección Empresa</label>
                  <input id="empresaDireccion" name="empresaDireccion"
                         value={form.empresaDireccion} onChange={handleChange} />
                </div>
              </div>
            )}
          </fieldset>

          <div className="form-actions">
            <button type="button" className="btn-secondary"
                    onClick={() => setStep(1)}>
              ← Volver
            </button>
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? 'Enviando...' : 'Matricularme'}
            </button>
          </div>
        </form>
      )}

      {/* ─── PASO 3: Confirmación ────────────────────────── */}
      {step === 3 && matriculaCreada && (
        <div className="step-confirmacion">
          <div className="success-icon">✅</div>
          <h2>¡Matrícula exitosa!</h2>
          <p>Tu solicitud de matrícula ha sido registrada.</p>
          <ul>
            <li><strong>N° Matrícula:</strong> {matriculaCreada.id}</li>
            <li><strong>Estudiante:</strong> {matriculaCreada.nombres} {matriculaCreada.apellidos}</li>
            <li><strong>Curso:</strong> {matriculaCreada.curso}</li>
            <li><strong>Estado:</strong> {matriculaCreada.estado}</li>
          </ul>
          <button className="btn-primary" onClick={() => {
            setStep(1);
            setForm(INITIAL_FORM);
            setMatriculaCreada(null);
          }}>
            Nueva Matrícula
          </button>
        </div>
      )}
    </div>
  );
}