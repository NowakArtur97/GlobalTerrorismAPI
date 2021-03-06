package com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser;

import com.nowakArtur97.globalTerrorismAPI.advice.RestResponseGlobalEntityExceptionHandler;
import com.nowakArtur97.globalTerrorismAPI.common.util.JwtUtil;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.CustomUserDetailsService;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.UserNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.UserBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.mapper.ObjectTestMapper;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserRegistrationController_Tests")
class UserRegistrationControllerTest {

    private final String AUTHENTICATION_BASE_PATH = "http://localhost:8080/api/v1/registration";
    private final String REGISTRATION_BASE_PATH = AUTHENTICATION_BASE_PATH + "/register";
    private final String CHECK_USER_DATA_BASE_PATH = AUTHENTICATION_BASE_PATH + "/checkUserData";

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    private static UserBuilder userBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        userBuilder = new UserBuilder();
    }

    @BeforeEach
    private void setUp() {

        UserRegistrationController userRegistrationController
                = new UserRegistrationController(userService, customUserDetailsService, jwtUtil);

        RestResponseGlobalEntityExceptionHandler restResponseGlobalEntityExceptionHandler
                = new RestResponseGlobalEntityExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(userRegistrationController, restResponseGlobalEntityExceptionHandler)
                .build();
    }

    @Nested
    class UserRegistrationControllerRegisterTest {

        @Test
        void when_register_valid_user_should_register_user() {

            UserDTO userDTO = (UserDTO) userBuilder.build(ObjectType.DTO);
            UserNode userNode = (UserNode) userBuilder.build(ObjectType.NODE);

            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("user"));
            User userDetails = new User(userNode.getUserName(), userNode.getPassword(),
                    authorities);

            String token = "generatedToken";
            int expirationTimeInMilliseconds = 36000000;

            when(userService.register(userDTO)).thenReturn(userNode);
            when(customUserDetailsService.getAuthorities(userNode.getRoles())).thenReturn(authorities);
            when(jwtUtil.generateToken(userDetails)).thenReturn(token);

            assertAll(
                    () -> mockMvc
                            .perform(post(REGISTRATION_BASE_PATH)
                                    .content(ObjectTestMapper.asJsonString(userDTO))
                                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("token", is(token)))
                            .andExpect(jsonPath("expirationTimeInMilliseconds", is(expirationTimeInMilliseconds))),
                    () -> verify(userService, times(1)).register(userDTO),
                    () -> verifyNoMoreInteractions(userService),
                    () -> verify(customUserDetailsService, times(1)).getAuthorities(userNode.getRoles()),
                    () -> verifyNoMoreInteractions(customUserDetailsService),
                    () -> verify(jwtUtil, times(1)).generateToken(userDetails),
                    () -> verifyNoMoreInteractions(jwtUtil));
        }

        @Test
        void when_register_user_with_null_fields_should_return_error_response() {

            UserDTO userDTO = (UserDTO) userBuilder.withUserName(null).withPassword(null).withMatchingPassword(null)
                    .withEmail(null).build(ObjectType.DTO);

            assertAll(
                    () -> mockMvc
                            .perform(post(REGISTRATION_BASE_PATH)
                                    .content(ObjectTestMapper.asJsonString(userDTO))
                                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("{user.name.notBlank}")))
                            .andExpect(jsonPath("errors", hasItem("{user.password.notBlank}")))
                            .andExpect(jsonPath("errors", hasItem("{user.matchingPassword.notBlank}")))
                            .andExpect(jsonPath("errors", hasItem("{user.email.notBlank}")))
                            .andExpect(jsonPath("errors", hasSize(4))),
                    () -> verifyNoInteractions(userService),
                    () -> verifyNoInteractions(customUserDetailsService),
                    () -> verifyNoInteractions(jwtUtil));
        }

        @ParameterizedTest(name = "{index}: For User name: {0}")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_register_user_with_blank_user_name_should_return_error_response(String invalidUserName) {

            UserDTO userDTO = (UserDTO) userBuilder.withUserName(invalidUserName).build(ObjectType.DTO);

            assertAll(
                    () -> mockMvc
                            .perform(post(REGISTRATION_BASE_PATH)
                                    .content(ObjectTestMapper.asJsonString(userDTO))
                                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("{user.name.notBlank}")))
                            .andExpect(jsonPath("errors", hasItem("{user.name.size}")))
                            .andExpect(jsonPath("errors", hasSize(2))),
                    () -> verifyNoInteractions(userService),
                    () -> verifyNoInteractions(customUserDetailsService),
                    () -> verifyNoInteractions(jwtUtil));
        }

        @Test
        void when_register_user_with_too_short_user_name_should_return_error_response() {

            UserDTO userDTO = (UserDTO) userBuilder.withUserName("u").build(ObjectType.DTO);

            assertAll(
                    () -> mockMvc
                            .perform(post(REGISTRATION_BASE_PATH)
                                    .content(ObjectTestMapper.asJsonString(userDTO))
                                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("{user.name.size}")))
                            .andExpect(jsonPath("errors", hasSize(1))),
                    () -> verifyNoInteractions(userService),
                    () -> verifyNoInteractions(customUserDetailsService),
                    () -> verifyNoInteractions(jwtUtil));
        }

        @Test
        void when_register_user_with_blank_email_should_return_error_response() {

            UserDTO userDTO = (UserDTO) userBuilder.withEmail("     ").build(ObjectType.DTO);

            assertAll(
                    () -> mockMvc
                            .perform(post(REGISTRATION_BASE_PATH)
                                    .content(ObjectTestMapper.asJsonString(userDTO))
                                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("{user.email.notBlank}")))
                            .andExpect(jsonPath("errors", hasItem("{user.email.wrongFormat}")))
                            .andExpect(jsonPath("errors", hasSize(2))),
                    () -> verifyNoInteractions(userService),
                    () -> verifyNoInteractions(customUserDetailsService),
                    () -> verifyNoInteractions(jwtUtil));
        }

        @ParameterizedTest(name = "{index}: For User email: {0}")
        @ValueSource(strings = {"wrongformat", "wrong.format"})
        void when_register_user_with_an_incorrect_format_email_should_return_error_response(String invalidEmail) {

            UserDTO userDTO = (UserDTO) userBuilder.withEmail(invalidEmail).build(ObjectType.DTO);

            assertAll(
                    () -> mockMvc
                            .perform(post(REGISTRATION_BASE_PATH)
                                    .content(ObjectTestMapper.asJsonString(userDTO))
                                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("{user.email.wrongFormat}")))
                            .andExpect(jsonPath("errors", hasSize(1))),
                    () -> verifyNoInteractions(userService),
                    () -> verifyNoInteractions(customUserDetailsService),
                    () -> verifyNoInteractions(jwtUtil));
        }

        @ParameterizedTest(name = "{index}: For User password: {0}")
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void when_register_user_with_blank_password_should_return_error_response(String invalidPassword) {

            UserDTO userDTO = (UserDTO) userBuilder.withPassword(invalidPassword).build(ObjectType.DTO);

            assertAll(
                    () -> mockMvc
                            .perform(post(REGISTRATION_BASE_PATH)
                                    .content(ObjectTestMapper.asJsonString(userDTO))
                                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("{user.password.notBlank}")))
                            .andExpect(jsonPath("errors", hasSize(1))),
                    () -> verifyNoInteractions(userService),
                    () -> verifyNoInteractions(customUserDetailsService),
                    () -> verifyNoInteractions(jwtUtil));
        }


        @ParameterizedTest(name = "{index}: For User matching password: {0}")
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void when_register_user_with_blank_matching_password_should_return_error_response(String invalidMatchingPassword) {

            UserDTO userDTO = (UserDTO) userBuilder.withMatchingPassword(invalidMatchingPassword).build(ObjectType.DTO);

            assertAll(
                    () -> mockMvc
                            .perform(post(REGISTRATION_BASE_PATH)
                                    .content(ObjectTestMapper.asJsonString(userDTO))
                                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("{user.matchingPassword.notBlank}")))
                            .andExpect(jsonPath("errors", hasSize(1))),
                    () -> verifyNoInteractions(userService),
                    () -> verifyNoInteractions(customUserDetailsService),
                    () -> verifyNoInteractions(jwtUtil));
        }
    }

    @Nested
    class UserRegistrationControllerChecUserDataTest {

        @Test
        void when_check_existing_user_data_should_response_with_false_statuses() {

            String existingUserName = "user";
            String existingEmail = "email@email.com";
            boolean isUserNameAvailable = false;
            boolean isEmailAvailable = false;
            UserDataStatusCheckRequest userData = new UserDataStatusCheckRequest(existingUserName, existingEmail);
            UserDataStatusCheckResponse userDataStatus = new UserDataStatusCheckResponse(isUserNameAvailable, isEmailAvailable);

            when(userService.checkUserData(userData)).thenReturn(userDataStatus);

            assertAll(
                    () -> mockMvc
                            .perform(post(CHECK_USER_DATA_BASE_PATH)
                                    .content(ObjectTestMapper.asJsonString(userData))
                                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("isUserNameAvailable", is(isUserNameAvailable)))
                            .andExpect(jsonPath("isEmailAvailable", is(isEmailAvailable))),
                    () -> verify(userService, times(1)).checkUserData(userData),
                    () -> verifyNoMoreInteractions(userService),
                    () -> verifyNoInteractions(customUserDetailsService),
                    () -> verifyNoInteractions(jwtUtil));
        }

        @Test
        void when_check_not_existing_user_data_should_response_with_true_statuses() {

            String notExistingUserName = "user";
            String notExistingEmail = "email@email.com";
            boolean isUserNameAvailable = true;
            boolean isEmailAvailable = true;
            UserDataStatusCheckRequest userData = new UserDataStatusCheckRequest(notExistingUserName, notExistingEmail);
            UserDataStatusCheckResponse userDataStatus = new UserDataStatusCheckResponse(isUserNameAvailable, isEmailAvailable);

            when(userService.checkUserData(userData)).thenReturn(userDataStatus);

            assertAll(
                    () -> mockMvc
                            .perform(post(CHECK_USER_DATA_BASE_PATH)
                                    .content(ObjectTestMapper.asJsonString(userData))
                                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("isUserNameAvailable", is(isUserNameAvailable)))
                            .andExpect(jsonPath("isEmailAvailable", is(isEmailAvailable))),
                    () -> verify(userService, times(1)).checkUserData(userData),
                    () -> verifyNoMoreInteractions(userService),
                    () -> verifyNoInteractions(customUserDetailsService),
                    () -> verifyNoInteractions(jwtUtil));
        }
    }
}