package org.helico.service;

import org.helico.dao.JobDAO;
import org.helico.domain.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    JobDAO jobDao;

    @Transactional
    public void save(Job job) {
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

    public Job find(Long id) {
        return jobDao.find(id);
    }




}
