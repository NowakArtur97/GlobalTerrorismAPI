package com.NowakArtur97.GlobalTerrorismAPI.controller.security;

import com.NowakArtur97.GlobalTerrorismAPI.model.request.AuthenticationRequest;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.AuthenticationResponse;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.ErrorResponse;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.CustomUserDetailsService;
import com.NowakArtur97.GlobalTerrorismAPI.tag.AuthorizationTag;
import com.NowakArtur97.GlobalTerrorismAPI.util.jwt.JwtUtil;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/authorization")
@Api(tags = {AuthorizationTag.RESOURCE})
@RequiredArgsConstructor
public class AuthorizationController {

    private final CustomUserDetailsService customUserDetailsService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    @PostMapping
    @ApiOperation(value = "Generate API key", notes = "Generate API key")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully generated API key", response = AuthenticationResponse.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<AuthenticationResponse> loginUser(@RequestBody @ApiParam(value = "User credentials", name = "user", required = true)AuthenticationRequest authenticationRequest) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUserName(), authenticationRequest.getPassword()));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(authenticationRequest.getUserName());

        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(token));
    }

}
