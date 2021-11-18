package com.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * used by subscribe dto
 *
 * @author xie.wei
 * @date created at 2021-11-16 10:58
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TopicDTO {
    @NotBlank(message = "topic can not be blank")
    private String topic;
    private int qos;
}
