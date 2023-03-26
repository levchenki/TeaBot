package com.uqii.teabot.botapi.utils.keyboards;

import com.uqii.teabot.botapi.utils.enums.CallbackQueryAction;
import com.uqii.teabot.botapi.utils.enums.CallbackQueryType;
import com.uqii.teabot.models.Category;
import com.uqii.teabot.services.CategoryService;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
@AllArgsConstructor
public class CategoryKeyboard {

  ButtonsBuilder buttonsBuilder;
  CategoryService categoryService;

  public InlineKeyboardMarkup getAllCategoriesKeyboard() {
    int buttonsInRow = 2;
    List<Category> categories = categoryService.getCategories();
    List<List<InlineKeyboardButton>> keyboard = getInitializedKeyboard(categories, buttonsInRow);
    return InlineKeyboardMarkup.builder().keyboard(keyboard).build();
  }

  public InlineKeyboardMarkup getAllSubcategoriesKeyboard() {
    int buttonsInRow = 1;

    List<Category> subcategories = categoryService.getSubcategories();
    List<List<InlineKeyboardButton>> keyboard = getInitializedKeyboard(subcategories, buttonsInRow);

    String backCallbackData =
        CallbackQueryType.CATEGORY.toString() + CallbackQueryAction.BACK_TO_CATEGORY;
    String backButtonText = "Назад";
    InlineKeyboardButton backButton = buttonsBuilder.getButton(backButtonText, backCallbackData);

    keyboard.add(List.of(backButton));
    return InlineKeyboardMarkup.builder().keyboard(keyboard).build();
  }

  private List<List<InlineKeyboardButton>> getInitializedKeyboard(List<Category> keyboardMarkupData,
      int buttonsInRow) {
    List<List<InlineKeyboardButton>> buttonsRows = new ArrayList<>();

    for (int i = 0; i < keyboardMarkupData.size(); i += buttonsInRow) {
      List<InlineKeyboardButton> sublist = new ArrayList<>(buttonsInRow);

      int buttonsCounter = i + buttonsInRow;
      for (int j = i; j < buttonsCounter && j < keyboardMarkupData.size(); j++) {
        String text = keyboardMarkupData.get(j).getTitle();
        String callbackData =
            CallbackQueryType.CATEGORY + keyboardMarkupData.get(j).name() + CallbackQueryAction.GET;
        sublist.add(buttonsBuilder.getButton(text, callbackData));
      }
      buttonsRows.add(sublist);
    }
    return buttonsRows;
  }
}
