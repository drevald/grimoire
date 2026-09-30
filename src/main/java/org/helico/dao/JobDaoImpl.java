package org.helico.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.helico.domain.Job;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class JobDaoImpl implements JobDao {

    private static Logger LOG = LoggerFactory.getLogger(JobDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    public Job find(Long id) {
    LOG.info(">>>>find job#" + id);
        Job job = entityManager.find(Job.class, id);
    LOG.info("<<<<found job#" + id);
        return job;
    }

    public void saveOrUpdate(Job job) {
        LOG.info(">>>>save job sess#"+entityManager.hashCode()+" " + job.toString());
        if (job.getId() == null) {
            entityManager.persist(job);
        } else {
            entityManager.merge(job);
        }
        LOG.info("<<<<saved job sess#"+entityManager.hashCode()+" " + job.toString());
    }

    @SuppressWarnings("unchecked")
    public List<Job> findActive(Long dictId) {
        //List<Job> jobs = (List<Job>)entityManager.createQuery("from Job where dictId=?1 and active=true")
        List<Job> jobs = (List<Job>)entityManager.createQuery("from Job where dictId=?1")
        .setParameter(1,dictId).getResultList();
        LOG.info("<<<<get active jobs:" + jobs);
        return jobs;
    }

    @SuppressWarnings("unchecked")
    public Job findLastOrActive(Long dictId) {
        List<Job> jobs = (List<Job>)entityManager
        .createQuery("from Job where dictId=?1 order by active desc, id desc")
        .setParameter(1,dictId).getResultList();
    LOG.debug("<<<<get last jobs:" + jobs);
    Job job = (jobs==null || jobs.isEmpty()) ? null : jobs.get(0);
    LOG.debug("<<<<last job:" + job);
    return job;
    }

    @Override
    public Map<Long, Job> findLastOrActiveByDictIds(Collection<Long> dictIds) {
        if (dictIds.isEmpty()) {
            return Map.of();
        }
        List<Job> jobs = entityManager
                .createQuery("from Job where dictId in :dicts order by active desc, id desc", Job.class)
                .setParameter("dicts", dictIds)
                .getResultList();
        return jobs.stream()
                .collect(Collectors.toMap(Job::getDictId, job -> job, (first, second) -> first));
    }

}
