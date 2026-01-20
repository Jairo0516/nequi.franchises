package com.nequi.franchises.infrastructure.rest;

import com.nequi.franchises.infrastructure.rest.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ApiResponseBodyAdvice implements ResponseBodyAdvice<Object> {

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
        // Evitar envolver Swagger/OpenAPI
        String path = extractPath(request);
        if (path != null && (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui"))) {
            return body;
        }

        if (body instanceof ApiResponse<?>) {
            return body;
        }

        int status = HttpStatus.OK.value();
        if (response != null && response.getHeaders() != null) {
            //OK por defecto
        }

        // Mensaje por default basado en el response status; si no, retorna OK
        String message = "OK";

        return ApiResponse.of(status, message, body);
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
