package com.moneydiary.backend.domain.usergroup;

import com.moneydiary.backend.domain.user.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class UserGroupRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(UserGroup userGroup){
        em.persist(userGroup);
    }

    public UserGroup findById(Long id){
        return em.find(UserGroup.class,id);
    }

    public void delete(Long id){
        em.createQuery("delete from UserGroup u where u.id=:id")
                .setParameter("id",id)
                .executeUpdate();
    }

    public List<UserGroup> findByGroupId(Long groupId){ //testX
        List<UserGroup> userGroupList = em.createQuery("select u from UserGroup u where u.group= :groupId", UserGroup.class)
                .setParameter("groupId", groupId)
                .getResultList();
        return userGroupList;
    }

    public UserGroup findByUserIdAndGroupId(Long userId,Long groupId){
        UserGroup result = em.createQuery("select u from UserGroup u where u.user.id=:userId and u.group.id=:groupId", UserGroup.class)
                .setParameter("userId", userId)
                .setParameter("groupId", groupId)
                .getSingleResult();
        return result;
    }

    public List<UserGroup> existUserGroupByUserIdAndGroupId(Long userId,Long groupId){
        List<UserGroup> result = em.createQuery("select u from UserGroup u where u.user.id=:userId and u.group.id=:groupId", UserGroup.class)
                .setParameter("userId", userId)
                .setParameter("groupId", groupId)
                .getResultList();
        return result;
    }

    public List<UserGroup> getUsers(Long groupId) {
        List<UserGroup> result = em.createQuery("select ug from UserGroup ug left join fetch ug.user u where ug.group.id=:groupId", UserGroup.class)
                .setParameter("groupId", groupId)
                .getResultList();
        return result;
    }

    public List<UserGroup> getGroups(Long userId){
        List<UserGroup> result = em.createQuery("select ug from UserGroup ug left join fetch ug.group g where ug.user.id=:userId", UserGroup.class)
                .setParameter("userId", userId)
                .getResultList();
        return result;
    }
}
