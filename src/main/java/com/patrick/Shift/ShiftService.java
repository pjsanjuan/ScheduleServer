package com.patrick.Shift;

import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.EntityNotFoundException;

import com.patrick.User.User;

import java.util.Collection;
import java.util.Optional;

public interface ShiftService {
    Collection<Shift> fetchAll();

    Collection<Shift> findShiftsByUsername(String username);

    Optional<Shift> getOne(Long id);

    void createOne(Shift s) throws DataIntegrityViolationException;

    void modifyOne(Shift s) throws EntityNotFoundException;

    void deleteOne(Long id);
}
