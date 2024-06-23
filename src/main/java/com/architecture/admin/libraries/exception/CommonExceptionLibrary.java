package com.architecture.admin.libraries.exception;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/*****************************************************
 * 예외 처리 - 기본 제공
 ****************************************************/
@RestControllerAdvice
@ResponseStatus(HttpStatus.OK)
public class CommonExceptionLibrary {
    @ExceptionHandler(Exception.class)
    public String except(Exception e, Model model) {
        model.addAttribute("exception", e);
        return displayError(e.getMessage(), "9500");
    }

    @ExceptionHandler(BindException.class)
    public String exceptBind(BindException e) {
        final String sMESSAGE = "message";
        final String[] message = new String[1];
        JSONObject obj = new JSONObject();
        obj.put("result", false);

        e.getAllErrors().forEach(objectError -> {
            if (!obj.has(sMESSAGE) || obj.getString(sMESSAGE) == null) {
                message[0] = objectError.getDefaultMessage();
            }
        });

        return displayError(message[0], "9400");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404(NoHandlerFoundException e) {
        return displayError(e.getMessage(), "9404");
    }

    @ExceptionHandler(CustomException.class)
    public String customExceptionHandle(CustomException e) {
        return displayError(e.getCustomError().getMessage(), e.getCustomError().getCode());
    }

    private String displayError(String sMessage, String sCode) {
        JSONObject obj = new JSONObject();
        obj.put("result", false);
        obj.put("code", sCode);
        obj.put("message", sMessage);

        return obj.toString();
    }
}
