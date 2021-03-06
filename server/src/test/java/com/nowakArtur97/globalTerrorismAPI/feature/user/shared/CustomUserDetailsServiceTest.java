package com.nowakArtur97.globalTerrorismAPI.feature.user.shared;

import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.UserBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("CustomUserDetailsService_Tests")
class CustomUserDetailsServiceTest {

    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    private static UserBuilder userBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        userBuilder = new UserBuilder();
    }

    @BeforeEach
    private void setUp() {

        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void when_load_user_by_user_name_should_return_user_details() {

        String userName = "user";

        UserNode userNodeExpected = (UserNode) userBuilder.withUserName(userName)
                .withRoles(Set.of(new RoleNode("user")))
                .build(ObjectType.NODE);
        User userDetailsExpected = new User(userNodeExpected.getUserName(), userNodeExpected.getPassword(),
                List.of(new SimpleGrantedAuthority("user")));

        when(userRepository.findByUserNameOrEmail(userName, userName)).thenReturn(Optional.of(userNodeExpected));

        UserDetails userDetailsActual = customUserDetailsService.loadUserByUsername(userName);

        assertAll(
                () -> assertEquals(userDetailsExpected.getUsername(), userDetailsActual.getUsername(),
                        () -> "should return user details with user name: " + userDetailsExpected.getUsername() + ", but was: " + userDetailsActual.getUsername()),
                () -> assertEquals(userDetailsExpected.getPassword(), userDetailsActual.getPassword(),
                        () -> "should return user details with user password: " + userDetailsExpected.getPassword() + ", but was: " + userDetailsActual.getPassword()),
                () -> assertEquals(userDetailsExpected.getAuthorities(), userDetailsActual.getAuthorities(),
                        () -> "should return user details with authorities: " + userDetailsExpected.getAuthorities() + ", but was: " + userDetailsActual.getAuthorities()),
                () -> verify(userRepository, times(1)).findByUserNameOrEmail(userName, userName),
                () -> verifyNoMoreInteractions(userRepository));
    }

    @Test
    void when_load_user_by_email_should_return_user_details() {

        String email = "user@email.com";

        UserNode userNodeExpected = (UserNode) userBuilder.withEmail(email)
                .withRoles(Set.of(new RoleNode("user")))
                .build(ObjectType.NODE);
        User userDetailsExpected = new User(userNodeExpected.getUserName(), userNodeExpected.getPassword(),
                List.of(new SimpleGrantedAuthority("user")));

        when(userRepository.findByUserNameOrEmail(email, email)).thenReturn(Optional.of(userNodeExpected));

        UserDetails userDetailsActual = customUserDetailsService.loadUserByUsername(email);

        assertAll(
                () -> assertEquals(userDetailsExpected.getUsername(), userDetailsActual.getUsername(),
                        () -> "should return user details with user name: " + userDetailsExpected.getUsername() + ", but was: " + userDetailsActual.getUsername()),
                () -> assertEquals(userDetailsExpected.getPassword(), userDetailsActual.getPassword(),
                        () -> "should return user details with user password: " + userDetailsExpected.getPassword() + ", but was: " + userDetailsActual.getPassword()),
                () -> assertEquals(userDetailsExpected.getAuthorities(), userDetailsActual.getAuthorities(),
                        () -> "should return user details with authorities: " + userDetailsExpected.getAuthorities() + ", but was: " + userDetailsActual.getAuthorities()),
                () -> verify(userRepository, times(1)).findByUserNameOrEmail(email, email),
                () -> verifyNoMoreInteractions(userRepository));
    }

    @Test
    void when_load_user_by_user_name_or_email_but_user_not_exists_should_throw_exception() {

        String userName = "user";

        when(userRepository.findByUserNameOrEmail(userName, userName)).thenReturn(Optional.empty());

        assertAll(
                () -> assertThrows(UsernameNotFoundException.class,
                        () -> customUserDetailsService.loadUserByUsername(userName),
                        () -> "should throw UsernameNotFoundException but wasn't"),
                () -> verify(userRepository, times(1)).findByUserNameOrEmail(userName, userName),
                () -> verifyNoMoreInteractions(userRepository));
    }

    @Test
    void when_get_user_authorities_should_return_list_of_authorities() {

        String email = "user@email.com";

        Set<RoleNode> authoritiesExpected = Set.of(new RoleNode("user"));
        UserNode userNodeExpected = (UserNode) userBuilder.withEmail(email)
                .withRoles(authoritiesExpected)
                .build(ObjectType.NODE);

        List<GrantedAuthority> authoritiesActual = customUserDetailsService.getAuthorities(userNodeExpected.getRoles());

        assertAll(
                () -> assertEquals(authoritiesExpected.size(), authoritiesActual.size(),
                        () -> "should return: " + authoritiesExpected.size() + " authorities, but was: " + authoritiesActual.size()),
                () -> assertEquals(authoritiesActual.get(0).getAuthority(), "user",
                        () -> "should return user authority, but was: " + authoritiesActual.get(0).getAuthority()),
                () -> verifyNoInteractions(userRepository));
    }
}
