package com.moneydiary.backend.domain.expense;

import com.moneydiary.backend.common.Validation;
import com.moneydiary.backend.domain.chat.ExpenseChatService;
import com.moneydiary.backend.domain.expense.dto.CreateExpenseRequest;
import com.moneydiary.backend.domain.expense.dto.DateExpense;
import com.moneydiary.backend.domain.expense.dto.ExpenseByDateResponse;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserService userService;
    private final ExpenseChatService expenseChatService;

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
        //지출내역채팅 생성 로직 호출
        expenseChatService.createExpenseChat(expense);
    }


    /**
     * 지출 내역 수정 로직
     */
    public void updateExpense(Long userId,Long expenseId,UpdateExpenseRequest updateExpenseRequest){
        Expense findExpense = this.findById(expenseId);
        if(findExpense.getUser().getId()!= userId){
            throw new RuntimeException("올바르지 않은 요청입니다.");
        }
        if(!updateExpenseRequest.getItem().equals(findExpense.getItem())) findExpense.updateItem(updateExpenseRequest.getItem());
        if(findExpense.getExpenseMoney() != updateExpenseRequest.getExpenseMoney()) findExpense.updateExpenseMoney(updateExpenseRequest.getExpenseMoney());
        if(findExpense.getIncomeMoney() != updateExpenseRequest.getIncomeMoney()) findExpense.updateIncomeMoney(updateExpenseRequest.getIncomeMoney());
        if(!updateExpenseRequest.getMemo().equals(findExpense.getMemo())) findExpense.updateMemo(updateExpenseRequest.getMemo());
        if(findExpense.getExpenseDate() != updateExpenseRequest.getExpenseDate()) findExpense.updateExpenseDate(updateExpenseRequest.getExpenseDate());
        findExpense.changeUpdateDate(LocalDateTime.now());
    }

    /**
     * 지출 내역 다건 삭제
     * @param ids
     */
    public void deleteExpense(List<Long> ids){
        for (Long id : ids) {
            expenseRepository.deleteExpense(id);
        }
    }

    /**
     * 지출 내역 단건 삭제
     * @param id
     */
    public void deleteExpense(Long id){
        Expense findExpense = findById(id);
        expenseRepository.deleteExpense(id);
    }


    /**
     * 월의 각 일자별 총 지출 금액 조회
     * @param userId
     * @param expenseDate
     * @return
     */
    public List<DateExpense> getTotalDateExpense(Long userId,Long sessionUserId,LocalDate expenseDate){
        User user = userService.findById(validation.validUser(sessionUserId, userId));
        int month = getMonth(expenseDate);
        int year = getYear(expenseDate);
        List<DateExpense> totalDateExpense = expenseRepository.getTotalDateExpense(user.getId(), month, year);
        return totalDateExpense;
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
    public List<ExpenseByDateResponse> getExpenseByDate(Long userId,Long sessionUserId, LocalDate expenseDate) {
        User findUser = userService.findById(validation.validUser(sessionUserId, userId));
        log.info("expenseDate.toString()={}",expenseDate.toString());
        List<Expense> expenseByDate = expenseRepository.getExpenseByDate(findUser.getId(),expenseDate);
        List<ExpenseByDateResponse> list=new ArrayList();
        for (Expense expense : expenseByDate) {
            ExpenseByDateResponse expenseByDateResponse = new ExpenseByDateResponse(expense.getId(), expense.getItem(), expense.getExpenseMoney(), expense.getIncomeMoney(), expense.getMemo(), expense.getExpenseDate());
            list.add(expenseByDateResponse);
        }
        return list;
    }
}
