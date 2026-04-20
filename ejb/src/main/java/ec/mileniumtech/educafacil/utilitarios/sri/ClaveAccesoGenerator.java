package ec.mileniumtech.educafacil.utilitarios.sri;

import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

/**
 * Utilidad para generar la clave de acceso de 49 dígitos requerida por el SRI.
 */
@Stateless
@LocalBean
public class ClaveAccesoGenerator {

    /**
     * Genera la clave de acceso de 49 dígitos.
     * 
     * @param fecha           Fecha de emisión (Date)
     * @param tipoComprobante Tipo de comprobante (e.g., "01" para factura)
     * @param ruc             RUC del emisor (13 dígitos)
     * @param ambiente        Ambiente (1 para Pruebas, 2 para Producción)
     * @param serie           Serie (Establecimiento + Punto de Emisión, 6 dígitos)
     * @param secuencial      Número secuencial (9 dígitos)
     * @param codigoNumerico  Código numérico (8 dígitos)
     * @param tipoEmision     Tipo de emisión (1 para Normal)
     * @return Clave de acceso de 49 dígitos
     */
    public String generarClaveAcceso(Date fecha, String tipoComprobante, String ruc, String ambiente,
            String serie, String secuencial, String codigoNumerico, String tipoEmision) {
        
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        String fechaFormateada = sdf.format(fecha);

        StringBuilder clave = new StringBuilder();
        clave.append(fechaFormateada);
        clave.append(tipoComprobante);
        clave.append(ruc);
        clave.append(ambiente);
        clave.append(serie);
        clave.append(secuencial);
        clave.append(codigoNumerico);
        clave.append(tipoEmision);

        // Calcular dígito verificador módulo 11
        int digitoVerificador = calcularModulo11(clave.toString());
        clave.append(digitoVerificador);

        return clave.toString();
    }

    /**
     * Calcula el dígito verificador usando el algoritmo módulo 11.
     * 
     * @param clave Clave de 48 dígitos
     * @return Dígito verificador (0-9)
     */
    private int calcularModulo11(String clave) {
        int suma = 0;
        int factor = 2;

        for (int i = clave.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(clave.charAt(i));
            suma += digito * factor;
            factor++;
            if (factor > 7) {
                factor = 2;
            }
        }

        int residuo = suma % 11;
        int verificador = 11 - residuo;

        if (verificador == 11) {
            verificador = 0;
        } else if (verificador == 10) {
            verificador = 1;
        }

        return verificador;
    }
}
