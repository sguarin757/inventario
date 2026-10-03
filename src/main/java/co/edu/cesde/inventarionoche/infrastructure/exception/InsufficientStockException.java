package co.edu.cesde.inventarionoche.infrastructure.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String mensaje) {
        super(mensaje);
    }
}
