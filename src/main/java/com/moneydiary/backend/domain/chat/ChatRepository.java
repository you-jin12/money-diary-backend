package com.moneydiary.backend.domain.chat;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ChatRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(ChatMessage chatMessage){
        em.persist(chatMessage);
        return chatMessage.getId();
    }

    public ChatMessage findById(Long id){
        ChatMessage chatMessage = em.find(ChatMessage.class, id);
        return chatMessage;
    }

    public ChatMessage findByIdWithFetchJoin(Long id){
        String query="select c from ChatMessage c left join fetch c.userGroup ug " +
                "left join fetch ug.user u " +
                "left join fetch ug.group g " +
                "where c.id=:id";
        ChatMessage chatMessage = em.createQuery(query, ChatMessage.class)
                .setParameter("id", id)
                .getSingleResult();
        return chatMessage;
    }

    public List getChatList(Long id){
        List<ChatMessage> chatList = em.createQuery("select c from ChatMessage c join c.userGroup ug join ug.group g where g.id=:id order by c.postDate asc", ChatMessage.class)
                .setParameter("id", id)
                .getResultList();
        return chatList;
    }
}

/*
"select c from ChatMessage c left join fetch m.userGroup ug " +
        "left join fetch ug.user u " +
        "left join fetch ug.group g " +
        "where g.id=:id order by c.postDate asc"*/
