package com.uqii.teabot.botapi.handlers;

import com.uqii.teabot.botapi.utils.keyboards.CategoryKeyboards;
import com.uqii.teabot.models.BotState;
import com.uqii.teabot.models.User;
import com.uqii.teabot.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Log4j
@Component
@AllArgsConstructor
public class MessageHandler {
	
	UserService userService;
	HandlerService handlerService;
	CategoryKeyboards categoryKeyboards;
	
	public BotApiMethod<?> handle(Message message, Long userId) {
		log.debug("message: " + message.getText() + "; from: " + userId);
		
		if (userService.getOneUser(userId).isEmpty()) {
			String username = message.getFrom().getUserName();
			User user = new User(userId, username);
			userService.createUser(user);
		}
		
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(userId);
		
		User user = userService.getOneUser(userId).get();
		switch (user.getState()) {
			case EVALUATING_TEA -> {
				return handlerService.evaluatingTeaDialog(message, userId);
			}
			case CREATING_TEA -> {
				return handlerService.creatingTeaDialog(message, userId);
			}
			case EDITING_TEA -> {
				return handlerService.editingTeaDialog(message, userId);
			}
			case DELETING_TEA -> {
				return handlerService.deletingTeaDialog(message, userId);
			}
			default -> {
				switch (message.getText()) {
					case "/category" -> {
						userService.setStateUser(userId, BotState.GET_CATEGORIES);
						sendMessage.setText("Категории");
						sendMessage.setReplyMarkup(categoryKeyboards.getAllCategoriesKeyboard());
					}
					case "/help" -> sendMessage.setText("Помощь");
					case "/search" -> // todo поиск
							sendMessage.setText("Поиск...");
					default -> sendMessage.setText("/category");
				}
			}
		}
		return sendMessage;
	}
}
