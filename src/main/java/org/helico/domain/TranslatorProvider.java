package org.helico.domain;

import jakarta.persistence.*;
import java.util.Set;

/**
 * Translation Provider entity
 *
 * Configuration fields:
 * - req_pattern: Request URL template (for both GET and POST)
 * - res_pattern: Response parser pattern (to extract translation from response)
 * - request_body: Request body template (for POST only)
 * - headers: Custom HTTP headers (for both GET and POST)
 *
 * Example configuration:
 * Method: GET
 * req_pattern: http://api.example.com/translate?text={0}&from={1}&to={2}
 * res_pattern: {0}
 *
 * Method: POST
 * req_pattern: https://api.example.com/translate
 * request_body: [{"text":"{0}"}]
 * res_pattern: {0}
 * headers: Authorization: Bearer TOKEN
 */
@Entity
@Table(name = "translator_provider")
public class TranslatorProvider {

//  `id` BIGINT NOT NULL AUTO_INCREMENT ,
//  `title` VARCHAR(32) NULL DEFAULT NULL ,
//  `host` VARCHAR(64) NULL DEFAULT NULL ,
//  `req_pattern` VARCHAR(255) NULL DEFAULT NULL ,
//  `res_pattern` VARCHAR(255) NULL DEFAULT NULL ,

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "host")
    private String host;

    @Column(name = "req_pattern")
    private String reqPattern; // Request URL pattern template (for both GET and POST)

    @Column(name = "res_pattern")
    private String resPattern; // Response parser pattern (MessageFormat pattern to extract translation)

    @Column(name = "request_body", length = 1000)
    private String requestBody; // Request body template for POST requests (optional)

    @Column(name = "method")
    private String method; // HTTP method: GET or POST

    @Column(name = "content_type")
    private String contentType; // Content-Type for POST requests (e.g., application/json, application/x-www-form-urlencoded)

    @Column(name = "charset")
    private String charset; // Character encoding (e.g., UTF-8, ISO-8859-1)

    @Column(name = "headers", length = 1000)
    private String headers; // Custom HTTP headers (format: "Header-Name: value\nAnother-Header: value")

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "provider")
    private Set<Translator> translators;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getReqPattern() {
        return reqPattern;
    }

    public void setReqPattern(String reqPattern) {
        this.reqPattern = reqPattern;
    }

    public String getResPattern() {
        return resPattern;
    }

    public void setResPattern(String resPattern) {
        this.resPattern = resPattern;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
