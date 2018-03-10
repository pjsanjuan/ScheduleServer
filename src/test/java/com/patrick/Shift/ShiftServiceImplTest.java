package com.patrick.Shift;

import com.patrick.Task.Task;
import com.patrick.User.User;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.OffsetDateTime;
import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class ShiftServiceImplTest {

    @TestConfiguration
    @Import(ShiftServiceImpl.class)
    static class Config {
    }

    @Autowired
    private ShiftService shiftService;

    @MockBean
    private ShiftRepository shiftRepository;

    private User testUser1 = new User("Rick", "r@gmail.com");
    private User testUser2 = new User("Morty", "m@gmail.com");
    private Shift testShift1 = new Shift(OffsetDateTime.now(), OffsetDateTime.now(), testUser1, new Task("Cleanup"));
    private Shift testShift2 = new Shift(OffsetDateTime.now(), OffsetDateTime.now(), testUser2, new Task("Pickup"));

    @After
    public void teardown() {
        Mockito.reset(shiftRepository);
    }

    @Test
    public void fetchAll() {
        Mockito.when(shiftRepository.findAll()).thenReturn(new ArrayList<Shift>() {{
            add(testShift1);
            add(testShift2);
        }});
        shiftService.fetchAll();
        Mockito.verify(shiftRepository).findAll();
    }

    @Test
    public void fetchOne() {
        Mockito.doReturn(testShift1).when(shiftRepository).getOne(1L);
        Shift s = shiftService.fetchOne(1L);
        Mockito.verify(shiftRepository).getOne(1L);
    }

    @Test
    public void createOne() {
        shiftService.createOne(testShift1);
        Mockito.verify(shiftRepository).save(testShift1);
    }

    @Test
    public void modifyOne() {
    }

    @Test
    public void deleteOne() {
        shiftService.deleteOne(1L);
        Mockito.verify(shiftRepository).deleteById(1L);
    }
}