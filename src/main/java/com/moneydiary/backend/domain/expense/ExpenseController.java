package com.moneydiary.backend.domain.expense;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moneydiary.backend.domain.expense.dto.CreateExpenseRequest;
import com.moneydiary.backend.domain.expense.dto.DateExpense;
import com.moneydiary.backend.domain.expense.dto.ExpenseByDateResponse;
import com.moneydiary.backend.domain.expense.dto.UpdateExpenseRequest;
import com.moneydiary.backend.domain.user.dto.UpdateUserRequest;
import com.moneydiary.backend.domain.user.dto.UserSessionDTO;
import com.moneydiary.backend.dto.ApiResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity createExpense(@SessionAttribute("user")UserSessionDTO session,
                                        @Valid @RequestBody CreateExpenseRequest request){
        //그룹 채팅에 지출내역 알림

        expenseService.createExpense(session.getId(),request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true,"지출내역이 작성되었습니다."));
    }

    @PutMapping("/{expense_id}")
    public ResponseEntity updateExpense(@PathVariable(name="expense_id") Long id,
                                        @Valid @RequestBody UpdateExpenseRequest request){
        //그룹 채팅에 수정된 지출내역 알림

        expenseService.updateExpense(id,request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true,"지출내역이 수정 되었습니다."));
    }

    @PostMapping("/{expense_id}")
    public ResponseEntity deleteExpense(@SessionAttribute("user")UserSessionDTO session,
                                        @PathVariable(name="expense_id") Long id,
                                        @RequestBody List<Long> ids){
        expenseService.deleteExpense(ids);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true,"지출내역이 삭제 되었습니다."));
    }

    @GetMapping("/calendar/{userId}/{expenseDate}")
    public ResponseEntity getDateTotalExpensesByMonth(@SessionAttribute("user")UserSessionDTO session,
                                                      @PathVariable("userId")Long userId,
                                                      @PathVariable(name="expenseDate") @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate expenseDate){
        List dateExpense = expenseService.getTotalDateExpense(userId,session.getId(), expenseDate);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<List<DateExpense>>(true,dateExpense,"총 지출액을 가져왔습니다."));
    }

    @GetMapping("/{userId}/{expenseDate}")
    public ResponseEntity getExpenseByDate(@PathVariable(name="userId")Long userId,
                                           @SessionAttribute("user")UserSessionDTO session,
                                           @PathVariable(name="expenseDate") @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate expenseDate){
        List<Expense> expenseByDate = expenseService.getExpenseByDate(userId, session.getId(),expenseDate);
        List list=new ArrayList();
        for (Expense expense : expenseByDate) {
            ExpenseByDateResponse expenseByDateResponse = new ExpenseByDateResponse(expense.getId(), expense.getItem(), expense.getExpenseMoney(), expense.getIncomeMoney(), expense.getMemo(), expense.getExpenseDate());
            list.add(expenseByDateResponse);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<List<ExpenseByDateResponse>>(true,list,"해당 일자의 지출 내역 리스트를 가져 왔습니다."));
    }
}
