package com.example.controller;

import com.example.dto.AuthOnPublishDTO;
import com.example.dto.AuthOnRegisterDTO;
import com.example.dto.AuthOnSubscribeDTO;
import com.example.dto.ResponseDTO;
import com.example.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * webhooks controller
 *
 * @author xie.wei
 * @date created at 2021-11-16 10:53
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/webhooks/auth")
@ResponseStatus(HttpStatus.OK)
public class WebhookAuthController {

    private final WebhookService webhookService;

    @PostMapping("register")
    public ResponseDTO authOnRegister(@RequestBody @Validated AuthOnRegisterDTO dto) {
        return webhookService.authenticateRegister(dto.getUsername(), dto.getPassword()) ?
                ResponseDTO.ok() : ResponseDTO.errorDefault();
    }

    @PostMapping("subscribe")
    public ResponseDTO authOnSubscribe(@RequestBody @Validated AuthOnSubscribeDTO dto) {
        return webhookService.authorizeSubscribe(dto.getTopics()) ?
                ResponseDTO.ok() : ResponseDTO.errorDefault();
    }

    @PostMapping("publish")
    public ResponseDTO authOnPublish(@RequestBody @Validated AuthOnPublishDTO dto) {
        return webhookService.authorizePublish(dto.getTopic()) ?
                ResponseDTO.ok() : ResponseDTO.errorDefault();
    }

}
