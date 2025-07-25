package com.moneydiary.backend.domain.chat;

import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ExpenseChatRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(ExpenseChatMessage expenseChatMessage){
        em.persist(expenseChatMessage);
    }

    public List<ExpenseChatMessage> getExpenseChatList(Long groupId){
        List<ExpenseChatMessage> chatList = em.createQuery("select ec from ExpenseChatMessage ec join ec.userGroup ug where ug.group.id=:groupId order by ec.postDate asc", ExpenseChatMessage.class)
                .setParameter("groupId", groupId)
                .getResultList();
        return chatList;
    }
}
