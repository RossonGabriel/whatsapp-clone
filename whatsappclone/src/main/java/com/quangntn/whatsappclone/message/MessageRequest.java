package com.quangntn.whatsappclone.message;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRequest {

    private String content;
    private String senderId;
    private String recipientId;
    private MessageType type;
    private String chatId;

}
