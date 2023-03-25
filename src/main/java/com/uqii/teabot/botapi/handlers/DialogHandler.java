package com.uqii.teabot.botapi.handlers;

import com.uqii.teabot.botapi.MethodWrapper;
import com.uqii.teabot.botapi.callbackquery.enums.CallbackQueryEditedValue;
import com.uqii.teabot.botapi.utils.keyboards.CategoryKeyboard;
import com.uqii.teabot.botapi.utils.keyboards.TeaKeyboard;
import com.uqii.teabot.models.Category;
import com.uqii.teabot.models.Evaluation;
import com.uqii.teabot.models.Tea;
import com.uqii.teabot.models.User;
import com.uqii.teabot.models.UserState;
import com.uqii.teabot.services.EvaluationService;
import com.uqii.teabot.services.RedisService;
import com.uqii.teabot.services.TeaService;
import com.uqii.teabot.services.UserService;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@AllArgsConstructor
public class DialogHandler {

  private final int MAX_TEX_LEN = 255;
  TeaService teaService;
  UserService userService;
  EvaluationService evaluationService;
  RedisService redisService;
  CategoryKeyboard categoryKeyboard;
  TeaKeyboard teaKeyboard;

  public MethodWrapper getEvaluatingTeaDialog(Message message, Long userId) {
    String evaluatingKey = redisService.getEvaluatingKey(userId);
    Long teaId = Long.valueOf(redisService.getFromHash(evaluatingKey, "tea"));
    int pageOfCurrentTea = Integer.parseInt(redisService.getFromHash(evaluatingKey, "page"));
    int editMessageId = Integer.parseInt(redisService.getFromHash(evaluatingKey, "editMessageId"));

    InlineKeyboardMarkup cancelKeyboard = teaKeyboard.getBackToTeaKeyboard(teaId, "Отмена",
        pageOfCurrentTea);
    InlineKeyboardMarkup getBackToTeaKeyboard = teaKeyboard.getBackToTeaKeyboard(teaId, "Назад",
        pageOfCurrentTea);

    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(userId);
    sendMessage.setReplyMarkup(cancelKeyboard);

    EditMessageText editMessageText = new EditMessageText();
    editMessageText.setMessageId(editMessageId);
    editMessageText.setChatId(userId);

    String messageText = message.getText();
    int nextMessageId = message.getMessageId() + 1;
    redisService.setToHash(evaluatingKey, "editMessageId", nextMessageId);

    if (redisService.isEmpty(evaluatingKey, "rating")) {
      String replaced = messageText.replace(",", ".");
      if (isValidRating(replaced)) {
        redisService.setToHash(evaluatingKey, "rating", replaced);

        editMessageText.setText("Введите число от 1 до 10");
        sendMessage.setText("Введите комментарий");
      } else {
        editMessageText.setText("Введите число от 1 до 10");
        sendMessage.setText("Ошибка! Введите число от 1 до 10");
      }
    } else {
      if (isInvalidText(messageText)) {
        editMessageText.setText("Введите комментарий");

        sendMessage.setText(
            "Комментарий не может превышать " + MAX_TEX_LEN + " символов.\nВведите комментарий");
        redisService.setToHash(evaluatingKey, "editMessageId", nextMessageId);
        return new MethodWrapper(editMessageText, sendMessage);
      }
      redisService.setToHash(evaluatingKey, "comment", messageText);

      editMessageText.setText("Введите комментарий");
      sendMessage.setText("Оценка поставлена!");
      sendMessage.setReplyMarkup(getBackToTeaKeyboard);

      String comment = redisService.getFromHash(evaluatingKey, "comment");
      Double rating = Double.valueOf(redisService.getFromHash(evaluatingKey, "rating"));

      evaluationService.getEvaluation(userId, teaId).ifPresentOrElse((evaluation -> {
        evaluation.setComment(comment);
        evaluation.setRating(rating);
        evaluationService.saveEvaluation(evaluation);
      }), () -> {
        User user = userService.getUser(userId)
            .orElseThrow(() -> new NoSuchElementException("No user with id " + userId));
        Tea tea = teaService.getTea(teaId)
            .orElseThrow(() -> new NoSuchElementException("No tea with id " + teaId));
        evaluationService.saveEvaluation(new Evaluation(rating, comment, tea, user));
      });

      redisService.removeKey(evaluatingKey);
      userService.setUserState(userId, UserState.GET_TEA);
    }
    return new MethodWrapper(editMessageText, sendMessage);
  }

