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
    	Job job = (Job)sessionFactory.getCurrentSession().createQuery("from Dict where id=?")
                .setLong(0, id).uniqueResult();
        return job;
    }

    public void saveOrUpdate(Job job) {
	    LOG.info("save sess#"+sessionFactory.getCurrentSession().hashCode()+" " + job.toString());
		sessionFactory.getCurrentSession().saveOrUpdate(job);
    }

}
