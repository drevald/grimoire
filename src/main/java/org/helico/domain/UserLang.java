package org.helico.domain;

import javax.persistence.*;

@Entity
@Table(name = "user_lang")
public class UserLang {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "lang_id")
    private String langId;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="user_id")
    private User user;

    public UserLang() {    }

    public UserLang(String langId, User user) {
        this.langId = langId;
        this.user = user;
    }

    public String getDictId() {
        return langId;
    }

    public void setDictId(Long dictId) {
        this.langId = langId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {return id;}

}
