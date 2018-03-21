package com.patrick.Shift;

import com.patrick.User.User;
import com.patrick.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.Optional;

@Service
public class ShiftServiceImpl implements ShiftService {

    private ShiftRepository shiftRepository;
    private UserRepository userRepository;

    @Autowired
    public ShiftServiceImpl(ShiftRepository shiftRepository, UserRepository userRepository) {
        this.shiftRepository = shiftRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Collection<Shift> fetchAllByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return shiftRepository.findByUser(user);
    }


    @Override
    public Collection<Shift> fetchAll() {
        return shiftRepository.findAll();
    }

    @Override
    public Shift fetchOne(Long id) throws EntityNotFoundException {
        return shiftRepository.getOne(id);
    }

    @Override
    public void createOne(Shift s) throws DataIntegrityViolationException {
        shiftRepository.save(s);
    }

    @Override
    public void modifyOne(Shift s) throws EntityNotFoundException {
        Optional<Shift> optionalShift = shiftRepository.findById(s.getId());
        if (!optionalShift.isPresent())
            throw new EntityNotFoundException();
        else
            shiftRepository.save(s);
    }

    @Override
    public void deleteOne(Long id) throws EntityNotFoundException {
        shiftRepository.deleteById(id);
    }

}
