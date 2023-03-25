package com.uqii.teabot.botapi.handlers;

import com.uqii.teabot.botapi.MethodWrapper;
import com.uqii.teabot.botapi.utils.keyboards.CategoryKeyboard;
import com.uqii.teabot.models.User;
import com.uqii.teabot.models.UserState;
import com.uqii.teabot.services.UserService;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Log4j2
@Component
@AllArgsConstructor
public class MessageHandler {

  UserService userService;
  CategoryKeyboard categoryKeyboard;
  DialogHandler dialogHandler;

  public MethodWrapper handleMessage(Message message) {
    String messageText = message.getText();
    Long userId = message.getFrom().getId();

    log.info("Message from " + userId + ": " + messageText);

    if (!userService.isUserExist(userId)) {
      String username = message.getFrom().getUserName();
      User user = new User(userId, username);
      userService.saveUser(user);
    }

    User user = userService.getUser(userId)
        .orElseThrow(() -> new NoSuchElementException("No user with id " + userId));
    UserState userState = user.getState();

    switch (userState) {
      case EVALUATING_TEA -> {
        return dialogHandler.getEvaluatingTeaDialog(message, userId);
      }
      case CREATING_TEA -> {
        return dialogHandler.getCreatingTeaDialog(message, userId);
      }
      case EDITING_TEA -> {
        return dialogHandler.getEditingTeaDialog(message, userId);
      }
      case DELETING_TEA -> {
        return dialogHandler.getDeletingTeaDialog(message, userId);
      }
    }

    switch (messageText) {
      case "/start" -> {
        SendMessage sendMessage = SendMessage.builder().chatId(userId)
            .text("Напишите /tea для получения категорий").build();
        return new MethodWrapper(sendMessage);
      }
      case "/tea" -> {
        InlineKeyboardMarkup categoryKeyboard = this.categoryKeyboard.getAllCategoriesKeyboard();
        SendMessage sendMessage = SendMessage.builder().chatId(userId).text("Категории")
            .replyMarkup(categoryKeyboard).build();
        return new MethodWrapper(sendMessage);
      }
    }

    return new MethodWrapper();
  }
}
