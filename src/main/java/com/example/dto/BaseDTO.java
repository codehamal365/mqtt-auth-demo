package com.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * base dto for publish subscribe and register
 *
 * @author xie.wei
 * @date created at 2021-11-16 10:59
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BaseDTO {
    @JsonProperty("client_id")
    private String clientId;

    @NotBlank(message = "username can not be blank")
    private String username;

    private String mountpoint;

}
