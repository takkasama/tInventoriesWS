package io.github.takkasama.web.tinventoriesws.util;

/**
 *
 * @author ccarranza
 */
public enum AnswerCode {
    OK(200),
    FORBIDDEN(403),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    BAD_REQUEST(400),
    INTERNAL_SERVER_ERROR(500);

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