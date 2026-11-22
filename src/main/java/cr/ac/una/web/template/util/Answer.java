/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cr.ac.una.web.template.util;

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
    
    public Answer(Boolean estado, AnswerCode answerCode, String message, String internalMessage, String name, Object result) {
        this.state = estado;
        this.answerCode = answerCode;
        this.message = message;
        this.internalMessage = internalMessage;
        this.result = new HashMap<>();
        this.result.put(name, result);
    }

    public Answer(Boolean estado, AnswerCode answerCode, String message ,String internalMessage, Object result) {
        this.state = estado;
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
    
    public Object getResultado(String name) {
        return result.get(name);
    }

    public void setResultado(String name, Object result) {
        this.result.put(name, result);
    }
    
    public Object getResultado() {
        return result.get("[Object]");
    }

    public void setResultado(Object result) {
        this.result.put("[Object]", result);
    }
}
