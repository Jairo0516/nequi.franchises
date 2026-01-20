package com.nequi.franchises.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nequi.franchises.infrastructure.rest.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    public ApiResponseBodyAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        String path = extractPath(request);
        // No  Swagger/OpenAPI
        if (path != null && (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui"))) {
            return body;
        }

        if (body instanceof ApiResponse<?>) {
            return body;
        }

        int status = 200;
        if (response instanceof ServletServerHttpResponse servletResponse) {
            status = servletResponse.getServletResponse().getStatus();
        }

        // Si es error, normalizamos el body default (Map) a tu estructura
        if (status >= 400) {
            String message = "Error";
            Object data = null;

            if (body instanceof Map<?, ?> m) {
                Object msg = m.get("message");
                Object err = m.get("error");

                message = (msg != null && !String.valueOf(msg).isBlank())
                        ? String.valueOf(msg)
                        : (err != null ? String.valueOf(err) : "Error");

                Map<String, Object> errData = new LinkedHashMap<>();
                // dejamos info útil, sin duplicar status/timestamp
                errData.put("error", m.get("error"));
                errData.put("path", m.get("path"));
                if (m.containsKey("details")) errData.put("details", m.get("details"));
                data = errData;
            } else {
                // si el body no es Map, lo dejamos como data
                data = body;
            }

            ApiResponse<Object> wrapped = ApiResponse.of(status, message, data);

            // Si el controller devuelve String, JSON String para no romper el converter
            if (body instanceof String) {
                try {
                    return objectMapper.writeValueAsString(wrapped);
                } catch (Exception e) {
                    return body;
                }
            }
            return wrapped;
        }

        // Éxito
        ApiResponse<Object> wrapped = ApiResponse.of(status, "OK", body);

        if (body instanceof String) {
            try {
                return objectMapper.writeValueAsString(wrapped);
            } catch (Exception e) {
                return body;
            }
        }
        return wrapped;
    }

    private String extractPath(ServerHttpRequest request) {
        try {
            if (request instanceof ServletServerHttpRequest sr) {
                HttpServletRequest r = sr.getServletRequest();
                return r.getRequestURI();
            }
        } catch (Exception ignored) {}
        return null;
    }
}
