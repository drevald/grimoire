package org.helico.domain;

import javax.persistence.*;

@Entity
@Table(name = "account_lang")
public class AccountLang {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "lang_id")
    private String langId;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="account_id")
    private Account account;

    public AccountLang() {    }

    public AccountLang(String langId, Account account) {
        this.langId = langId;
        this.account = account;
    }

    public String getDictId() {
        return langId;
    }

    public void setDictId(Long dictId) {
        this.langId = langId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Long getId() {return id;}

}
