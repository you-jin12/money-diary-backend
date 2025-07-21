package com.moneydiary.backend.domain.expense;

import com.moneydiary.backend.domain.expense.dto.CreateExpenseRequest;
import com.moneydiary.backend.domain.expense.dto.UpdateExpenseRequest;
import com.moneydiary.backend.domain.user.User;
import com.moneydiary.backend.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserService userService;

    private final Validation validation;

    /**
     * 지출 내역 작성 로직
     */
    public void createExpense(Long sessionUserId,CreateExpenseRequest createExpenseRequest){
        Long userId = validation.validUser(sessionUserId, createExpenseRequest.getUserId());
        if(createExpenseRequest.getExpenseMoney()>0 && createExpenseRequest.getIncomeMoney()>0){
            throw new RuntimeException("지출과 수입은 동시에 입력할 수 없습니다.");
        }
        log.info("expenseMoney={}",createExpenseRequest.getExpenseMoney());
        User findUser = userService.findById(userId);
        Expense expense = Expense.builder()
                .item(createExpenseRequest.getItem())
                .expenseMoney(createExpenseRequest.getExpenseMoney())
                .incomeMoney(createExpenseRequest.getIncomeMoney())
                .memo(createExpenseRequest.getMemo())
                .expenseDate(createExpenseRequest.getExpenseDate())
                .createDate(LocalDateTime.now())
                .user(findUser)
                .build();

        expenseRepository.save(expense);
    }

    private Long validUser(Long sessionUserId, Long userId) {
        if(sessionUserId==userId){
            return sessionUserId;
        }else{
            throw new RuntimeException("올바르지 않은 요청입니다.");
        }
    }

    /**
     * 지출 내역 수정 로직
     */
    public void updateExpense(Long id,UpdateExpenseRequest updateExpenseRequest){
        Expense findExpense = this.findById(id);
        if(updateExpenseRequest.getItem()!=null) findExpense.updateItem(updateExpenseRequest.getItem());
        if(findExpense.getExpenseMoney() != updateExpenseRequest.getExpenseMoney()) findExpense.updateExpenseMoney(updateExpenseRequest.getExpenseMoney());
        if(findExpense.getIncomeMoney() != updateExpenseRequest.getIncomeMoney()) findExpense.updateIncomeMoney(updateExpenseRequest.getIncomeMoney());
        if(updateExpenseRequest.getMemo() != null) findExpense.updateMemo(updateExpenseRequest.getMemo());
        if(findExpense.getExpenseDate() != updateExpenseRequest.getExpenseDate()) findExpense.updateExpenseDate(updateExpenseRequest.getExpenseDate());
        findExpense.updateExpenseDate(LocalDate.now());
    }

    /**
     * 지출 내역 삭제 로직
     * @param ids
     */
    public void deleteExpense(List<Long> ids){
        for (Long id : ids) {
            expenseRepository.deleteExpense(id);
        }
    }


    /**
     * 월의 각 일자별 총 지출 금액 조회
     * @param userId
     * @param expenseDate
     * @return
     */
    public List getTotalDateExpense(Long userId,Long sessionUserId,LocalDate expenseDate){
        User user = userService.findById(validUser(sessionUserId, userId));
        int month = getMonth(expenseDate);
        int year = getYear(expenseDate);
        return expenseRepository.getTotalDateExpense(user.getId(),month,year);
    }

    private int getMonth(LocalDate date){
        Month month = date.getMonth();
        log.info("month.getValue()={}",month.getValue());
        log.info("month.toString={}",month.toString());
        log.info("month={}",month);
        return month.getValue();
    }
    private int getYear(LocalDate date){
        int year = date.getYear();
        return year;
    }
    public Expense findById(Long id){
        Expense findExpense = expenseRepository.findById(id);
        if(findExpense==null){
            new RuntimeException("지출내역이 존재하지 않습니다.");
        }
        return findExpense;
    }

    /**
     * 일자별 지출 내역 조회
     * @param userId
     * @param expenseDate
     * @return
     */
    public List<Expense> getExpenseByDate(Long userId,Long sessionUserId, LocalDate expenseDate) {
        User findUser = userService.findById(validUser(sessionUserId, userId));
        log.info("expenseDate.toString()={}",expenseDate.toString());
        List expenseByDate = expenseRepository.getExpenseByDate(findUser.getId(),expenseDate);
        return expenseByDate;
    }
}
