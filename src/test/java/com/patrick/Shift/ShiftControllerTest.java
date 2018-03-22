package com.patrick.Shift;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.patrick.Task.Task;
import com.patrick.User.User;
import edu.emory.mathcs.backport.java.util.Collections;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ShiftController.class})
public class ShiftControllerTest {

    @Autowired
    ShiftController shiftController;
    @MockBean
    private ShiftService shiftService;


    private ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mvc;

    //Test Data
    private User testUser1 = new User("Rick", "r@gmail.com");
    private User testUser2 = new User("Morty", "m@gmail.com");
    private Shift testShift1 = new Shift(OffsetDateTime.parse("2018-03-10T18:38:11.738-08:00"),
            OffsetDateTime.parse("2018-03-10T18:38:11.738-08:00"), testUser1, new Task("Cleanup"));

    @Before
    public void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(shiftController)
                .alwaysDo(print())
                .build();
        testUser1.setId(1L);
        testUser2.setId(2L);
        testShift1.setId(1L);

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.findAndRegisterModules();
    }


    @Test
    public void test_getShifts_Normal() throws Exception {
        Mockito.doReturn(Collections.singletonList(testShift1)).when(shiftService).fetchAll();

        mvc.perform(get("/shifts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    public void test_getOneShift_Normal() throws Exception {
        Mockito.doReturn(testShift1).when(shiftService).fetchOne(1L);

        mvc.perform(get("/shifts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username", Matchers.is("Rick")));
    }

    public void test_getOneShift_NotFound() throws Exception {
        Mockito.doThrow(new EntityNotFoundException()).when(shiftService).fetchOne(1L);

        mvc.perform(get("/shifts/1")).andExpect(status().isBadRequest());
    }

    @Test
    public void test_createShift_Normal() throws Exception {
        mvc.perform(post("/shifts")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(testShift1))
        ).andExpect(status().isCreated()).andExpect(header().exists("Location"));

        Mockito.verify(shiftService).createOne(objectMapper.readValue(objectMapper.writeValueAsString(testShift1), Shift.class));
    }

    @Test
    public void test_createShift_Duplicate() throws Exception {
        Mockito.doThrow(new DataIntegrityViolationException("")).when(shiftService).createOne(Mockito.any());

        mvc.perform(post("/shifts")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(testShift1))
        ).andExpect(status().isConflict());
    }

    @Test
    public void test_updateShift_Normal() throws Exception {
        mvc.perform(put("/shifts/1")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(testShift1))
        ).andExpect(status().isOk());

        Mockito.verify(shiftService).modifyOne(objectMapper.readValue(objectMapper.writeValueAsString(testShift1), Shift.class));
    }

    @Test
    public void test_updateShift_NotFound() throws Exception {
        Mockito.doThrow(new EntityNotFoundException()).when(shiftService).modifyOne(Mockito.any());

        mvc.perform(put("/shifts/1")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(testShift1))
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void test_deleteShift_Normal() throws Exception {
        mvc.perform(delete("/shifts/1")).andExpect(status().isOk());
    }

    public void test_deleteShift_NotFound() throws Exception {
        Mockito.doThrow(new EntityNotFoundException()).when(shiftService).deleteOne(1L);

        mvc.perform(delete("/shifts/1")).andExpect(status().isBadRequest());
    }
}