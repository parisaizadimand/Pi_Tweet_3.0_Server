package ir.pi.project.server.controller.logic.entering;

import ir.pi.project.server.config.texts.entering.SignUpTexts;
import ir.pi.project.server.controller.logic.MainLogic;
import ir.pi.project.server.db.ID;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.response.Response;
import ir.pi.project.shared.response.others.ShowMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDateTime;

public class SignUpLogic extends MainLogic {
    private final SignUpTexts signUpTexts = new SignUpTexts();
    static private final Logger logger = LogManager.getLogger(SignUpLogic.class);

    public Response signUp(String firstName, String lastName, String userName, String password, String email, String phoneNumber, String birthDate, boolean canSee) {
        if (!isUserNameAvailable(userName))
            return new ShowMessage(signUpTexts.getTakenUsername());
        if (!isEmailAvailable(email))
            return new ShowMessage(signUpTexts.getTakenEmail());
        if (!isPhoneNumberAvailable(phoneNumber))
            return new ShowMessage(signUpTexts.getTakenPhoneNumber());


        User user = new User(ID.newID(), firstName, lastName, userName, password, email);
        user.setBirthDate(birthDate);
        user.setPhoneNumber(phoneNumber);
        String date = LocalDateTime.now().getYear() + " " + LocalDateTime.now().getMonth() + " " + LocalDateTime.now().getDayOfMonth() +
                " - " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute();
        user.setLastSeen("LastSeen: " + date);
        user.setOnline(false);
        user.setEPBCanSee(canSee);

        context.Users.update(user);
        logger.info("user " + user.getId() + " signed up");
        return new ShowMessage(signUpTexts.getSuccessful());
    }


    private boolean isUserNameAvailable(String username) {
        for (User user : context.Users.all())
            if (user.getUserName().equals(username)) return false;

        return true;
    }

    private boolean isEmailAvailable(String email) {
        for (User user : context.Users.all())
            if (user.getEmail().equals(email)) return false;

        return true;
    }

    private boolean isPhoneNumberAvailable(String phoneNumber) {
        for (User user : context.Users.all())
            if (user.getUserName().equals(phoneNumber)) return false;

        return true;
    }


}
