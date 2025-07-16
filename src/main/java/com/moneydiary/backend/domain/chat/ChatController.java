package com.moneydiary.backend.domain.chat;


import com.moneydiary.backend.domain.chat.dto.ChatRequest;
import com.moneydiary.backend.domain.chat.dto.ChatResponse;
import com.moneydiary.backend.domain.user.User;
import com.moneydiary.backend.domain.user.dto.UserSessionDTO;
import com.moneydiary.backend.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/{groupId}")
    @SendTo("/room/messages/{groupId}")
    public ChatResponse sendChatMessage(
                                @DestinationVariable String groupId, ChatRequest chatRequest){
        log.info("groupId={}",groupId);
        //유저 검증 필요(어차피 소켓은 양방향이니까 필요없나?)tcp 보안 알아볼것
        Long chatId = chatService.createChat(chatRequest);
        ChatMessage chat = chatService.findByIdwithFetchJoin(chatId);
        User chatUser = chat.getUserGroup().getUser();
        return new ChatResponse(chatUser.getId(),chatUser.getNickName(),chatUser.getProfileImg(),chat.getUserGroup().getGroup().getId(),chat.getContent(),chat.getPostDate());

    }

    //그룹의 채팅 내역 가져오기
    @GetMapping("/{groupId}")
    public ResponseEntity getChatList(@PathVariable Long groupId, @SessionAttribute("user")UserSessionDTO session){
        //해당 유저가 채팅방에 속한 유저인지 확인
        List<ChatResponse> chatList = chatService.getChatList(groupId, session.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true,chatList,"해당 그룹의 채팅 내역을 가져왔습니다."));
    }

}
