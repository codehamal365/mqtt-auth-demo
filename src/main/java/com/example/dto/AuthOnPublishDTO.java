package com.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * publish dto
 *
 * @author xie.wei
 * @date created at 2021-11-16 10:55
 */
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AuthOnPublishDTO extends BaseDTO {

    private int qos;

    private String topic;

    private String payload;

    private boolean retain;
}
