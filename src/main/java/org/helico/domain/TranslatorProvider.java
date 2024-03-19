package org.helico.domain;

import javax.persistence.*;
import java.util.Set;

/**

http://api.microsofttranslator.com/V2/Http.svc/Translate?appId=CDCB8BFFDD9E4C3054316BC629E82D1E39CA585C&text=dog&from=en&to=ru

<string xmlns="http://schemas.microsoft.com/2003/10/Serialization/">
собака
</string>

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
    @GeneratedValue
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "host")
    private String host;

    @Column(name = "req_pattern")
    private String reqPattern;

    @Column(name = "res_pattern")
    private String resPattern;

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

}
