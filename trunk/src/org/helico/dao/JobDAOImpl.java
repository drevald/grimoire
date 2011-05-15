package org.helico.dao;

import org.apache.log4j.Logger;
import org.helico.domain.Job;
import org.hibernate.SessionFactory;
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

}
