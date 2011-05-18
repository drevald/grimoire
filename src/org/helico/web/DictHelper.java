package org.helico.web;

import java.util.List;

import org.helico.domain.Job;
import org.helico.domain.Dict;


public class DictHelper {

    private List<Job> jobs;

    private Dict dict;

    public List<Job> getJobs() {
	return jobs;
    }

    public void setJobs(List<Job> jobs) {
	this.jobs = jobs;
    }

    public Dict getDict() {
	return dict;
    }

    public void setDict(Dict dict) {
	this.dict=dict;
    }

}