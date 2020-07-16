package com.NowakArtur97.GlobalTerrorismAPI.model.response;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@ApiModel(description = "Details about the Country")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class CountryModel extends RepresentationModel<CountryModel> {

    @ApiModelProperty(notes = "The unique id of the Country")
    private Long id;

    @ApiModelProperty(notes = "The country's name")
    private String name;
}