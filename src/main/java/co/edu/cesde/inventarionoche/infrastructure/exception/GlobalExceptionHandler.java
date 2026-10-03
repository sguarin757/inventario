package co.edu.cesde.inventarionoche.infrastructure.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(ResourceNotFoundException ex, HttpServletRequest peticion) {
        return armarRespuesta(HttpStatus.NOT_FOUND, ex.getMessage(), peticion);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> datosMalos(ValidationException ex, HttpServletRequest peticion) {
        return armarRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), peticion);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> sinStock(InsufficientStockException ex, HttpServletRequest peticion) {
        return armarRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), peticion);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> repetido(DuplicateResourceException ex, HttpServletRequest peticion) {
        return armarRespuesta(HttpStatus.CONFLICT, ex.getMessage(), peticion);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> camposMalos(MethodArgumentNotValidException ex, HttpServletRequest peticion) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(campo -> campo.getField() + ": " + campo.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return armarRespuesta(HttpStatus.BAD_REQUEST, mensaje, peticion);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> jsonMalo(HttpMessageNotReadableException ex, HttpServletRequest peticion) {
        return armarRespuesta(HttpStatus.BAD_REQUEST, "Los datos que mandó están mal escritos", peticion);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> rutaNoExiste(NoResourceFoundException ex, HttpServletRequest peticion) {
        return armarRespuesta(HttpStatus.NOT_FOUND, "La ruta " + peticion.getRequestURI() + " no existe", peticion);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> metodoNoVa(HttpRequestMethodNotSupportedException ex, HttpServletRequest peticion) {
        return armarRespuesta(HttpStatus.METHOD_NOT_ALLOWED, "El método " + ex.getMethod() + " no se puede usar en esta ruta", peticion);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> errorCualquiera(Exception ex, HttpServletRequest peticion) {
        return armarRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Algo salió mal en el servidor: " + ex.getMessage(), peticion);
    }

    private ResponseEntity<ErrorResponse> armarRespuesta(HttpStatus status, String mensaje, HttpServletRequest peticion) {
        ErrorResponse respuesta = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensaje,
                peticion.getRequestURI());
        return ResponseEntity.status(status).body(respuesta);
    }
}
