package org.helico.service;

import org.helico.domain.Dict;
import org.helico.domain.Job;

public record DictWithLastJob (Dict dict, Job job)  {
        public Dict getDict() { return this.dict; }
        public Job getLastJob() { return this.job; }
}
