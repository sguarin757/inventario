package co.edu.cesde.inventarionoche.application.service.impl;

import co.edu.cesde.inventarionoche.application.service.MovimientoService;
import co.edu.cesde.inventarionoche.domain.model.Movimiento;
import co.edu.cesde.inventarionoche.domain.model.Producto;
import co.edu.cesde.inventarionoche.domain.model.TipoMovimiento;
import co.edu.cesde.inventarionoche.infrastructure.exception.InsufficientStockException;
import co.edu.cesde.inventarionoche.infrastructure.exception.ResourceNotFoundException;
import co.edu.cesde.inventarionoche.infrastructure.exception.ValidationException;
import co.edu.cesde.inventarionoche.infrastructure.repository.MovimientoRepository;
import co.edu.cesde.inventarionoche.infrastructure.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepo;
    private final ProductoRepository productoRepo;

    @Override
    @Transactional
    public Movimiento save(Movimiento movimiento) {
        Producto producto = buscarProducto(movimiento);
        moverStock(producto, movimiento.getTipo(), movimiento.getCantidad());
        productoRepo.save(producto);

        movimiento.setId(null);
        movimiento.setProducto(producto);
        return movimientoRepo.save(movimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> findAll() {
        return movimientoRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Movimiento findById(Long id) {
        return movimientoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No hay ningún movimiento con el id " + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Movimiento movimiento = findById(id);
        Producto producto = movimiento.getProducto();
        devolverStock(producto, movimiento.getTipo(), movimiento.getCantidad());
        productoRepo.save(producto);
        movimientoRepo.delete(movimiento);
    }

    @Override
    @Transactional
    public Movimiento update(Movimiento movimiento) {
        if (movimiento.getId() == null) {
            throw new ValidationException("Falta el id del movimiento que quiere actualizar");
        }
        Movimiento guardado = findById(movimiento.getId());

        Producto productoViejo = guardado.getProducto();
        devolverStock(productoViejo, guardado.getTipo(), guardado.getCantidad());
        productoRepo.save(productoViejo);

        Producto productoNuevo = buscarProducto(movimiento);
        moverStock(productoNuevo, movimiento.getTipo(), movimiento.getCantidad());
        productoRepo.save(productoNuevo);

        guardado.setProducto(productoNuevo);
        guardado.setCantidad(movimiento.getCantidad());
        guardado.setTipo(movimiento.getTipo());
        return movimientoRepo.save(guardado);
    }

    private Producto buscarProducto(Movimiento movimiento) {
        if (movimiento.getProducto() == null || movimiento.getProducto().getId() == null) {
            throw new ValidationException("Falta el id del producto para guardar el movimiento");
        }
        Long idProducto = movimiento.getProducto().getId();
        return productoRepo.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("No hay ningún producto con el id " + idProducto));
    }

    private void moverStock(Producto producto, TipoMovimiento tipo, Integer cantidad) {
        if (tipo == TipoMovimiento.ENTRADA) {
            producto.sumarStock(cantidad);
        } else {
            revisarSiAlcanza(producto, cantidad);
            producto.restarStock(cantidad);
        }
    }

    private void devolverStock(Producto producto, TipoMovimiento tipo, Integer cantidad) {
        if (tipo == TipoMovimiento.ENTRADA) {
            revisarSiAlcanza(producto, cantidad);
            producto.restarStock(cantidad);
        } else {
            producto.sumarStock(cantidad);
        }
    }

    private void revisarSiAlcanza(Producto producto, Integer cantidad) {
        if (producto.getStock() < cantidad) {
            throw new InsufficientStockException("No alcanza el stock del producto '" + producto.getNombre()
                    + "'. Hay " + producto.getStock() + " y pidieron " + cantidad);
        }
    }
}
