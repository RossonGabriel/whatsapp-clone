import {Component, input, InputSignal, output} from '@angular/core';
import {ChatResponse} from '../../services/models/chat-response';
import {DatePipe} from '@angular/common';
import {UserResponse} from '../../services/models/user-response';
import {UserService} from '../../services/services/user.service';
import {ChatService} from '../../services/services/chat.service';
import {KeycloakService} from '../../utils/keycloak/keycloak.service';

@Component({
  selector: 'app-chat-list',
  imports: [
    DatePipe
  ],
  templateUrl: './chat-list.component.html',
  styleUrl: './chat-list.component.scss'
})
export class ChatListComponent {

  chats: InputSignal<ChatResponse[]> = input<ChatResponse[]>([]);
  searchNewContact = false;
  contacts: Array<UserResponse> = [];
  chatSelected = output<ChatResponse>()

  constructor(
    private readonly userService: UserService,
    private readonly chatService: ChatService,
    private readonly keycloakService: KeycloakService
  ) {

  }

  searchContact() {
    this.userService.getAllUsers()
      .subscribe( {
        next: (users) => {
          this.contacts = users;
          this.searchNewContact = true;
        }
      } )
  }

  chatClicked(chat: ChatResponse) {
    this.chatSelected.emit(chat);
  }

  wrapMessage(lastMessage: string | undefined):string {
    if (lastMessage && lastMessage.length <= 20) {
      return lastMessage;
    } else if (lastMessage && lastMessage.length > 20) {
      return lastMessage.substring(0, 17) + '...';
    }
    return 'No messages yet!';
  }

  selectContact(contact: UserResponse) {
    const existingChat = this.chats()
      .filter(chat => chat.senderId === contact.id || chat.recipientId === contact.id);
    console.log(existingChat);
    if (existingChat.length === 0) {
      this.chatService.createChat({
        'sender-id': this.keycloakService.userId,
        'recipient-id': contact.id as string
      }).subscribe({
        next: (res) => {
          const chat: ChatResponse = {
            id: res.response,
            name: contact.firstName + ' ' + contact.lastName,
            recipientOnline: contact.online,
            lastMessageTime: contact.lastSeen,
            senderId: this.keycloakService.userId,
            recipientId: contact.id
          };
          this.chats().unshift(chat);
          this.searchNewContact = false;
          this.chatSelected.emit(chat);
        }
      })
    }


  }
}
