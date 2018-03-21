package com.patrick.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patrick.User.User;
import com.patrick.User.UserRepository;
import com.patrick.User.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoginIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    private MockMvc mvc;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity()) //perform all of the initial setup we need to integrate Spring Security with Spring MVC Test
                .alwaysDo(print())
                .build();
        userService.createOne(new User("testuser", "testpass")); //password needs to be encrypted first
    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void test_loginWithValidCredentials() throws Exception {
        AccountCredentials accountCredentials = new AccountCredentials("testuser", "testpass");
        mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(accountCredentials))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.header().exists("Authorization"));
    }

    @Test
    public void test_loginWithoutValidCredentials() throws Exception {
        AccountCredentials accountCredentials = new AccountCredentials("testuser", "wrongpass");
        mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(accountCredentials))
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
