package com.moneydiary.backend.domain.user;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.*;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager em;


    public void save(User user){
        em.persist(user);
    }

    public Optional<User> findByUserId(String userId){
        return findAll().stream()
                .filter(user->user.getUserId().trim().equals(userId.trim()))
                .findFirst();
    }

    public User findById(Long id){
        return em.find(User.class,id);
    }
    public List<User> findAll(){
        return em.createQuery("select u from User u",User.class)
                .getResultList();
    }

    public void delete(Long id){
        em.createQuery("delete from User u where u.id=:id")
                .setParameter("id",id)
                .executeUpdate();
    }

    public List<User> findByKeyword(String keyword){
        List<User> resultList = em.createQuery("select u " +
                        "from User u " +
                        "where u.userId like concat('%',:keyword,'%') or u.nickName like concat('%',:keyword,'%')", User.class)
                .setParameter("keyword", keyword.trim())
                .getResultList();
        return resultList;

    }
}