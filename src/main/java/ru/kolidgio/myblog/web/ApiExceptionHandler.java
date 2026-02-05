package ru.kolidgio.myblog.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.kolidgio.myblog.service.errors.BadRequestException;
import ru.kolidgio.myblog.service.errors.ConflictException;
import ru.kolidgio.myblog.service.errors.NotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequest(BadRequestException ex, HttpServletRequest req) {
        return problem(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return problem(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
    }
    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleComflict(ConflictException ex, HttpServletRequest req) {
        return problem(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
    }

    private static ProblemDetail problem(HttpStatus status, String detail, String path) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(status.getReasonPhrase());
        pd.setType(java.net.URI.create("about:blank"));
        pd.setInstance(java.net.URI.create(path));
        return pd;
    }
}
