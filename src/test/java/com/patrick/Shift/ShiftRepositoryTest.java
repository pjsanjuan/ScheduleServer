package com.patrick.Shift;

import com.patrick.Task.Task;
import com.patrick.Task.TaskRepository;
import com.patrick.User.User;
import com.patrick.User.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.OffsetDateTime;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    public void fetchAllByUser() {
        //Setup
        User u = new User("testuser", "testuser@gmail.com");
        userRepository.saveAndFlush(u); //Has ID of 1
        Task t = new Task("Cleanup");
        taskRepository.saveAndFlush(t); //has ID of 1
        Shift s = new Shift(OffsetDateTime.now(), OffsetDateTime.now(), u, t);
        entityManager.persist(s);
        entityManager.flush();


        Collection<Shift> shifts = shiftRepository.findByUser(u);
        assertNotNull(shifts);
        assertEquals(1, shifts.size());
    }

    @Test
    public void createNewShift() {
        User u = new User("testuser", "testuser@gmail.com");
        userRepository.save(u); //Has ID of 1
        Task t = new Task("Cleanup");
        taskRepository.save(t); //has ID of 1

        Shift s = new Shift(OffsetDateTime.now(), OffsetDateTime.now(), u, t);
        shiftRepository.save(s);
    }

    @Test
    public void deleteShift() {
        User u = new User("testuser", "testuser@gmail.com");
        userRepository.save(u); //Has ID of 1
        Task t = new Task("Cleanup");
        taskRepository.save(t); //has ID of 1
        Shift s = new Shift(OffsetDateTime.now(), OffsetDateTime.now(), u, t);
        entityManager.persist(s);
        entityManager.flush();

        shiftRepository.delete(s);

        assertEquals(0, shiftRepository.count());
    }
}