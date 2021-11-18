package com.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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
    @NotEmpty(message = "topics can not be empty")
    private List<@Valid TopicDTO> topics;
}
