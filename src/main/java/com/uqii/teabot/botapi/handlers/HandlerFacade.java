package com.uqii.teabot.botapi.handlers;

import com.uqii.teabot.botapi.MethodWrapper;
import com.uqii.teabot.botapi.callbackquery.enums.CallbackQueryEditedValue;
import com.uqii.teabot.botapi.utils.keyboards.CategoryKeyboard;
import com.uqii.teabot.botapi.utils.keyboards.TeaKeyboard;
import com.uqii.teabot.models.Category;
import com.uqii.teabot.models.Evaluation;
import com.uqii.teabot.models.Tea;
import com.uqii.teabot.models.UserState;
import com.uqii.teabot.services.EvaluationService;
import com.uqii.teabot.services.RedisService;
import com.uqii.teabot.services.TeaService;
import com.uqii.teabot.services.UserService;
import com.vdurmont.emoji.EmojiParser;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@AllArgsConstructor
public class HandlerFacade {

  TeaService teaService;
  UserService userService;
  EvaluationService evaluationService;
  RedisService redisService;
  CategoryKeyboard categoryKeyboard;
  TeaKeyboard teaKeyboard;

  public MethodWrapper getCategories(EditMessageText editMessageText, Long userId) {
    InlineKeyboardMarkup categoriesKeyboard = categoryKeyboard.getAllCategoriesKeyboard();
    userService.setUserState(userId, UserState.GET_CATEGORIES);

    editMessageText.setText("Категории");
    editMessageText.setReplyMarkup(categoriesKeyboard);
    return new MethodWrapper(editMessageText);
  }

  public MethodWrapper getSubcategories(EditMessageText editMessageText, Long userId) {
    InlineKeyboardMarkup subcategoriesKeyboard = categoryKeyboard.getAllSubcategoriesKeyboard();
    userService.setUserState(userId, UserState.GET_CATEGORIES);

    editMessageText.setText("Улуны");
    editMessageText.setReplyMarkup(subcategoriesKeyboard);
    return new MethodWrapper(editMessageText);
  }


  public MethodWrapper getCategory(EditMessageText editMessageText, Long userId, Category category,
      int page) {
    boolean isAdmin = userService.isUserAdmin(userId);

    InlineKeyboardMarkup allTeaKeyboard = teaKeyboard.getOneCategoryKeyboard(userId, category, page,
        isAdmin);
    userService.setUserState(userId, UserState.GET_CATEGORY);

    editMessageText.setText(category.getTitle());
    editMessageText.setReplyMarkup(allTeaKeyboard);
    return new MethodWrapper(editMessageText);
  }

  public MethodWrapper getTea(EditMessageText editMessageText, Long userId, Long teaId,
      int pageOfCurrentTea) {
    redisService.clearUserCache(userId);

    boolean isAdmin = userService.isUserAdmin(userId);
    boolean isEvaluated = evaluationService.isTeaEvaluatedByUser(userId, teaId);
    InlineKeyboardMarkup oneTeaKeyboard = teaKeyboard.getOneTeaKeyboard(teaId, isEvaluated, isAdmin,
        pageOfCurrentTea);
    userService.setUserState(userId, UserState.GET_TEA);

    String text = generateTeaMessageText(userId, teaId);

    editMessageText.setText(text);
    editMessageText.setReplyMarkup(oneTeaKeyboard);
    return new MethodWrapper(editMessageText);
  }

  private String generateTeaMessageText(Long userId, Long teaId) {
    List<String> generatedStrings = new ArrayList<>();

    Tea tea = teaService.getTea(teaId)
        .orElseThrow(() -> new NoSuchElementException("No tea with id " + teaId));
    String name = tea.getName();
    int price = tea.getPrice();
    String description = tea.getDescription();
    Double averageRating = evaluationService.getAverageRating(teaId);

    Optional<Evaluation> evaluationOptional = evaluationService.getEvaluation(userId, teaId);

    generatedStrings.add(tea.getCategory().getTitle() + ": " + name);
    generatedStrings.add("Стоимость: " + price + " ₽");

    if (description != null && !description.isEmpty() && !description.equals("-")) {
      generatedStrings.add("Описание: " + description);
    }

    if (averageRating > 0) {
      generatedStrings.add(
          "Средний рейтинг: " + String.format("%.2f", averageRating).replace(".", ","));
    } else {
      generatedStrings.add("У чая ещё нет оценок :disappointed:");
    }

    if (evaluationOptional.isPresent()) {
      Evaluation evaluation = evaluationOptional.get();
      Double rating = evaluation.getRating();
      String comment = evaluation.getComment();

      generatedStrings.add("Ваша оценка: "
          + String.format("%.2f", rating).replace(".", ","));
      if (!comment.isEmpty()) {
        generatedStrings.add("Комментарий: " + comment);
      }
    } else {
      generatedStrings.add("Вы ещё не оценили этот чай");
    }

    StringBuilder resultingString = new StringBuilder();

    for (String line : generatedStrings) {
      String result = EmojiParser.parseToUnicode(line);
      resultingString.append(result).append('\n').append('\n');
    }
    return resultingString.toString();
  }

