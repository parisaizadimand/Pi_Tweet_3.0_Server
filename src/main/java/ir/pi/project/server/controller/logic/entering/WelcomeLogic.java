package ir.pi.project.server.controller.logic.entering;

import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.shared.response.Response;

public class WelcomeLogic {
    LogInLogic logInLogic;
    SignUpLogic signUpLogic;

    public WelcomeLogic(ClientHandler clientHandler){
        logInLogic=new LogInLogic(clientHandler);
        signUpLogic=new SignUpLogic();
    }

    public Response signUp(String firstName,String lastName,String userName,String password,String email,String phoneNumber,String birthDate, boolean canSee){
        return signUpLogic.signUp(firstName,lastName,userName,password,email,phoneNumber,birthDate,canSee);
    }

    public Response logIn(String username, String password){
        return logInLogic.login(username,password);
    }
}
