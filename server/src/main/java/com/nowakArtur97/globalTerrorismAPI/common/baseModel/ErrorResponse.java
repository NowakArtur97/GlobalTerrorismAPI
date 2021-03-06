package com.nowakArtur97.globalTerrorismAPI.common.baseModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApiModel(description = "Details about the Error")
@Getter
@Setter
public class ErrorResponse {

    @ApiModelProperty(notes = "Error time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime timestamp;

    @ApiModelProperty(notes = "Error status")
    private int status;

    @ApiModelProperty(notes = "Details of the cause of the error")
    private final List<String> errors;

    public ErrorResponse(LocalDateTime timestamp, int status) {
        this.timestamp = timestamp;
        this.status = status;
        this.errors = new ArrayList<>();
    }

    public void addError(String error) {

        errors.add(error);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof ErrorResponse)) return false;

        ErrorResponse that = (ErrorResponse) o;
        return getStatus() == that.getStatus() &&
                Objects.equals(getTimestamp(), that.getTimestamp()) &&
                Objects.equals(getErrors(), that.getErrors());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTimestamp(), getStatus(), getErrors());
    }
}
