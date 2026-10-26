package datos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/**
 * Representa una candidatura a una oferta de empleo.
 * Equivale a una línea del archivo de texto.
 */
public class Oferta {

    /** Formato de fecha usado en el archivo y en los comandos: dd/MM/yyyy. */
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final long id;
    private final String titulo;
    private final String empresa;
    private final String estado;
    private final LocalDate fechaPostulacion;
    private final String enlace;
    private final String notas;

    /**
     * Crea una candidatura.
     *
     * @param id               identificador único
     * @param titulo           puesto al que se postula
     * @param empresa          nombre de la empresa
     * @param estado           pendiente, entrevista, rechazado o aceptado
     * @param fechaPostulacion fecha en que se envió la candidatura
     * @param enlace           URL de la oferta (puede estar vacío)
     * @param notas            notas personales (pueden estar vacías)
     */
    public Oferta(long id, String titulo, String empresa, String estado,
                  LocalDate fechaPostulacion, String enlace, String notas) {
        this.id = id;
        this.titulo = titulo;
        this.empresa = empresa;
        this.estado = estado;
        this.fechaPostulacion = fechaPostulacion;
        this.enlace = enlace;
        this.notas = notas;
    }

    /** @return identificador único */
    public long getId() {
        return id;
    }

    /** @return puesto al que se postula */
    public String getTitulo() {
        return titulo;
    }

    /** @return nombre de la empresa */
    public String getEmpresa() {
        return empresa;
    }

    /** @return estado de la candidatura */
    public String getEstado() {
        return estado;
    }

    /** @return fecha en que se envió la candidatura */
    public LocalDate getFechaPostulacion() {
        return fechaPostulacion;
    }

    /** @return URL de la oferta */
    public String getEnlace() {
        return enlace;
    }

    /** @return notas personales */
    public String getNotas() {
        return notas;
    }

    @Override
    public String toString() {
        return id + " | " + titulo + " | " + empresa + " | " + estado + " | "
                + fechaPostulacion.format(FORMATO_FECHA);
    }
}
