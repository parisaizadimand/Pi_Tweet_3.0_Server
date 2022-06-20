package ir.pi.project.server.controller.logic.messages;

import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.server.controller.logic.MainLogic;
import ir.pi.project.shared.enums.others.MessagesUpReqs;
import ir.pi.project.shared.response.Response;
public class MessagesUpReqLogic extends MainLogic {
    ClientHandler clientHandler;
    public MessagesUpReqLogic(ClientHandler clientHandler){
        this.clientHandler=clientHandler;
    }

    public Response check(MessagesUpReqs messagesUpReqs){
        return switch (messagesUpReqs) {
            case DIRECTS -> new MessagesPageLogic(clientHandler).loadDirectChatsListPage();
            case GROUP_CHATS -> new MessagesPageLogic(clientHandler).loadGroupChatsListPage();
            case DIRECT_CHAT_PAGE -> new ShowChatLogic(clientHandler).loadChatPage(true);
            default -> new ShowChatLogic(clientHandler).loadChatPage(false);
        };
    }
}
