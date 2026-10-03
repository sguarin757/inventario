package co.edu.cesde.inventarionoche.presentation.controller;

import co.edu.cesde.inventarionoche.application.service.MovimientoService;
import co.edu.cesde.inventarionoche.domain.model.Movimiento;
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
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
@Tag(name = "Movimientos", description = "Lo que entra y sale del inventario")
public class MovimientoController {

    private final MovimientoService servicioMovimientos;

    @GetMapping
    @Operation(summary = "Ver todos los movimientos")
    public ResponseEntity<List<Movimiento>> findAll() {
        return ResponseEntity.ok(servicioMovimientos.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar un movimiento por su id")
    public ResponseEntity<Movimiento> findById(@PathVariable Long id) {
        return ResponseEntity.ok(servicioMovimientos.findById(id));
    }

    @PostMapping
    @Operation(summary = "Guardar un movimiento y cambiar el stock del producto")
    public ResponseEntity<Movimiento> save(@Valid @RequestBody Movimiento movimiento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioMovimientos.save(movimiento));
    }

    @PutMapping
    @Operation(summary = "Cambiar un movimiento")
    public ResponseEntity<Movimiento> update(@Valid @RequestBody Movimiento movimiento) {
        return ResponseEntity.ok(servicioMovimientos.update(movimiento));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borrar un movimiento")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        servicioMovimientos.delete(id);
        return ResponseEntity.noContent().build();
    }
}
