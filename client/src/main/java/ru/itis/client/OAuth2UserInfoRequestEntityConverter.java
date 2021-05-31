package ru.itis.client;


import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static ru.itis.client.WebSecurityConfig.DEFAULT_FORM_DATA_MEDIA_TYPE;

public class OAuth2UserInfoRequestEntityConverter implements Converter<OAuth2UserRequest, RequestEntity<?>> {

    @Override
    public RequestEntity<?> convert(OAuth2UserRequest userRequest) {
        ClientRegistration clientRegistration = userRequest.getClientRegistration();

        Map<String, String> params = new HashMap<>();

        params.put("token", userRequest.getAccessToken().getTokenValue());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(DEFAULT_FORM_DATA_MEDIA_TYPE);
        httpHeaders.setBasicAuth(clientRegistration.getClientId(), clientRegistration.getClientSecret());

        URI uri = UriComponentsBuilder
                .fromUriString(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri())
                .build()
                .toUri();

        return new RequestEntity(params, httpHeaders, HttpMethod.POST, uri);
    }
}