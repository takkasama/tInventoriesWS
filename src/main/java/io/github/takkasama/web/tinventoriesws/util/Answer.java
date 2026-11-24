package io.github.takkasama.web.tinventoriesws.util;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author ccarranza
 */
public class Answer implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean state;
    private AnswerCode answerCode;
    private String message;
    private String internalMessage;
    private HashMap<String, Object> result;

    public Answer() {
        this.result = new HashMap<>();
    }

    public Answer(Boolean state, AnswerCode answerCode, String message, String internalMessage) {
        this.state = state;
        this.answerCode = answerCode;
        this.message = message;
        this.internalMessage = internalMessage;
        this.result = new HashMap<>();
    }

    public Answer(Boolean state, AnswerCode answerCode, String message, String internalMessage, String name, Object result) {
        this.state = state;
        this.answerCode = answerCode;
        this.message = message;
        this.internalMessage = internalMessage;
        this.result = new HashMap<>();
        this.result.put(name, result);
    }

    public Answer(Boolean state, AnswerCode answerCode, String message, String internalMessage, Object result) {
        this.state = state;
        this.answerCode = answerCode;
        this.message = message;
        this.internalMessage = internalMessage;
        this.result = new HashMap<>();
        this.result.put("[Object]", result);
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public AnswerCode getAnswerCode() {
        return answerCode;
    }

    public void setAnswerCode(AnswerCode answerCode) {
        this.answerCode = answerCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getInternalMessage() {
        return internalMessage;
    }

    public void setInternalMessage(String internalMessage) {
        this.internalMessage = internalMessage;
    }

    public Object getResult(String name) {
        return result.get(name);
    }

    public void setResult(String name, Object result) {
        this.result.put(name, result);
    }

    public Object getResult() {
        return result.get("[Object]");
    }

    public void setResult(Object result) {
        this.result.put("[Object]", result);
    }
}
