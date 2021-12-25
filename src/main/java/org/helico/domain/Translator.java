package org.helico.domain;

import javax.persistence.*;

@Entity
@Table(name = "translator")
public class Translator {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "src_lang_id")
    private String srcLangId;

    @Column(name = "dest_lang_id")
    private String destLangId;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="service_id")
    private TranslatorProvider provider;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSrcLangId() {
        return srcLangId;
    }

    public void setSrcLangId(String srcLangId) {
        this.srcLangId = srcLangId;
    }

    public String getDestLangId() {
        return destLangId;
    }

    public void setDestLangId(String destLangId) {
        this.destLangId = destLangId;
    }

    public TranslatorProvider getProvider() {
        return provider;
    }

    public void setProvider(TranslatorProvider provider) {
        this.provider = provider;
    }

}
