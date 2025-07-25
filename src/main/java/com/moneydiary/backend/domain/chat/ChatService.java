package com.moneydiary.backend.domain.chat;

import com.moneydiary.backend.domain.chat.dto.ChatMessageResponse;
import com.moneydiary.backend.domain.chat.dto.ChatRequest;
import com.moneydiary.backend.domain.chat.dto.ChatResponse;
import com.moneydiary.backend.domain.chat.dto.ExpenseChatMessageResponse;
import com.moneydiary.backend.domain.expense.Expense;
import com.moneydiary.backend.domain.group.Group;
import com.moneydiary.backend.domain.user.User;
import com.moneydiary.backend.domain.usergroup.UserGroup;
import com.moneydiary.backend.domain.usergroup.UserGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {

    private final UserGroupService userGroupService;
    private final ChatRepository chatRepository;

    private final ExpenseChatRepository expenseChatRepository;


    public Long createChat(ChatRequest chatRequest){
        log.info("userId={}, groupId={}",chatRequest.getUserId(),chatRequest.getGroupId());
        UserGroup findUserGroup = userGroupService.findByUserIdAndGroupId(chatRequest.getGroupId(), chatRequest.getUserId());
        ChatMessage chatMessage = new ChatMessage(chatRequest.getMessage(), findUserGroup, LocalDateTime.now());
        Long chatId = chatRepository.save(chatMessage);
        return chatId;
    }

    public ChatMessage findById(Long id){
        ChatMessage findChat = chatRepository.findById(id);
        if(findChat==null){
            throw new RuntimeException("해당 채팅은 존재하지 않습니다.");
        }
        return findChat;
    }

    public ChatMessage findByIdwithFetchJoin(Long id){
        ChatMessage findChat = chatRepository.findByIdWithFetchJoin(id);
        return findChat;
    }


    /**
     * 채팅 리스트(ChatMessage * ExpenseChatMessage)
     * @param groupId
     * @param userId
     * @return
     */
    public List<ChatResponse> getChatList(Long groupId,Long userId){

        //validation(유저가 그룹에 소속되어 있는지 확인)
        UserGroup findUserGroup = userGroupService.findByUserIdAndGroupId(groupId,userId);
        Group group = findUserGroup.getGroup();

        List<ChatMessage> chatList = chatRepository.getChatList(groupId);
        List<ExpenseChatMessage> expenseChatList = expenseChatRepository.getExpenseChatList(groupId);

        List<ChatResponse> list=new ArrayList<>();

        for (ChatMessage chatMessage : chatList) {
            User user = chatMessage.getUserGroup().getUser();
            list.add(new ChatMessageResponse(chatMessage.getId(),user.getId(),user.getNickName(),user.getProfileImg(),groupId,chatMessage.getContent(),chatMessage.getPostDate()));
        }
        for (ExpenseChatMessage expenseChatMessage : expenseChatList) {
            User user = expenseChatMessage.getUserGroup().getUser();
            Expense expense = expenseChatMessage.getExpense();
            list.add(new ExpenseChatMessageResponse(expenseChatMessage.getId(),user.getId(),user.getNickName(),user.getProfileImg(),groupId,expense.getItem(),expense.getExpenseMoney(),expense.getMemo(),expense.getExpenseDate(),expenseChatMessage.getPostDate()));
        }

        //postDate로 오름차순 정렬
        list.sort((e1,e2)-> e1.getPostDate().compareTo(e2.getPostDate()));
        return list;
    }

}
