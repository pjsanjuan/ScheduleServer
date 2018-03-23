package com.patrick.Shift;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/shifts")
public class ShiftController {
    private ShiftService shiftService;

    @Autowired
    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISOR')")
    Collection<Shift> getShifts() {
        return shiftService.fetchAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISOR')")
    ResponseEntity<?> getOneShift(@PathVariable("id") Long id) {
        Optional<Shift> shift = shiftService.getOne(id);
        if (shift.isPresent()) return new ResponseEntity<>(shift.get(), HttpStatus.OK);
        else return ResponseEntity.notFound().build();
    }

    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISOR')")
    ResponseEntity<?> createShift(@RequestBody Shift shift) {
        try {
            shiftService.createOne(shift);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Shift passed is a duplicate.");
        }
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/shifts/" + shift.getId()).build().toUri();
        return ResponseEntity.created(location).build();

    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISOR')")
    ResponseEntity<?> updateShift(@RequestBody Shift shift, @PathVariable("id") Long id) {
        try {
            shift.setId(id);
            shiftService.modifyOne(shift);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body("Shift with ID: " + id + "could not be found");
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISOR')")
    ResponseEntity<?> deleteShift(@PathVariable("id") Long id) {
        try {
            shiftService.deleteOne(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body("Shift with ID: " + id + "could not be found");
        }
    }
}
