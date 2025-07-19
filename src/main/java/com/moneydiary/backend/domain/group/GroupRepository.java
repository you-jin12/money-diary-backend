package com.moneydiary.backend.domain.group;

import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class GroupRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Group group){
        em.persist(group);
    }

    public Group findById(Long id){
        return em.find(Group.class, id);
    }

    public void delete(Long id){
        em.createQuery("delete from Group g where g.id=:id")
                .setParameter("id",id)
                .executeUpdate();
    }

    public List<Group> getGroupList() {
        List<Group> resultList = em.createQuery("select g from Group g order by g.createDate desc", Group.class)
                .setFirstResult(0)
                .setMaxResults(100)
                .getResultList();
        return resultList;


    }
}
