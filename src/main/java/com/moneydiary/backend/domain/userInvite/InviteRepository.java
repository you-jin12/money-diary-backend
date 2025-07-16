package com.moneydiary.backend.domain.userInvite;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class InviteRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Invite invite){
        em.persist(invite);
    }

    public Invite findById(Long id){
        return em.find(Invite.class, id);
    }

    public List<Invite> findByUser(Long userId){
        List<Invite> result = em.createQuery("select i from Invite i left join fetch i.user left join fetch i.group where i.user.id=:userId",Invite.class)
                .setParameter("userId", userId)
                .getResultList();
        return result;
    }

    public List findByUserIdAndGroupId(Long userId,Long groupId){
        List<Invite> result = em.createQuery("select i from Invite i where i.user.id=:userId and i.group.id=:groupId", Invite.class)
                .setParameter("userId", userId)
                .setParameter("groupId", groupId)
                .getResultList();
        return result;
    }

    public List<Invite> findByGroup(Long groupId) {
        List<Invite> result = em.createQuery("select i from Invite i left join fetch i.user where i.group.id=:groupId", Invite.class)
                .setParameter("groupId", groupId)
                .getResultList();
        return result;
    }
}
