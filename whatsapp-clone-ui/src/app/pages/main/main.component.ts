import {AfterViewChecked, Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ChatListComponent} from '../../components/chat-list/chat-list.component';
import {KeycloakService} from '../../utils/keycloak/keycloak.service';
import {ChatResponse} from '../../services/models/chat-response';
import {DatePipe} from '@angular/common';
import {MessageService} from '../../services/services/message.service';
import {MessageResponse} from '../../services/models/message-response';
import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import {FormsModule} from '@angular/forms';
import {MessageRequest} from '../../services/models/message-request';
import {Notification} from './models/notification';
import {ChatService} from '../../services/services/chat.service';
import {PickerComponent} from '@ctrl/ngx-emoji-mart';
import {EmojiData} from '@ctrl/ngx-emoji-mart/ngx-emoji';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-main',
  imports: [
    ChatListComponent,
    DatePipe,
    FormsModule,
    PickerComponent
  ],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss'
})
export class MainComponent implements OnInit, OnDestroy, AfterViewChecked {

  selectedChat: ChatResponse = {};
  chats: Array<ChatResponse> = [];
  chatMessages: Array<MessageResponse> = [];
  socketClient: any = null;
  messageContent: string = '';
  showEmojis = false;
  @ViewChild('scrollableDiv') scrollableDiv!: ElementRef<HTMLDivElement>;
  private notificationSubscription: any;

  constructor(
    private chatService: ChatService,
    private messageService: MessageService,
    private keycloakService: KeycloakService,
  ) {
  }

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  ngOnDestroy(): void {
    if (this.socketClient !== null) {
      this.socketClient.disconnect();
      this.notificationSubscription.unsubscribe();
      this.socketClient = null;
    }
  }

  ngOnInit(): void {
    this.initWebSocket();
    this.getAllChats();
  }

  chatSelected(chatResponse: ChatResponse) {
    this.selectedChat = chatResponse;
    this.getAllChatMessages(chatResponse.id as string);
    this.setMessagesToSeen();
    this.selectedChat.unreadCount = 0;
  }

  isSelfMessage(message: MessageResponse): boolean {
    return message.senderId === this.keycloakService.userId;
  }

  sendMessage() {
    if (this.messageContent) {
      const messageRequest: MessageRequest = {
        chatId: this.selectedChat.id,
        senderId: this.getSenderId(),
        recipientId: this.getRecipientId(),
        content: this.messageContent,
        type: 'TEXT',
      };
      this.messageService.saveMessage({
        body: messageRequest
      }).subscribe({
        next: () => {
          const message: MessageResponse = {
            senderId: this.getSenderId(),
            recipientId: this.getRecipientId(),
            content: this.messageContent,
            type: 'TEXT',
            state: 'SENT',
            createdAt: new Date().toString()
          };
          this.selectedChat.lastMessage = this.messageContent;
          this.chatMessages.push(message);
          this.messageContent = '';
          this.showEmojis = false;
        }
      });
    }
  }

