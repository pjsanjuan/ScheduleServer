package com.patrick.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patrick.Security.AccountCredentials;
import com.patrick.Security.TokenAuthenticationService;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Architecture for this test borrowed from: https://stackoverflow.com/a/45247733/3893713
 * Test cases from the perspective of a ADMIN
 */
//https://docs.spring.io/spring-security/site/docs/3.1.x/reference/el-access.html
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserApiAdminIntegrationTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UserRepository userRepository;
    private MockMvc mvc;
    private String jwt = TokenAuthenticationService
            .createToken("testadmin", Collections.singletonList(new SimpleGrantedAuthority("ADMIN")));

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo(print())
                .build();
    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    public void test_getUsers() throws Exception {
        //Setup
        userRepository.saveAndFlush(new User("testuser", "testpass"));

        //Exercise
        mvc.perform(get("/users").header("Authorization", jwt))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    public void test_getOneUser_Self() throws Exception {
        //Setup data
        User user = new User("testadmin", "testadmin");
        userRepository.saveAndFlush(user); //should have ID 1

        mvc.perform(get("/users/testadmin")
                .header("Authorization", jwt)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void test_getOneUser_Other() throws Exception {
        //Setup data
        User user = new User("testuser", "testpass");
        userRepository.saveAndFlush(user); //should have ID 1

        mvc.perform(get("/users/testuser")
                .header("Authorization", jwt)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void test_createOneUser() throws Exception {
        mvc.perform(post("/users")
                .header("Authorization", jwt)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(new AccountCredentials("testuser", "testpass")))
        ).andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(header().exists("Location"));
    }

    @Test
    public void test_modifyOneUser() throws Exception {
        //Setup
        User user = new User("testuser", "testpass");
        userRepository.saveAndFlush(user);

        User fechtedUser = userRepository.findByUsername("testuser");

        String content = "{\"id\":1,\"username\":\"modifyuser\",\"password\":\"modifypassword\",\"email\":null,\"role\":\"STUDENT\"}";
        mvc.perform(put("/users/testuser")
                .header("Authorization", jwt)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(content)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }
}
