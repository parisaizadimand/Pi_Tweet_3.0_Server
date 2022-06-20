package ir.pi.project.server.model;

import java.util.ArrayList;
import java.util.List;

public class CurrentChat {
    boolean isDirect;
    String name;
    List<Integer> members;
    List<Integer> messages;
    Integer theOther;
    Integer groupChatId;

    public CurrentChat(){
        this.members=new ArrayList<>();
        this.members=new ArrayList<>();
    }

    public boolean isDirect() {
        return isDirect;
    }

    public void setDirect(boolean direct) {
        isDirect = direct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getMembers() {
        return members;
    }

    public void setMembers(List<Integer> members) {
        this.members = members;
    }

    public List<Integer> getMessages() {
        return messages;
    }

    public void setMessages(List<Integer> messages) {
        this.messages = messages;
    }

    public Integer getTheOther() {
        return theOther;
    }

    public void setTheOther(Integer theOther) {
        this.theOther = theOther;
    }

    public Integer getGroupChatId() {
        return groupChatId;
    }

    public void setGroupChatId(Integer groupChatId) {
        this.groupChatId = groupChatId;
    }
}
