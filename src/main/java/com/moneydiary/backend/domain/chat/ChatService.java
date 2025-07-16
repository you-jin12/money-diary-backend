package com.moneydiary.backend.domain.chat;

import com.moneydiary.backend.domain.chat.dto.ChatRequest;
import com.moneydiary.backend.domain.chat.dto.ChatResponse;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {

    private final UserGroupService userGroupService;
    private final ChatRepository chatRepository;


    public Long createChat(ChatRequest chatRequest){
        log.info("userId={}, groupId={}",chatRequest.getUserId(),chatRequest.getGroupId());
        UserGroup findUserGroup = userGroupService.findByUserIdAndGroupId(chatRequest.getGroupId(), chatRequest.getUserId());
        ChatMessage chatMessage = new ChatMessage(chatRequest.getMessage(), findUserGroup, LocalDateTime.now());
        Long chatId = chatRepository.save(chatMessage);
        return chatId;
    }

    private Long validUser(Long sessionUserId, Long userId) {
        if(sessionUserId==userId){
            return sessionUserId;
        }else{
            throw new RuntimeException("올바르지 않은 요청입니다.");
        }
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

    public List<ChatResponse> getChatList(Long groupId,Long userId){

        //validation(유저가 그룹에 소속되어 있는지 확인)
        UserGroup findUserGroup = userGroupService.findByUserIdAndGroupId(groupId,userId);
        Group group = findUserGroup.getGroup();
        log.info("findUserGroup.getGroup().getId()={}",findUserGroup.getGroup());
        log.info("group Id={}",group.getId());

        List<ChatMessage> chatList = chatRepository.getChatList(groupId);

        List<ChatResponse> list=new ArrayList<>();
        for (ChatMessage chatMessage : chatList) {
            User user = chatMessage.getUserGroup().getUser();
            list.add(new ChatResponse(user.getId(),user.getNickName(),user.getProfileImg(),groupId,chatMessage.getContent(),chatMessage.getPostDate()));
        }
        return list;
    }
}
