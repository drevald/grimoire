package org.helico.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.helico.domain.Job;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.security.Security;

@Repository
public class JobDAOImpl implements JobDAO {

    private static Logger LOG = Logger.getLogger(JobDAOImpl.class);

    @Autowired
    SessionFactory sessionFactory;

    public Job find(Long id) {
	LOG.info(">>>>find job#" + id);
    	Job job = (Job)sessionFactory.getCurrentSession().get(Job.class, id);
	LOG.info("<<<<found job#" + id);
        return job;
    }

    public void saveOrUpdate(Job job) {
	    LOG.info(">>>>save job sess#"+sessionFactory.getCurrentSession().hashCode()+" " + job.toString());
		sessionFactory.getCurrentSession().saveOrUpdate(job);
	    LOG.info("<<<<saved job sess#"+sessionFactory.getCurrentSession().hashCode()+" " + job.toString());
    }

	@SuppressWarnings("unchecked") 
    public List<Job> findActive(Long dictId) {
	Session session = sessionFactory.getCurrentSession();
    	List<Job> jobs = (List<Job>)session.createQuery("from Job where dictId=?1 and active=1")
	    .setParameter(0,dictId).list();
		LOG.info("<<<<get active jobs:" + jobs);
		return jobs;
    }

	@SuppressWarnings("unchecked") 
    public Job findLastOrActive(Long dictId) {
	Session session = sessionFactory.getCurrentSession();
    	List<Job> jobs = (List<Job>)session
	    .createQuery("from Job where dictId=?1 order by active desc, id desc")
	    .setParameter(0,dictId).list();
	LOG.debug("<<<<get last jobs:" + jobs);
	Job job = (jobs==null)?null:jobs.get(0); 
	LOG.debug("<<<<last job:" + job);
	return job;
    }

}
