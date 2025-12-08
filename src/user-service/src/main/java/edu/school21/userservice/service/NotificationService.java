package edu.school21.userservice.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

	public void sendToEmail(String mail, String message) {
		System.err.println("Sending email to: " + mail + " with message: " + message);
	}

}
