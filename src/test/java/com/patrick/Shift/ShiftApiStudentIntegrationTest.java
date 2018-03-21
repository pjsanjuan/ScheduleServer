package com.patrick.Shift;

import com.patrick.Security.TokenAuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Test cases from the perspective of a STUDENT
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShiftApiStudentIntegrationTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;
    private String jwt = TokenAuthenticationService
            .createToken("student", Collections.singletonList(new SimpleGrantedAuthority("student")));

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo(print())
                .build();
    }

    @Test
    public void test_getShifts() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/shifts")
                .header("Authorization", jwt)
        ).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void test_getOneShift() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/shifts/1")
                .header("Authorization", jwt)
        ).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void test_createShift() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/shifts")
                .header("Authorization", jwt)
        ).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void test_updateShift() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put("/shifts/1")
                .header("Authorization", jwt)
        ).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

}
