package com.nowakArtur97.globalTerrorismAPI.feature.user.shared;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "API key")
@Getter
@Setter
@AllArgsConstructor
public class AuthenticationResponse {

    @ApiModelProperty(notes = "Generated token")
    private String token;

    @ApiModelProperty(notes = "Expiration time in milliseconds")
    private long expirationTimeInMilliseconds;
}
