package com.spring.boot.security.jwt.example.demo.exceptions;

import com.spring.boot.security.jwt.example.demo.model.ErrorMessage;
import com.spring.boot.security.jwt.example.demo.model.users.IncorrectPasswordException;
import com.spring.boot.security.jwt.example.demo.model.users.RoleExistsException;
import com.spring.boot.security.jwt.example.demo.model.users.RoleNotFoundException;
import com.spring.boot.security.jwt.example.demo.model.users.UserExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            UsernameNotFoundException.class,
            UserExistsException.class,
            RoleNotFoundException.class,
            RoleExistsException.class,
            IncorrectPasswordException.class})
    public ResponseEntity<Object> handleBadRequestExceptions(Exception ex, WebRequest request) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .time(LocalDateTime.now())
                .path(request.getDescription(false))
                .message(ex.getLocalizedMessage())
                .build();
        return ResponseEntity.badRequest().body(errorMessage);
    }

}
