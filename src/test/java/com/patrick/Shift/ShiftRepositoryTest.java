package com.patrick.Shift;

import com.patrick.Task.Task;
import com.patrick.Task.TaskRepository;
import com.patrick.User.User;
import com.patrick.User.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.OffsetDateTime;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ShiftRepositoryTest {

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    ShiftRepository shiftRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TaskRepository taskRepository;

    @Test
    public void createNewShift() {
        User u = new User("testuser", "testuser@gmail.com");
        userRepository.save(u); //Has ID of 1
        Task t = new Task("Cleanup");
        taskRepository.save(t); //has ID of 1

        Shift s = new Shift(OffsetDateTime.now(), OffsetDateTime.now(), u, t);
        shiftRepository.save(s);

        System.out.println(s);
    }

    @Test
    public void deleteShift() {
        User u = new User("testuser", "testuser@gmail.com");
        userRepository.save(u); //Has ID of 1
        Task t = new Task("Cleanup");
        taskRepository.save(t); //has ID of 1
        Shift s = new Shift(OffsetDateTime.now(), OffsetDateTime.now(), u, t);
        shiftRepository.save(s);

        shiftRepository.delete(s);

        assertEquals(0, shiftRepository.count());
    }

//    public void modifyShift() {
//        Shift s = shiftRepository.getOne(1L);
//
//        Shift modifiedShift = new Shift(s.getStart(), s.getEnd(), s.getU(), s.getTask());
//        modifiedShift.setId(1L);
//        shiftRepository.save(modifiedShift);
//
//        Shift found = shiftRepository.getOne(1L);
//        assertTrue(modifiedShift.equals(found));
//    }
}