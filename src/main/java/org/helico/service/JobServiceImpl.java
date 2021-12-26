package org.helico.service;

import java.util.List;
import org.helico.dao.JobDAO;
import org.helico.domain.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Service
public class JobServiceImpl implements JobService {

    protected static final Logger LOG = LogManager.getLogger(JobServiceImpl.class);

    @Autowired
    JobDAO jobDao;

    
    public Job createJob(Long transId, Long dictId) {
	Job job = new Job();
	job.setTransId(transId);
        job.setActive(false);
	job.setProgress(0);
        job.setDictId(dictId);
        jobDao.save(job);
	return job;
    }

    
    public void save(Job job) {
	LOG.debug("saving " + job);
        jobDao.saveAndFlush(job);
    }

    
    public synchronized void setProgress(Long id, Integer progress) {
        Job job = jobDao.findById(id).get();
	LOG.debug(">>>setting progress for job"+job+":"+progress);
        job.setProgress(progress);
	LOG.debug(">>>setting progress for job"+job+":"+progress);
        jobDao.save(job);
    }

    
    public void setActive(Long id, Boolean active) {
        Job job = jobDao.findById(id).get();
        job.setActive(active);
        jobDao.save(job);
    }

    
    public void setDetails(Long id, String details) {
        Job job = jobDao.findById(id).get();
        job.setDetails(details);
        jobDao.save(job);
    }

    
    public Job find(Long id) {
        return jobDao.findById(id).get();
    }

    
    public List<Job> getActiveJobs(Long dictId) {
	return jobDao.findActive(dictId);
    }
    
    
    public Job getLastOrActive(Long dictId) {

        return jobDao.findFirstByDictIdOrderByIdDesc(dictId);

//        if (jobDao.findAll().isEmpty())
//            return null;
//        return jobDao.findAll().iterator().next();
	    //return jobDao.findActive(dictId).get(1);
    }

}
