package com.uqii.teabot.botapi.utils.keyboards;

import com.uqii.teabot.botapi.callbackquery.enums.CallbackQueryAction;
import com.uqii.teabot.botapi.callbackquery.enums.CallbackQueryEditedValue;
import com.uqii.teabot.botapi.callbackquery.enums.CallbackQueryType;
import com.uqii.teabot.models.Category;
import com.uqii.teabot.models.Evaluation;
import com.uqii.teabot.models.Tea;
import com.uqii.teabot.services.CategoryService;
import com.uqii.teabot.services.TeaService;
import com.vdurmont.emoji.EmojiParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
@AllArgsConstructor
public class TeaKeyboard {

  private final String prefix = CallbackQueryType.TEA.toString();
  ButtonsBuilder buttonsBuilder;
  TeaService teaService;
  CategoryService categoryService;

  public InlineKeyboardMarkup getOneCategoryKeyboard(Long userId, Category category, int page,
      boolean isAdmin) {
    int offset = 5;
    int buttonsInRow = 1;

    List<Tea> evaluatedTeaList = teaService.getEvaluatedTeaList(category, userId, page, offset);

    long count = teaService.getCountByCategory(category);
    long pageCount = count / offset;
    if (count % offset != 0) {
      pageCount++;
    }

    List<List<InlineKeyboardButton>> keyboard = getInitializedKeyboard(evaluatedTeaList,
        buttonsInRow, page);

    keyboard.add(buttonsBuilder.getPaginationButtons(page, pageCount, prefix, category));

    if (isAdmin) {
      InlineKeyboardButton buttonAdd = buttonsBuilder.getButton("Добавить",
          prefix + category.name() + CallbackQueryAction.CREATE);
      keyboard.add(List.of(buttonAdd));
    }

    String backCallbackData = categoryService.isSubcategory(category)
        ? CallbackQueryType.CATEGORY.toString() + Category.OOLONG + CallbackQueryAction.GET
        :  prefix + CallbackQueryAction.BACK_TO_CATEGORY;
    keyboard.add(buttonsBuilder.getBackButton(backCallbackData));
    return InlineKeyboardMarkup.builder().keyboard(keyboard).build();
  }

  public InlineKeyboardMarkup getOneTeaKeyboard(Long teaId, boolean isEvaluated, boolean isAdmin,
      int pageOfCurrentTea) {
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    Tea tea = teaService.getTea(teaId)
        .orElseThrow(() -> new RuntimeException("No tea with id " + teaId));
    Category category = tea.getCategory();

    InlineKeyboardButton buttonRate = buttonsBuilder.getButton(isEvaluated ? "Изменить" : "Оценить",
        prefix + teaId + CallbackQueryAction.RATE + ":" + pageOfCurrentTea);
    keyboard.add(List.of(buttonRate));

    if (isAdmin) {
      InlineKeyboardButton buttonEdit = buttonsBuilder
          .getButton("Изменить",
              prefix + teaId + CallbackQueryAction.EDIT + ":" + pageOfCurrentTea);

      InlineKeyboardButton buttonDelete = buttonsBuilder
          .getButton("Удалить",
              prefix + teaId + CallbackQueryAction.DELETE + ":" + pageOfCurrentTea);

      keyboard.add(List.of(buttonEdit, buttonDelete));
    }

    String backCallbackData = prefix + category.name() + CallbackQueryAction.PAGE + pageOfCurrentTea;
    keyboard.add(buttonsBuilder.getBackButton(backCallbackData));
    return InlineKeyboardMarkup.builder().keyboard(keyboard).build();
  }

