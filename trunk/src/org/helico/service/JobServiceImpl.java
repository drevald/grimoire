package org.helico.service;

import org.helico.dao.JobDAO;
import org.helico.domain.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.log4j.Logger;

@Service
public class JobServiceImpl implements JobService {

    private static final Logger LOG = Logger.getLogger(JobServiceImpl.class);

    @Autowired
    JobDAO jobDao;

    @Transactional
    public Job createJob(Long transId, Long dictId) {
	Job job = new Job();
	job.setTransId(transId);
        job.setActive(false);
	job.setProgress(0);
        job.setDictId(dictId);
        jobDao.saveOrUpdate(job);
	return job;
    }

    @Transactional
    public void save(Job job) {
	LOG.debug("saving " + job);
        jobDao.saveOrUpdate(job);
    }

    @Transactional
    public void setProgress(Long id, Integer progress) {
        Job job = jobDao.find(id);
        job.setProgress(progress);
        jobDao.saveOrUpdate(job);
    }

    @Transactional
    public void setActive(Long id, Boolean active) {
        Job job = jobDao.find(id);
        job.setActive(active);
        jobDao.saveOrUpdate(job);
    }

    @Transactional
    public Job find(Long id) {
        return jobDao.find(id);
    }




}
