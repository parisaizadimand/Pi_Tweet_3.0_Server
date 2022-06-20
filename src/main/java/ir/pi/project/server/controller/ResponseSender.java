package ir.pi.project.server.controller;

import ir.pi.project.shared.event.Event;
import ir.pi.project.shared.response.Response;

public interface ResponseSender {
    Event getEvent();
    void sendResponse(Response response);
    void close();
}