  public MethodWrapper getCreatingTeaDialog(Message message, Long userId) {
    String creatingKey = redisService.getCreatingKey(userId);
    Category category = Category.valueOf(redisService.getFromHash(creatingKey, "category"));
    int editMessageId = Integer.parseInt(redisService.getFromHash(creatingKey, "editMessageId"));

    InlineKeyboardMarkup cancelKeyboard = teaKeyboard.getBackToCategoryKeyboard(category, "Отмена");
    InlineKeyboardMarkup backToCategoryKeyboard = teaKeyboard.getBackToCategoryKeyboard(category,
        "Назад");

    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(userId);
    sendMessage.setReplyMarkup(cancelKeyboard);

    EditMessageText editMessageText = new EditMessageText();
    editMessageText.setChatId(userId);
    editMessageText.setMessageId(editMessageId);
    editMessageText.setReplyMarkup(null);

    String messageText = message.getText();
    int nextMessageId = message.getMessageId() + 1;
    redisService.setToHash(creatingKey, "editMessageId", nextMessageId);

    if (redisService.isEmpty(creatingKey, "name")) {
      if (isInvalidText(messageText)) {

        editMessageText.setText("Введите название чая");

        sendMessage.setText(
            "Название не может превышать " + MAX_TEX_LEN + " символов.\nВведите название чая");

        return new MethodWrapper(editMessageText, sendMessage);
      }

      editMessageText.setText("Введите название чая");
      sendMessage.setText("Введите стоимость чая");

      redisService.setToHash(creatingKey, "name", messageText);
      return new MethodWrapper(editMessageText, sendMessage);
    } else if (redisService.isEmpty(creatingKey, "price")) {
      if (isValidPrice(messageText)) {
        editMessageText.setText("Введите стоимость чая");
        sendMessage.setText("Введите описание");

        redisService.setToHash(creatingKey, "price", messageText);
        return new MethodWrapper(editMessageText, sendMessage);
      } else {
        editMessageText.setText("Введите стоимость чая");
        sendMessage.setText("Введите валидную стоимость чая");

        return new MethodWrapper(editMessageText, sendMessage);
      }
    } else {
      if (isInvalidText(messageText)) {
        editMessageText.setText("Введите стоимость чая");
        sendMessage.setText(
            "Описание не может превышать " + MAX_TEX_LEN + " символов.\nВведите описание");

        return new MethodWrapper(editMessageText, sendMessage);
      }

      redisService.setToHash(creatingKey, "description", messageText);
      editMessageText.setText("Введите описание");

      sendMessage.setText("Чай успешно добавлен!");
      sendMessage.setReplyMarkup(backToCategoryKeyboard);

      String teaName = redisService.getFromHash(creatingKey, "name");
      int teaPrice = Integer.parseInt(redisService.getFromHash(creatingKey, "price"));
      String teaDescription = redisService.getFromHash(creatingKey, "description");

      redisService.removeKey(creatingKey);
      teaService.saveTea(new Tea(teaName, teaDescription, teaPrice, category));
      userService.setUserState(userId, UserState.GET_CATEGORY);

      return new MethodWrapper(editMessageText, sendMessage);
    }
  }

