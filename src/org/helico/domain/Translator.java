package org.helico.domain;

import javax.persistence.*;

@Entity
@Table(name = "translator")
public class Translator {

//CREATE TABLE translator (
//  id BIGINT NOT NULL AUTO_INCREMENT,
//  service_id BIGINT NOT NULL,
//  dest_lang_id VARCHAR(2) NOT NULL,
//  src_lang_id VARCHAR(2) NOT NULL,
//  PRIMARY KEY(id, service_id),
//  INDEX lang_translator_FKIndex1(src_lang_id),
//  INDEX lang_translator_FKIndex2(dest_lang_id),
//  INDEX lang_translators_FKIndex3(service_id)
//);

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "src_lang_id")
    private Long srcLangId;

    @Column(name = "dest_lang_id")
    private Long destLangId;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="serviceId")
    private TranslatorProvider provider;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getSrcLangId() {
        return srcLangId;
    }

    public void setSrcLangId(Long srcLangId) {
        this.srcLangId = srcLangId;
    }

    public Long getDestLangId() {
        return destLangId;
    }

    public void setDestLangId(Long destLangId) {
        this.destLangId = destLangId;
    }

}
