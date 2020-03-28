package com.NowakArtur97.GlobalTerrorismAPI.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@ApiImplicitParams({
		@ApiImplicitParam(name = "page", dataType = "integer", paramType = "query", defaultValue = "0", value = "Results page you want to retrieve (0..N)"),
		@ApiImplicitParam(name = "size", dataType = "integer", paramType = "query", defaultValue = "100", value = "Number of records per page.") })
public @interface ApiPageable {

}