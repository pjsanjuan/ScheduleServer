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
    ResponseEntity<Collection<Shift>> getShifts() {
        return new ResponseEntity<>(shiftService.fetchAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISOR')")
    ResponseEntity<Shift> getOneShift(@PathVariable("id") Long id) {
        return new ResponseEntity<>(shiftService.fetchOne(id), HttpStatus.OK);
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