  keyDown(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.sendMessage();
    }
  }

  onSelectEmojis(emojiSelected: any) {
    const emoji: EmojiData = emojiSelected.emoji;
    this.messageContent += emoji.native;
  }

  onClick() {
    this.setMessagesToSeen();
  }

  uploadMedia(target: EventTarget | null) {
    const file = this.extractFileFromTarget(target);
    if (file !== null) {
      const reader = new FileReader();
      reader.onload = () => {
        if (reader.result) {

          const mediaLines = reader.result.toString().split(',')[1];

          this.messageService.uploadMedia({
            'chat-id': this.selectedChat.id as string,
            body: {
              file: file
            }
          }).subscribe({
            next: () => {
              const message: MessageResponse = {
                senderId: this.getSenderId(),
                recipientId: this.getRecipientId(),
                content: 'Attachment',
                type: 'IMAGE',
                state: 'SENT',
                media: [mediaLines],
                createdAt: new Date().toString()
              };
              this.chatMessages.push(message);
            }
          });
        }
      }
      reader.readAsDataURL(file);
    }
  }

  logout() {
    this.keycloakService.logout();
  }

  userProfile() {
    this.keycloakService.accountManagement();
  }

  private setMessagesToSeen() {
    this.messageService.setMessageToSeen({
      'chat-id': this.selectedChat.id as string
    }).subscribe({
      next: () => {
      }
    });
  }

  private getAllChats() {
    this.chatService.getChatsByRecipientId()
      .subscribe({
        next: (res) => {
          this.chats = res;
        }
      });  
  }

  private getAllChatMessages(chatId: string) {
    this.messageService.getMessages({
      'chat-id': chatId
    }).subscribe({
      next: (messages) => {
        this.chatMessages = messages;
      }
    });
  }

  private initWebSocket() {
    if (this.keycloakService.keycloak.tokenParsed?.sub) {
      let ws = new SockJS(`${environment.apiUrl}/ws`);
      this.socketClient = Stomp.over(ws);
      const subUrl = `/user/${this.keycloakService.keycloak.tokenParsed?.sub}/chat`;
      this.socketClient.connect({'Authorization': 'Bearer ' + this.keycloakService.keycloak.token},
        () => {
          this.notificationSubscription = this.socketClient.subscribe(subUrl,
            (message: any) => {
              const notification: Notification = JSON.parse(message.body);
              this.handleNotification(notification);
            },
            () => console.error('Error while connecting to webSocket')
          );
        }
      );
    }
  }

  private handleNotification(notification: Notification): void {
    if (!notification) return;

    if (this.isNotificationForSelectedChat(notification)) {
      this.handleSelectedChatNotification(notification);
    } else {
      this.handleNonSelectedChatNotification(notification);
    }
  }

  private isNotificationForSelectedChat(notification: Notification): boolean {
    return this.selectedChat?.id === notification.chatId;
  }

  private handleSelectedChatNotification(notification: Notification): void {
    switch (notification.type) {
      case 'MESSAGE':
      case 'IMAGE':
        this.handleNewMessage(notification);
        break;
      case 'SEEN':
        this.handleSeenStatus();
        break;
    }
  }

  private handleNewMessage(notification: Notification): void {
    const message = this.createMessageFromNotification(notification);
    this.updateSelectedChatPreview(notification);
    this.chatMessages.push(message);
  }

  private createMessageFromNotification(notification: Notification): MessageResponse {
    return {
      senderId: notification.senderId,
      recipientId: notification.recipientId,
      content: notification.content,
      type: notification.messageType,
      media: notification.media,
      createdAt: new Date().toString()
    };
  }

  private updateSelectedChatPreview(notification: Notification): void {
    this.selectedChat.lastMessage = notification.type === 'IMAGE' ? 'Attachment' : notification.content;
  }

  private handleSeenStatus(): void {
    this.chatMessages.forEach(message => message.state = 'SEEN');
  }

  private handleNonSelectedChatNotification(notification: Notification): void {
    const existingChat = this.chats.find(chat => chat.id === notification.chatId);

    if (notification.type === 'SEEN') return;

    if (existingChat) {
      this.updateExistingChat(existingChat, notification);
    } else if (notification.type === 'MESSAGE') {
      this.createNewChat(notification);
    }
  }

  private updateExistingChat(chat: ChatResponse, notification: Notification): void {
    chat.lastMessage = notification.type === 'IMAGE' ? 'Attachment' : notification.content;
    chat.lastMessageTime = new Date().toString();
    chat.unreadCount = (chat.unreadCount || 0) + 1;
  }

  private createNewChat(notification: Notification): void {
    const newChat: ChatResponse = {
      id: notification.chatId,
      senderId: notification.senderId,
      recipientId: notification.recipientId,
      lastMessage: notification.content,
      name: notification.chatName,
      unreadCount: 1,
      lastMessageTime: new Date().toString()
    };
    this.chats.unshift(newChat);
  }

  private getSenderId(): string {
    if (this.selectedChat.senderId === this.keycloakService.userId) {
      return this.selectedChat.senderId as string;
    }
    return this.selectedChat.recipientId as string;
  }

  private getRecipientId(): string {
    if (this.selectedChat.senderId === this.keycloakService.userId) {
      return this.selectedChat.recipientId as string;
    }
    return this.selectedChat.senderId as string;
  }

  private scrollToBottom() {
    if (this.scrollableDiv) {
      const div = this.scrollableDiv.nativeElement;
      div.scrollTop = div.scrollHeight;
    }
  }

  private extractFileFromTarget(target: EventTarget | null): File | null {
    const htmlInputTarget = target as HTMLInputElement;
    if (target === null || htmlInputTarget.files === null) {
      return null;
    }
    return htmlInputTarget.files[0];
  }
}