  public MethodWrapper getEditingTeaDialog(Message message, Long userId) {
    String editingKey = redisService.getEditingKey(userId);
    Long teaId = Long.valueOf(redisService.getFromHash(editingKey, "tea"));
    int editMessageId = Integer.parseInt(redisService.getFromHash(editingKey, "editMessageId"));
    int pageOfCurrentTea = Integer.parseInt(redisService.getFromHash(editingKey, "page"));
    CallbackQueryEditedValue editedValue = CallbackQueryEditedValue.valueOf(
        redisService.getFromHash(editingKey, "editedValue"));

    InlineKeyboardMarkup cancelKeyboard = teaKeyboard.getBackToEvaluatingTeaKeyboard(teaId,
        "Отмена", pageOfCurrentTea);
    InlineKeyboardMarkup getBackToEvaluatingTeaKeyboard = teaKeyboard.getBackToEvaluatingTeaKeyboard(
        teaId, "Назад", pageOfCurrentTea);

    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(userId);

    EditMessageText editMessageText = new EditMessageText();
    editMessageText.setChatId(userId);
    editMessageText.setMessageId(editMessageId);
    editMessageText.setReplyMarkup(null);

    Tea tea = teaService.getTea(teaId)
        .orElseThrow(() -> new NoSuchElementException("No tea with id " + teaId));

    String messageText = message.getText();
    int nextMessageId = message.getMessageId() + 1;
    redisService.setToHash(editingKey, "editMessageId", nextMessageId);

    switch (editedValue) {
      case EDIT_NAME -> {
        editMessageText.setText("Изменение чая \"" + tea.getName() + "\". Введите новое название");
        if (isInvalidText(messageText)) {
          sendMessage.setText(
              "Название не может превышать " + MAX_TEX_LEN + " символов.\nВведите новое название");
          sendMessage.setReplyMarkup(cancelKeyboard);
        } else {
          tea.setName(messageText);
          teaService.saveTea(tea);
          sendMessage.setText("Название чая изменено");
          sendMessage.setReplyMarkup(getBackToEvaluatingTeaKeyboard);
        }
        return new MethodWrapper(editMessageText, sendMessage);
      }
      case EDIT_PRICE -> {
        editMessageText.setText("Изменение чая \"" + tea.getName() + "\". Введите новую стоимость");
        if (isValidPrice(messageText)) {
          tea.setPrice(Integer.parseInt(messageText));
          teaService.saveTea(tea);
          sendMessage.setText("Стоимость чая изменена");
          sendMessage.setReplyMarkup(getBackToEvaluatingTeaKeyboard);
        } else {
          sendMessage.setText("Введите валидную стоимость чая");
          sendMessage.setReplyMarkup(cancelKeyboard);
        }
        return new MethodWrapper(editMessageText, sendMessage);
      }
      case EDIT_DESCRIPTION -> {
        editMessageText.setText("Изменение чая \"" + tea.getName() + "\". Введите новое описание");
        if (isInvalidText(messageText)) {
          sendMessage.setText(
              "Описание не может превышать " + MAX_TEX_LEN + " символов.\nВведите новое описание");
          sendMessage.setReplyMarkup(cancelKeyboard);
        } else {
          tea.setDescription(messageText);
          teaService.saveTea(tea);
          sendMessage.setText("Описание чая изменено");
          sendMessage.setReplyMarkup(getBackToEvaluatingTeaKeyboard);
        }
        return new MethodWrapper(editMessageText, sendMessage);
      }
    }
    return new MethodWrapper();
  }

  public MethodWrapper getDeletingTeaDialog(Message message, Long userId) {
    String deletingKey = redisService.getDeletingKey(userId);
    long teaId = Long.parseLong(redisService.getFromHash(deletingKey, "tea"));
    int editMessageId = Integer.parseInt(redisService.getFromHash(deletingKey, "editMessageId"));
    int pageOfCurrentTea = Integer.parseInt(redisService.getFromHash(deletingKey, "page"));

    Tea tea = teaService.getTea(teaId)
        .orElseThrow(() -> new NoSuchElementException("No tea with id " + teaId));

    InlineKeyboardMarkup backToCategoryKeyboard = teaKeyboard.getBackToCategoryKeyboard(
        tea.getCategory(), "Назад", pageOfCurrentTea);
    InlineKeyboardMarkup cancelKeyboard = teaKeyboard.getBackToTeaKeyboard(teaId, "Отмена",
        pageOfCurrentTea);

    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(userId);
    sendMessage.setReplyMarkup(backToCategoryKeyboard);

    EditMessageText editMessageText = new EditMessageText();
    editMessageText.setChatId(userId);
    editMessageText.setMessageId(editMessageId);
    editMessageText.setReplyMarkup(null);

    String messageText = message.getText();
    int nextMessageId = message.getMessageId() + 1;

    if (messageText.equals(tea.getName())) {
      editMessageText.setText(
          "Удаление чая \"" + tea.getName() + "\". Напишите название чая для подтверждения.");

      sendMessage.setText("Чай удалён");

      teaService.deleteTea(teaId);

      userService.setUserState(userId, UserState.GET_CATEGORY);
      redisService.removeKey(deletingKey);

      return new MethodWrapper(editMessageText, sendMessage);
    } else {
      editMessageText.setText(
          "Удаление чая \"" + tea.getName() + "\". Напишите название чая для подтверждения");
      sendMessage.setText("Названия отличаются.\nУдаление чая \"" + tea.getName()
          + "\". Напишите название чая для подтверждения");
      sendMessage.setReplyMarkup(cancelKeyboard);

      redisService.setToHash(deletingKey, "editMessageId", nextMessageId);
      return new MethodWrapper(editMessageText, sendMessage);
    }
  }

  private boolean isValidPrice(String messageText) {
    return messageText.matches("\\d{1,5}") && Integer.parseInt(messageText) > 0
        && Integer.parseInt(messageText) < 99999;
  }

  private boolean isInvalidText(String messageText) {
    return messageText.length() > MAX_TEX_LEN;
  }

  private boolean isValidRating(String messageText) {
    return messageText.matches("\\d+[.,]?\\d{0,2}") && (Float.parseFloat(messageText) >= 1
        && Float.parseFloat(messageText) <= 10);
  }
}
