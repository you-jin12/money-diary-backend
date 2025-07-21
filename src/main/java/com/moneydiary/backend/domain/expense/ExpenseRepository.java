package com.moneydiary.backend.domain.expense;

import com.moneydiary.backend.domain.expense.dto.DateExpense;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Repository
public class ExpenseRepository {

    @PersistenceContext
    EntityManager em;

    public void save(Expense expense){
        em.persist(expense);
    }

    public Expense findById(Long id){
        return em.find(Expense.class,id);
    }

    public List<Expense> findAll(){
        return em.createQuery("select e from Expense e",Expense.class)
                .getResultList();
    }

    public void deleteExpense(Long id){
        em.createQuery("delete from Expense e where e.id=:expense_id")
                .setParameter("expense_id",id)
                .executeUpdate();
    }
    public List<Expense> findByExpenseDate(Long userId,LocalDateTime expenseDate){
        String query="select e from Expense e where e.user=:userId and e.expenseDate=:expenseDate";
        List<Expense> resultList = em.createQuery(query, Expense.class)
                .setParameter("userId", userId)
                .setParameter("expenseDate", expenseDate)
                .getResultList();
        return  resultList;
    }


    public List getTotalDateExpense(Long userId, int month, int year) {
        String query="select new com.moneydiary.backend.domain.expense.dto.DateExpense(day(e.expenseDate) ,sum(e.expenseMoney)) " +
                    "from Expense e " +
                    "where e.user.id=:userId and Year(e.expenseDate)=:year and Month(e.expenseDate)=:month " +
                    "group by day(e.expenseDate)";
        List<DateExpense> resultList = em.createQuery(query,DateExpense.class)
                .setParameter("userId", userId)
                .setParameter("year", year)
                .setParameter("month", month)
                .getResultList();

        return resultList;
    }

    public List<Expense> getExpenseByDate(Long userId, LocalDate expenseDate){
        String query="select e from Expense e where e.user.id=:userId and e.expenseDate=:expenseDate";
        List<Expense> resultList = em.createQuery(query, Expense.class)
                .setParameter("userId", userId)
                .setParameter("expenseDate", expenseDate)
                .getResultList();
        return resultList;
    }
}
