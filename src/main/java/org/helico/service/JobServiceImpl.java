package org.helico.service;

import java.util.List;
import org.helico.dao.JobDao;
import org.helico.domain.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JobServiceImpl implements JobService {

    private static final Logger LOG = LoggerFactory.getLogger(JobServiceImpl.class);

    @Autowired
    JobDao jobDao;

    public Job createJob(Long transId, Long dictId) {
    Job job = new Job();
    job.setTransId(transId);
        job.setActive(false);
    job.setProgress(0);
        job.setDictId(dictId);
        jobDao.saveOrUpdate(job);
    return job;
    }

    public void save(Job job) {
        LOG.debug("saving " + job);
        jobDao.saveOrUpdate(job);
    }

    @Transactional
    public synchronized void setProgress(Long id, Integer progress) {
        Job job = jobDao.find(id);
    LOG.debug(">>>setting progress for job"+job+":"+progress);
        job.setProgress(progress);
    LOG.debug(">>>setting progress for job"+job+":"+progress);
        jobDao.saveOrUpdate(job);
    }

    @Transactional
    public void setActive(Long id, Boolean active) {
        Job job = jobDao.find(id);
        job.setActive(active);
        jobDao.saveOrUpdate(job);
    }

    @Transactional
    public void setDetails(Long id, String details) {
        Job job = jobDao.find(id);
        job.setDetails(details);
        jobDao.saveOrUpdate(job);
    }

    public Job find(Long id) {
        return jobDao.find(id);
    }

    public List<Job> getActiveJobs(Long dictId) {
    return jobDao.findActive(dictId);
    }

    public Job getLastOrActive(Long dictId) {
    return jobDao.findLastOrActive(dictId);
    }

}
