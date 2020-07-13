package com.NowakArtur97.GlobalTerrorismAPI.controller.security;

import com.NowakArtur97.GlobalTerrorismAPI.node.RoleNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.UserNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.UserRepository;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.jwt.JwtUtil;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("AuthenticationController_Tests")
class JwtAuthenticationTest {

    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";
    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";
    private final String GROUP_BASE_PATH = "http://localhost:8080/api/v1/groups";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private static UserNode userNode = new UserNode("user1234", "Password1234!", "user1234email@.com",
            Set.of(new RoleNode("user")));

    @BeforeAll
    private static void setUpUser(@Autowired UserRepository userRepository) {

        userRepository.save(userNode);
    }

    @ParameterizedTest(name = "{index}: For URI: {0} without jwt token should throw exceptions")
    @ValueSource(strings = {TARGET_BASE_PATH, EVENT_BASE_PATH, GROUP_BASE_PATH})
    void when_jwt_token_missing_in_request_headers_should_return_error_response(String url) {

        assertAll(
                () -> mockMvc
                        .perform(get(url).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(401)))
                        .andExpect(jsonPath("errors[0]", is("Missing Jwt token in request headers.")))
                        .andExpect(jsonPath("errors", IsCollectionWithSize.hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For URI: {0} with jwt token should return content")
    @ValueSource(strings = {TARGET_BASE_PATH, EVENT_BASE_PATH, GROUP_BASE_PATH})
    void when_jwt_token_is_in_request_headers_should_return_content(String url) {

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(get(url).header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("content").exists())
                        .andExpect(jsonPath("errors").doesNotExist()));
    }
}