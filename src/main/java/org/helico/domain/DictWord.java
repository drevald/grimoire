package org.helico.domain;

import javax.persistence.*;

@Entity
@Table(name = "dict_word")
public class DictWord {

    @Id
	@Column(name = "id")
	@GeneratedValue
	private Long id;

    @Column(name = "dict_id")
	private Long dictId;

    @Column(name = "counter")
	private Long counter = 0L;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="word_id")
    private Word word;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDictId() {
        return dictId;
    }

    public void setDictId(Long dictId) {
        this.dictId = dictId;
    }


    public Word getWord() {
	    return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public Long getCounter() {
        return counter;
    }

    public void setCounter(Long counter) {
        this.counter = counter;
    }

}
