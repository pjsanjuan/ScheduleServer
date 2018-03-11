package com.patrick.Shift;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.patrick.Task.Task;
import com.patrick.User.User;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.OffsetDateTime;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ShiftController.class)
public class ShiftControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ShiftService shiftService;

    @MockBean
    private ShiftRepository shiftRepository;

    private User testUser1 = new User("Rick", "r@gmail.com");
    private User testUser2 = new User("Morty", "m@gmail.com");
    private Shift testShift1 = new Shift(OffsetDateTime.parse("2018-03-10T18:38:11.738-08:00"),
            OffsetDateTime.parse("2018-03-10T18:38:11.738-08:00"), testUser1, new Task("Cleanup"));
    private Shift testShift2 = new Shift(OffsetDateTime.parse("2018-03-10T18:38:11.738-08:00"),
            OffsetDateTime.parse("2018-03-10T18:38:11.738-08:00"), testUser2, new Task("Pickup"));


    @After
    public void teardown() {
        Mockito.reset(shiftService);
    }

    @Test
    public void getShifts() throws Exception {
        Mockito.when(shiftRepository.findAll()).thenReturn(new ArrayList<Shift>() {{
            add(testShift1);
            add(testShift2);
        }});
        mvc.perform(get("/shifts")).andExpect(status().isOk());
    }

    @Test
    public void getOneShift() throws Exception {
        Mockito.doReturn(testShift1).when(shiftService).fetchOne(1L);
        mvc.perform(get("/shifts")).andExpect(status().isOk());
    }

    @Test
    public void createShift_NoDuplicate() throws Exception {
        mvc.perform(post("/shifts")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(testShift1))
        ).andExpect(status().isCreated());
        Mockito.verify(shiftService).createOne(objectMapper.readValue(objectMapper.writeValueAsString(testShift1), Shift.class));
    }

    @Test
    public void createShift_Duplicate() throws Exception {
        Mockito.doNothing()
                .doThrow(new DataIntegrityViolationException(""))
                .when(shiftService).createOne(Mockito.any());

        //First create call
        mvc.perform(post("/shifts")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(testShift1))
        ).andExpect(status().isCreated()).andReturn();

        //Duplicate create call
        mvc.perform(post("/shifts")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(testShift1))
        ).andExpect(status().isConflict()).andReturn();
    }

    @Test
    public void updateShift() {
    }

    @Test
    public void deleteShift() throws Exception {
        Mockito.doNothing().when(shiftService).deleteOne(1L);
        mvc.perform(delete("/shifts/1")).andExpect(status().isOk());
    }
}