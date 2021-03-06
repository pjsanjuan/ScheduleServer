package com.patrick.Shift;

import com.patrick.Task.Task;
import com.patrick.User.User;
import com.patrick.User.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
public class ShiftServiceImplTest {

    @Autowired
    private ShiftService shiftService;
    @MockBean
    private ShiftRepository shiftRepository;
    @MockBean
    private UserRepository userRepository;
    //Test Data
    private User testUser1 = new User("Rick", "r@gmail.com");
    private User testUser2 = new User("Morty", "m@gmail.com");
    private Shift testShift1 = new Shift(OffsetDateTime.now(), OffsetDateTime.now(), testUser1, new Task("Cleanup"));

    @Before
    public void setUp() {
        testUser1.setId(1L);
        testUser2.setId(2L);
        testShift1.setId(1L);
    }

    @Test
    public void test_fetchAll() {
        Mockito.when(shiftRepository.findAll()).thenReturn(Collections.emptyList());
        Collection<Shift> shift = shiftService.fetchAll();
        assertEquals(Collections.emptyList(), shift);
    }

    @Test
    public void test_findShiftsByUsername() {
        Mockito.doReturn(Collections.singletonList(new Shift())).when(shiftRepository).findShiftsByUser(testUser1);
        Mockito.doReturn(testUser1).when(userRepository).findByUsername(testUser1.getUsername());
        Collection<Shift> shifts = shiftService.findShiftsByUsername(testUser1.getUsername());
        assertEquals(1, shifts.size());
    }

    @Test
    public void test_getOne_Normal() {
        Mockito.doReturn(Optional.of(testShift1)).when(shiftRepository).findById(1L);
        Optional<Shift> shift = shiftService.getOne(1L);
        assertTrue(shift.isPresent());
    }

    @Test
    public void test_getOne_NotFound() {
        Mockito.doThrow(new EntityNotFoundException()).when(shiftRepository).getOne(1L);
        Optional<Shift> shift = shiftService.getOne(1L);
        assertFalse(shift.isPresent());
    }

    @Test
    public void test_createOne_Normal() {
        shiftService.createOne(testShift1);
        Mockito.verify(shiftRepository).save(testShift1);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void test_createOne_Duplicate() {
        Mockito.doThrow(new DataIntegrityViolationException("")).when(shiftRepository).save(Mockito.any());
        shiftService.createOne(testShift1);
    }

    @Test
    public void test_modifyOne_Normal() {
        Optional<Shift> optionalShift = Optional.of(testShift1);
        Mockito.doReturn(optionalShift).when(shiftRepository).findById(1L);
        shiftService.modifyOne(testShift1);
        Mockito.verify(shiftRepository).save(testShift1);
    }

    @Test(expected = EntityNotFoundException.class)
    public void test_modifyOne_NotFound() {
        Optional<Shift> emptyShift = Optional.empty();
        Mockito.doReturn(emptyShift).when(shiftRepository).findById(1L);
        shiftService.modifyOne(testShift1);
    }

    @Test
    public void test_deleteOne_Normal() {
        shiftService.deleteOne(1L);
        Mockito.verify(shiftRepository).deleteById(1L);
    }

    @Test(expected = EntityNotFoundException.class)
    public void test_deleteOne_NotFound() {
        Mockito.doThrow(new EntityNotFoundException()).when(shiftRepository).deleteById(1L);
        shiftService.deleteOne(1L);
    }

    @TestConfiguration
    @Import(ShiftServiceImpl.class)
    static class Config {
    }
}