package cr.ac.una.web.template.util;

/**
 *
 * @author ccarranza
 */
public enum AnswerCode {
    CORRECTO(200),
    ERROR_ACCESO(403),
    ERROR_PERMISOS(401),
    ERROR_NOENCONTRADO(404),
    ERROR_CLIENTE(400),
    ERROR_INTERNO(500);

    private Integer value;

    private AnswerCode(Integer value) {
        this.setValue(value);
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
