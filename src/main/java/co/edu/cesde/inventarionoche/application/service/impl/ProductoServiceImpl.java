package co.edu.cesde.inventarionoche.application.service.impl;

import co.edu.cesde.inventarionoche.application.service.ProductoService;
import co.edu.cesde.inventarionoche.domain.model.Producto;
import co.edu.cesde.inventarionoche.infrastructure.exception.DuplicateResourceException;
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
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepo;
    private final MovimientoRepository movimientoRepo;

    @Override
    @Transactional
    public Producto save(Producto producto) {
        productoRepo.findByNombre(producto.getNombre()).ifPresent(guardado -> {
            throw new DuplicateResourceException("Ya hay un producto que se llama " + producto.getNombre());
        });

        producto.setId(null);
        return productoRepo.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        return productoRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Producto findById(Long id) {
        return productoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No hay ningún producto con el id " + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Producto producto = findById(id);
        if (movimientoRepo.existsByProductoId(id)) {
            throw new ValidationException("No se puede borrar el producto " + id
                    + " porque ya tiene movimientos");
        }
        productoRepo.delete(producto);
    }

    @Override
    @Transactional
    public Producto update(Producto producto) {
        if (producto.getId() == null) {
            throw new ValidationException("Falta el id del producto que quiere actualizar");
        }
        Producto guardado = findById(producto.getId());

        productoRepo.findByNombre(producto.getNombre())
                .filter(repetido -> !repetido.getId().equals(guardado.getId()))
                .ifPresent(repetido -> {
                    throw new DuplicateResourceException("Ya hay un producto que se llama " + producto.getNombre());
                });

        guardado.setNombre(producto.getNombre());
        guardado.setDescripcion(producto.getDescripcion());
        guardado.setPrecio(producto.getPrecio());
        guardado.setStock(producto.getStock());
        return productoRepo.save(guardado);
    }
}
