package com.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author xie.wei
 * @date created at 2021-11-16 10:58
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TopicDTO {
    private String topic;
    private int qos;
}
