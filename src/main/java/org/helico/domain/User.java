package org.helico.domain;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {

	@Id
	@Column(name = "id")
	@GeneratedValue
	private Long id;

	@Column(name = "username")
	private String name;

    @Column(name = "password")
	private String password;

	@Column(name = "native_lang_id")
	private String nativeLangId;

//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
//    private Set<UserLang> userLangs;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_lang",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "lang_id")})
    private Set<Lang> userLangs;

    public User() {

    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public User(String name, String password, String nativeLangId, Set<Lang> userLangs) {
        this.name = name;
        this.password = password;
        this.nativeLangId = nativeLangId;
        this.userLangs = userLangs;
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

    public Set<Lang> getUserLangs() {return userLangs;}

    public void setUserLangs(Set<Lang> userLangs) {this.userLangs = userLangs;}

}

