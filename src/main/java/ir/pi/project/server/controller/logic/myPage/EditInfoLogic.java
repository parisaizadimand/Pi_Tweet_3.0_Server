package ir.pi.project.server.controller.logic.myPage;


import ir.pi.project.server.config.texts.myPage.EditInfoTexts;
import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.server.controller.logic.MainLogic;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.response.Response;
import ir.pi.project.shared.response.others.ShowMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EditInfoLogic extends MainLogic {
    static private final Logger logger = LogManager.getLogger(EditInfoLogic.class);
    EditInfoTexts editInfoTexts=new EditInfoTexts();
    ClientHandler clientHandler;

    public EditInfoLogic(ClientHandler clientHandler){
        this.clientHandler=clientHandler;
    }

    public Response save(String firstName,String lastName,String userName,String email,String phoneNumber,String birthDate,String biography,boolean canSee,String imageInString){

        if(firstName.equals("")||lastName.equals("")||userName.equals("")||phoneNumber.equals("")||birthDate.equals(""))
            return new ShowMessage(editInfoTexts.getEmptyField());

        if(!isUserNameAvailable(userName))
            return new ShowMessage(editInfoTexts.getTakenUsername());
        if(!isEmailAvailable(email))
            return new ShowMessage(editInfoTexts.getTakenEmail());
        if(!isPhoneNumberAvailable(phoneNumber))
            return new ShowMessage(editInfoTexts.getTakenPhoneNumber());


        User user=context.Users.get(clientHandler.getCurrentUserId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserName(userName);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setBirthDate(birthDate);
        user.setBiography(biography);
        user.setEPBCanSee(canSee);
        user.setProfileImageInString(imageInString);

        context.Users.update(user);
        logger.info("user "+user.getId()+" changed info");

        return new ShowMessage(editInfoTexts.getSaved());

    }

    public boolean isUserNameAvailable(String username){
        User thisUser= context.Users.get(clientHandler.getCurrentUserId());
        for (User user: context.Users.all()) {
            if(user.getUserName().equals(username) && user.getId()!=thisUser.getId())return false;
        }
        return true;
    }

    public boolean isEmailAvailable(String email){
        User thisUser= context.Users.get(clientHandler.getCurrentUserId());
        for (User user: context.Users.all()) {
            if(user.getEmail().equals(email)&& user.getId()!=thisUser.getId())return false;
        }
        return true;
    }

    public boolean isPhoneNumberAvailable(String phoneNumber){
        User thisUser= context.Users.get(clientHandler.getCurrentUserId());
        for (User user:context.Users.all()) {
            if(user.getUserName().equals(phoneNumber)&& user.getId()!=thisUser.getId())return false;
        }
        return true;
    }


}
