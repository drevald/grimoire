package org.helico.domain;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "accountname")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "native_lang_id")
    private String nativeLangId;

//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "account")
//    private Set<AccountLang> accountLangs;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "account_lang",
            joinColumns = {@JoinColumn(name = "account_id")},
            inverseJoinColumns = {@JoinColumn(name = "lang_id")})
    private Set<Lang> accountLangs;

    public Account() {

    }

    public Account(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public Account(String name, String password, String nativeLangId, Set<Lang> accountLangs) {
        this.name = name;
        this.password = password;
        this.nativeLangId = nativeLangId;
        this.accountLangs = accountLangs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNativeLangId() {return nativeLangId;}

    public void setNativeLangId(String nativeLangId) {this.nativeLangId = nativeLangId;}

    public Set<Lang> getAccountLangs() {return accountLangs;}

    public void setAccountLangs(Set<Lang> accountLangs) {this.accountLangs = accountLangs;}

}

