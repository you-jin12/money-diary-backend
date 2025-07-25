package com.moneydiary.backend.domain.chat;

import com.moneydiary.backend.domain.chat.dto.ExpenseChatMessageResponse;
import com.moneydiary.backend.domain.expense.Expense;
import com.moneydiary.backend.domain.group.Group;
import com.moneydiary.backend.domain.user.User;
import com.moneydiary.backend.domain.usergroup.UserGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ExpenseChatService {

    private final ExpenseChatRepository expenseChatRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void createExpenseChat(Expense expense){
        User findUser = expense.getUser();
        List<UserGroup> userGroupList = findUser.getUserGroupList();
        LocalDateTime now = LocalDateTime.now();
        for (UserGroup userGroup : userGroupList) {
            Group findGroup = userGroup.getGroup();
            ExpenseChatMessage expenseChatMessage = new ExpenseChatMessage(expense, userGroup, now, null);
            expenseChatRepository.save(expenseChatMessage);
            //소켓에 연결 되어 있는 채팅방에 지출내역 메시지 전송
            ExpenseChatMessageResponse expenseChatMessageResponse = new ExpenseChatMessageResponse(expenseChatMessage.getId(), findUser.getId(), findUser.getNickName(), findUser.getProfileImg(), findGroup.getId(), expense.getItem(), expense.getExpenseMoney(), expense.getMemo(), expense.getExpenseDate(), expenseChatMessage.getPostDate());
            messagingTemplate.convertAndSend("/room/messages/"+userGroup.getGroup().getId(),expenseChatMessageResponse);
        }
    }

}
