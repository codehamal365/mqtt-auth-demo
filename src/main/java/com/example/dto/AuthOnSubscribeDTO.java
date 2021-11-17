package com.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * subscribe dto
 *
 * @author xie.wei
 * @date created at 2021-11-16 10:58
 */
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AuthOnSubscribeDTO extends BaseDTO {
    private List<TopicDTO> topics;
}
