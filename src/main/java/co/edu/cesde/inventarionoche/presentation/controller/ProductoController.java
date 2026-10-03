package co.edu.cesde.inventarionoche.presentation.controller;

import co.edu.cesde.inventarionoche.application.service.ProductoService;
import co.edu.cesde.inventarionoche.domain.model.Producto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Los productos del inventario")
public class ProductoController {

    private final ProductoService servicioProductos;

    @GetMapping
    @Operation(summary = "Ver todos los productos")
    public ResponseEntity<List<Producto>> findAll() {
        return ResponseEntity.ok(servicioProductos.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar un producto por su id")
    public ResponseEntity<Producto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(servicioProductos.findById(id));
    }

    @PostMapping
    @Operation(summary = "Guardar un producto nuevo")
    public ResponseEntity<Producto> save(@Valid @RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioProductos.save(producto));
    }

    @PutMapping
    @Operation(summary = "Cambiar un producto")
    public ResponseEntity<Producto> update(@Valid @RequestBody Producto producto) {
        return ResponseEntity.ok(servicioProductos.update(producto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borrar un producto")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        servicioProductos.delete(id);
        return ResponseEntity.noContent().build();
    }
}
