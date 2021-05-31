package ru.itis.client;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ru.itis.client.WebSecurityConfig.DEFAULT_FORM_DATA_MEDIA_TYPE;

public class OAuth2UserInfoHttpMessageConverter implements HttpMessageConverter<Map<String, Object>> {

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.singletonList(DEFAULT_FORM_DATA_MEDIA_TYPE);
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public Map<String, Object> read(
            Class<? extends Map<String, Object>> clazz,
            HttpInputMessage inputMessage
    ) throws HttpMessageNotReadableException {
        return null;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        if (!Map.class.isAssignableFrom(clazz)) {
            return false;
        }

        if (mediaType == null || MediaType.ALL.equals(mediaType)) {
            return true;
        }

        for (MediaType supportedMediaType : getSupportedMediaTypes()) {
            if (supportedMediaType.isCompatibleWith(mediaType)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void write(Map<String, Object> formData,
                      MediaType contentType,
                      HttpOutputMessage outputMessage
    ) throws HttpMessageNotWritableException, IOException {

        contentType = DEFAULT_FORM_DATA_MEDIA_TYPE;

        outputMessage.getHeaders().setContentType(contentType);

        Charset charset = contentType.getCharset();

        byte[] bytes = serializeForm(formData, charset).getBytes(charset);

        outputMessage.getHeaders().setContentLength(bytes.length);

        if (outputMessage instanceof StreamingHttpOutputMessage) {
            StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
            streamingOutputMessage.setBody(outputStream -> StreamUtils.copy(bytes, outputStream));
        } else {
            StreamUtils.copy(bytes, outputMessage.getBody());
        }
    }

    private String serializeForm(Map<String, Object> formData, Charset charset) {
        StringBuilder builder = new StringBuilder();

        formData.forEach((name, value) -> {
            if (name == null) {
                return;
            }
            try {
                if (builder.length() != 0) {
                    builder.append('&');
                }

                builder.append(URLEncoder.encode(name, charset.name()));

                if (value != null) {
                    builder.append('=');
                    builder.append(URLEncoder.encode(String.valueOf(value), charset.name()));
                }
            } catch (UnsupportedEncodingException ex) {
                throw new IllegalStateException(ex);
            }
        });

        return builder.toString();
    }
}