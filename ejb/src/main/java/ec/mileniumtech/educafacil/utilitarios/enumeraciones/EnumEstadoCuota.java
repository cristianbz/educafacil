package ec.mileniumtech.educafacil.utilitarios.enumeraciones;

import lombok.Getter;

public enum EnumEstadoCuota {
    PENDIENTE("CUOTA01", "PENDIENTE"),
    PAGADO("CUOTA02", "PAGADO"),
    VENCIDO("CUOTA03", "VENCIDO");

    @Getter
    private final String codigo;
    @Getter
    private final String label;

    private EnumEstadoCuota(String codigo, String label) {
        this.codigo = codigo;
        this.label = label;
    }

    public static EnumEstadoCuota[] listaValores() {
        return values();
    }
}
