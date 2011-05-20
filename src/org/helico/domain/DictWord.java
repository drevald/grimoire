package org.helico.domain;

import javax.persistence.*;
import org.helico.domain.Word;

@Entity
@Table(name = "dict_word")
public class DictWord {

    @Id
	@Column(name = "id")
	@GeneratedValue
	private Long id;

    @Column(name = "dict_id")
	private Long dictId;

    @Column(name = "word_id")
	private Long wordId;

    @Column(name = "counter")
	private Long counter = 0L;

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

    @ManyToOne
    @JoinColumn(name="word_id", nullable=false)
    public Word getWord() {
	return word;
    }
    

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public Long getCounter() {
        return counter;
    }

    public void setCounter(Long counter) {
        this.counter = counter;
    }

}