  private List<List<InlineKeyboardButton>> getInitializedKeyboard(List<Tea> keyboardMarkupData,
      int buttonsInRow, int page) {
    List<List<InlineKeyboardButton>> buttonRows = new ArrayList<>();

    for (int i = 0; i < keyboardMarkupData.size(); i += buttonsInRow) {
      List<InlineKeyboardButton> sublist = new ArrayList<>(buttonsInRow);

      int buttonsCounter = i + buttonsInRow;
      for (int j = i; j < buttonsCounter && j < keyboardMarkupData.size(); j++) {
        Tea tea = keyboardMarkupData.get(j);
        String title;

        if (tea.getEvaluations().isEmpty()) {
          title = tea.getName();
        } else {
          Evaluation evaluation = tea.getEvaluations().stream()
              .filter(e -> Objects.equals(e.getTea().getId(), tea.getId())).findFirst()
              .orElseThrow();
          title = EmojiParser.parseToUnicode(
              tea.getName() + " :star: " + String.format("%.2f", evaluation.getRating()));
        }

        String callbackData = prefix + tea.getId() + CallbackQueryAction.GET + ":" + page;
        sublist.add(buttonsBuilder.getButton(title, callbackData));
      }
      buttonRows.add(sublist);
    }
    return buttonRows;
  }

  public InlineKeyboardMarkup getBackToTeaKeyboard(Long teaId, String backButtonText,
      int pageOfCurrentTea) {
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    String backCallbackData = prefix + teaId + CallbackQueryAction.GET + ":" + pageOfCurrentTea;
    keyboard.add(buttonsBuilder.getBackButton(backCallbackData, backButtonText));
    return InlineKeyboardMarkup.builder().keyboard(keyboard).build();
  }

  public InlineKeyboardMarkup getBackToCategoryKeyboard(Category category, String backButtonText) {
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    String backCallbackData =
        CallbackQueryType.CATEGORY + category.name() + CallbackQueryAction.GET;
    keyboard.add(buttonsBuilder.getBackButton(backCallbackData, backButtonText));
    return InlineKeyboardMarkup.builder().keyboard(keyboard).build();
  }

  public InlineKeyboardMarkup getBackToCategoryKeyboard(Category category, String backButtonText,
      int pageOfCurrentTea) {
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    String backCallbackData =
        CallbackQueryType.CATEGORY + category.name() + CallbackQueryAction.GET + ":"
            + pageOfCurrentTea;
    keyboard.add(buttonsBuilder.getBackButton(backCallbackData, backButtonText));
    return InlineKeyboardMarkup.builder().keyboard(keyboard).build();
  }

  public InlineKeyboardMarkup getEvaluatingTeaKeyboard(Long teaId, String backButtonText,
      int pageOfCurrentTea) {
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    String editNameCallbackData = prefix + teaId + CallbackQueryAction.EDIT + ":" + pageOfCurrentTea
        + CallbackQueryEditedValue.EDIT_NAME;
    String editPriceCallbackData =
        prefix + teaId + CallbackQueryAction.EDIT + ":" + pageOfCurrentTea
            + CallbackQueryEditedValue.EDIT_PRICE;
    String editDescriptionCallbackData =
        prefix + teaId + CallbackQueryAction.EDIT + ":" + pageOfCurrentTea
            + CallbackQueryEditedValue.EDIT_DESCRIPTION;

    InlineKeyboardButton editNameButton = buttonsBuilder.getButton("Изменить название",
        editNameCallbackData);
    InlineKeyboardButton editPriceButton = buttonsBuilder.getButton("Изменить стоимость",
        editPriceCallbackData);
    InlineKeyboardButton editDescriptionButton = buttonsBuilder.getButton("Изменить описание",
        editDescriptionCallbackData);

    keyboard.add(List.of(editNameButton));
    keyboard.add(List.of(editPriceButton));
    keyboard.add(List.of(editDescriptionButton));

    String backCallbackData = prefix + teaId + CallbackQueryAction.GET + ":" + pageOfCurrentTea;
    keyboard.add(buttonsBuilder.getBackButton(backCallbackData, backButtonText));
    return InlineKeyboardMarkup.builder().keyboard(keyboard).build();
  }

  public InlineKeyboardMarkup getBackToEvaluatingTeaKeyboard(Long teaId, String backButtonText,
      int pageOfCurrentTea) {
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    InlineKeyboardButton buttonEdit = buttonsBuilder
        .getButton(backButtonText,
            prefix + teaId + CallbackQueryAction.EDIT + ":" + pageOfCurrentTea);
    keyboard.add(List.of(buttonEdit));
    return InlineKeyboardMarkup.builder().keyboard(keyboard).build();
  }
}