  public MethodWrapper evaluateTea(EditMessageText editMessageText, Long userId, Long teaId,
      int pageOfCurrentTea) {
    InlineKeyboardMarkup cancelKeyboard = teaKeyboard.getBackToTeaKeyboard(teaId, "Отмена",
        pageOfCurrentTea);
    userService.setUserState(userId, UserState.EVALUATING_TEA);

    String evaluatingKey = redisService.getEvaluatingKey(userId);
    redisService.setToHash(evaluatingKey, "tea", teaId);
    redisService.setToHash(evaluatingKey, "page", pageOfCurrentTea);
    redisService.setToHash(evaluatingKey, "editMessageId", editMessageText.getMessageId());

    editMessageText.setText("Оцените чай от 1 до 10");
    editMessageText.setReplyMarkup(cancelKeyboard);
    return new MethodWrapper(editMessageText);
  }

  public MethodWrapper createTea(EditMessageText editMessageText, Long userId, Category category) {
    InlineKeyboardMarkup cancelKeyboard = teaKeyboard.getBackToCategoryKeyboard(category, "Отмена");
    userService.setUserState(userId, UserState.CREATING_TEA);

    String creatingKey = redisService.getCreatingKey(userId);
    redisService.setToHash(creatingKey, "category", category);
    redisService.setToHash(creatingKey, "editMessageId", editMessageText.getMessageId());

    editMessageText.setText("Введите название чая");
    editMessageText.setReplyMarkup(cancelKeyboard);
    return new MethodWrapper(editMessageText);
  }

  public MethodWrapper deleteTea(EditMessageText editMessageText, Long userId, Long teaId,
      int pageOfCurrentTea) {
    InlineKeyboardMarkup cancelKeyboard = teaKeyboard.getBackToTeaKeyboard(teaId, "Отмена",
        pageOfCurrentTea);
    userService.setUserState(userId, UserState.DELETING_TEA);

    Tea tea = teaService.getTea(teaId)
        .orElseThrow(() -> new NoSuchElementException("No tea with id " + teaId));

    String deletingKey = redisService.getDeletingKey(userId);
    redisService.setToHash(deletingKey, "tea", teaId);
    redisService.setToHash(deletingKey, "editMessageId", editMessageText.getMessageId());
    redisService.setToHash(deletingKey, "page", pageOfCurrentTea);

    editMessageText.setText(
        "Удаление чая \"" + tea.getName() + "\". Напишите название чая для подтверждения");
    editMessageText.setReplyMarkup(cancelKeyboard);
    return new MethodWrapper(editMessageText);
  }

  public MethodWrapper editTea(EditMessageText editMessageText, Long userId, Long teaId,
      int pageOfCurrentTea) {
    InlineKeyboardMarkup evaluatingTeaKeyboard = teaKeyboard.getEvaluatingTeaKeyboard(teaId,
        "Назад", pageOfCurrentTea);
    userService.setUserState(userId, UserState.EDITING_TEA);

    Tea tea = teaService.getTea(teaId)
        .orElseThrow(() -> new NoSuchElementException("No tea with id " + teaId));

    editMessageText.setText("Изменение чая \"" + tea.getName() + "\"");
    editMessageText.setReplyMarkup(evaluatingTeaKeyboard);
    return new MethodWrapper(editMessageText);
  }

  public MethodWrapper editTeaValues(EditMessageText editMessageText, Long userId, Long teaId,
      int pageOfCurrentTea, CallbackQueryEditedValue editedValue) {
    InlineKeyboardMarkup cancelKeyboard = teaKeyboard.getBackToEvaluatingTeaKeyboard(teaId,
        "Отмена", pageOfCurrentTea);
    Tea tea = teaService.getTea(teaId)
        .orElseThrow(() -> new NoSuchElementException("No tea with id " + teaId));

    String editingKey = redisService.getEditingKey(userId);
    redisService.setToHash(editingKey, "tea", teaId);
    redisService.setToHash(editingKey, "editMessageId", editMessageText.getMessageId());
    redisService.setToHash(editingKey, "page", pageOfCurrentTea);
    redisService.setToHash(editingKey, "editedValue", editedValue.name());

    switch (editedValue) {
      case EDIT_NAME -> editMessageText.setText(
          "Изменение чая \"" + tea.getName() + "\". Введите новое название");
      case EDIT_PRICE -> editMessageText.setText(
          "Изменение чая \"" + tea.getName() + "\". Введите новую стоимость");
      case EDIT_DESCRIPTION -> editMessageText.setText(
          "Изменение чая \"" + tea.getName() + "\". Введите новое описание");
    }

    editMessageText.setReplyMarkup(cancelKeyboard);
    return new MethodWrapper(editMessageText);
  }
}
