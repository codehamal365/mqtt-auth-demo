package com.example.controller;

import com.example.constant.AuthConstants;
import com.example.dto.AuthOnPublishDTO;
import com.example.dto.AuthOnRegisterDTO;
import com.example.dto.AuthOnSubscribeDTO;
import com.example.dto.ResponseDTO;
import com.example.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

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

    static final CacheControl CACHE_CONTROL = CacheControl.maxAge(0, TimeUnit.SECONDS);

    private final WebhookService webhookService;

    @PostMapping("register")
    public ResponseDTO authOnRegister(@RequestHeader(AuthConstants.AUTH_HEADER_KEY) String header,
                                      @RequestBody AuthOnRegisterDTO dto) {
        return webhookService.authenticateRegister(dto.getUsername(), dto.getPassword()) ?
                ResponseDTO.ok() : ResponseDTO.errorDefault();
    }

    @PostMapping("subscribe")
    public ResponseDTO authOnSubscribe(@RequestBody AuthOnSubscribeDTO dto) {
        return webhookService.authorizeSubscribe(dto.getTopics()) ?
                ResponseDTO.ok() : ResponseDTO.errorDefault();
    }

    @PostMapping("publish")
    public ResponseDTO authOnPublish(@RequestBody AuthOnPublishDTO dto) {
        return webhookService.authorizePublish(dto.getTopic()) ?
                ResponseDTO.ok() : ResponseDTO.error("some message error");
    }

}
