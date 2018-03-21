package com.patrick.Shift;

import com.patrick.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    Collection<Shift> findShiftsByUser(User u);
}
