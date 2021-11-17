package com.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

/**
 * @author xie.wei
 * @date created at 2021-11-16 10:59
 */
@Data
public class BaseDTO {
    @JsonProperty("client_id")
    private String clientId;

    @Getter
    private String username;

    private String mountpoint;

}
