package ir.pi.project.server.controller.logic.messages.groups;

import ir.pi.project.server.config.texts.messages.MessagesText;
import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.server.controller.logic.MainLogic;
import ir.pi.project.server.controller.logic.UserLogic;
import ir.pi.project.server.db.ID;
import ir.pi.project.shared.enums.Pages.*;
import ir.pi.project.shared.model.Group;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.response.Response;
import ir.pi.project.shared.response.messages.groups.LoadEditGroupPage;
import ir.pi.project.shared.response.messages.groups.LoadGroupsPage;
import ir.pi.project.shared.response.others.ShowMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

public class GroupsLogic extends MainLogic {
    private final MessagesText messagesText = new MessagesText();
    static private final Logger logger = LogManager.getLogger(GroupsLogic.class);

    ClientHandler clientHandler;
    UserLogic userLogic;

    public GroupsLogic(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.userLogic = new UserLogic(clientHandler);
    }

    public Response check(GroupsPage groupsPage, String string) {
        return switch (groupsPage) {
            case MAKE -> makeGroup(string);
            case EDIT -> editGroup(string);
            case REMOVE -> removeMember(string);
            default -> addMember(string);
        };
    }

    private Response makeGroup(String groupName) {
        if (!isInGroups(groupName)) return make(groupName);
        return new ShowMessage(messagesText.getTakenGroupName());
    }

    private Response editGroup(String groupName) {
        if (isInGroups(groupName)) return edit(groupName);
        return new ShowMessage(messagesText.getGroupNotFound());
    }

    private Response addMember(String username) {
        User user=context.Users.get(clientHandler.getCurrentUserId());
        if (username.equals(user.getUserName()))
            return new ShowMessage(messagesText.getCantAddUrself());
        else if (!isInCurrentGroup(username)) return add(username);

        return new ShowMessage(messagesText.getChosenUser());
    }

    private Response removeMember(String username){
        if (isInCurrentGroup(username)) return remove(username);
        return new ShowMessage(messagesText.getUserNotFound());
    }



    private Response add(String username){
        Group group = context.Groups.get(clientHandler.getCurrentGroupId());
        User me = context.Users.get(clientHandler.getCurrentUserId());
        for (User user : context.Users.all()) {
            if(user.getUserName().equals(username) && user.isActive()){
                if (userLogic.isFollowing(me.getId(), user.getId()) || userLogic.isFollowing(user.getId(), me.getId())) {
                    group.getMembers().add(user.getId());
                    context.Groups.update(group);
                    context.Users.update(user);
                    logger.info("user "+me.getId() +" added " + user.getId() + "to a group with id: " + group.getId());
                    return new LoadEditGroupPage(currentGroupMembers());
                } else return new ShowMessage(messagesText.getFf());
            }
        }

        return new ShowMessage(messagesText.getUserNotFound());
    }

    private Response remove(String username){
        User me = context.Users.get(clientHandler.getCurrentUserId());
        Group group = context.Groups.get(clientHandler.getCurrentGroupId());
        for (int i = 0; i < group.getMembers().size(); i++) {
            User member = context.Users.get(group.getMembers().get(i));
            if (member.getUserName().equals(username)) {
                group.getMembers().remove(i);
                context.Groups.update(group);
                logger.info("user "+ me.getId() +" removed " + member.getId() + "from group with id: " + group.getId());

                break;
            }
        }
        return new LoadEditGroupPage(currentGroupMembers());
    }

    private Response make(String groupName) {
        if(groupName.isEmpty())
            return new ShowMessage(messagesText.getEmptyName());
        User user = context.Users.get(clientHandler.getCurrentUserId());
        Group group = new Group(ID.newID(), groupName, user.getId());
        user.getGroups().add(group.getId());
        context.Groups.update(group);
        context.Users.update(user);
        return new LoadGroupsPage(groupNames());
    }

    private Response edit(String groupName) {
        User user = context.Users.get(clientHandler.getCurrentUserId());

        for (Integer groupId: user.getGroups())
            if (context.Groups.get(groupId).getName().equals(groupName))
                clientHandler.setCurrentGroupId(groupId);

        return new LoadEditGroupPage(currentGroupMembers());
    }




    private List<String> groupNames() {
        User user = context.Users.get(clientHandler.getCurrentUserId());
        List<String> list = new ArrayList<>();
        for (Integer groupId : user.getGroups())
            list.add(context.Groups.get(groupId).getName());

        return list;
    }

    private List<String> currentGroupMembers() {
        List<String> usernames=new ArrayList<>();
        Group group=context.Groups.get(clientHandler.getCurrentGroupId());
        for (Integer userId: group.getMembers())
            usernames.add(context.Users.get(userId).getUserName());

        return usernames;
    }

    private boolean isInGroups(String groupName) {
        User user = context.Users.get(clientHandler.getCurrentUserId());
        for (Integer groupId : user.getGroups())
            if (context.Groups.get(groupId).getName().equals(groupName)) return true;
        return false;
    }

    private boolean isInCurrentGroup(String username) {
        Group group = context.Groups.get(clientHandler.getCurrentGroupId());
        for (Integer memberId : group.getMembers()) {
            if (context.Users.get(memberId).getUserName().equals(username)) return true;
        }
        return false;
    }
}
