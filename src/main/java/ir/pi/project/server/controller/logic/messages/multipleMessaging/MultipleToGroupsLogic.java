package ir.pi.project.server.controller.logic.messages.multipleMessaging;

import ir.pi.project.server.config.texts.messages.MessagesText;
import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.server.controller.logic.MainLogic;
import ir.pi.project.server.controller.logic.NewMessageLogic;
import ir.pi.project.server.controller.logic.UserLogic;
import ir.pi.project.shared.model.Group;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.response.Response;
import ir.pi.project.shared.response.messages.multipleMessaging.LoadMultipleToGroupsPage;
import ir.pi.project.shared.response.others.ShowMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

public class MultipleToGroupsLogic extends MainLogic {
    static private final Logger logger = LogManager.getLogger(MultipleToGroupsLogic.class);
    private final MessagesText messagesText = new MessagesText();
    ClientHandler clientHandler;
    UserLogic userLogic;
    NewMessageLogic newMessageLogic;

    public MultipleToGroupsLogic(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.userLogic = new UserLogic(clientHandler);
        this.newMessageLogic = new NewMessageLogic();
    }

    public Response addToGroupNames(String groupName) {
        List<Integer> groups=clientHandler.getCurrentMultipleGroups();

        User me = context.Users.get(clientHandler.getCurrentUserId());

        boolean exists=false;
        for (Integer groupId : me.getGroups()) {
            Group group = context.Groups.get(groupId);
            if (group.getName().equals(groupName)) {
                exists=true;
                if (group.getMembers().isEmpty())
                    return new ShowMessage(messagesText.getEmptyGroup());
                int q = 0;
                for (Integer memberId : group.getMembers()) {
                    if (context.Users.get(memberId).isActive()) q++;
                }
                if (q == 0) return new ShowMessage(messagesText.getEmptyGroup());

                if(groups.contains(groupId))return new ShowMessage(messagesText.getTakenGroupName());

                groups.add(groupId);
            }
        }

        if(!exists)return new ShowMessage(messagesText.getGroupNotFound());


        List<String> groupNames=new ArrayList<>();
        for (Integer groupId: groups) {
            groupNames.add(context.Groups.get(groupId).getName());
        }
        return new LoadMultipleToGroupsPage(groupNames);
    }


    public Response sendMessage(String text,String imageInString) {
        User user = context.Users.get(clientHandler.getCurrentUserId());

        if (text == null || text.equals(""))
            return new ShowMessage(messagesText.getEmptyText());

        List<Integer> receivers=new ArrayList<>();
        for (Integer groupId: clientHandler.getCurrentMultipleGroups() ) {
            Group group=context.Groups.get(groupId);
            receivers.addAll(group.getMembers());
            logger.info("user "+user.getId()+" sent multiple message to members of group with id "+groupId);
        }

        for (Integer receiverId: receivers) {
            newMessageLogic.newMessage(clientHandler.getCurrentUserId(), receiverId, text,imageInString,false,null);
        }

        return new ShowMessage(messagesText.getSent());
    }
}
