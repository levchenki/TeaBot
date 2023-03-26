package com.uqii.teabot.botapi.utils.keyboards;

import com.uqii.teabot.botapi.utils.enums.CallbackQueryAction;
import com.uqii.teabot.models.Category;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class ButtonsBuilder {

  public List<InlineKeyboardButton> getPaginationButtons(int page, long pageCount, String prefix,
      Category category) {
    List<InlineKeyboardButton> navigateRows = new ArrayList<>();

    if (pageCount <= 1) {
      return navigateRows;
    }

    String paginationCallbackData = prefix + category.name() + CallbackQueryAction.PAGE;

    if (page == 0) {
      navigateRows.add(getButton("->", paginationCallbackData + (page + 1)));
    } else if (page == pageCount - 1) {
      navigateRows.add(getButton("<-", paginationCallbackData + (page - 1)));
    } else {
      navigateRows.add(getButton("<-", paginationCallbackData + (page - 1)));
      navigateRows.add(getButton("->", paginationCallbackData + (page + 1)));
    }
    return navigateRows;
  }


  public InlineKeyboardButton getButton(String text, String callbackData) {
    return InlineKeyboardButton.builder().text(text).callbackData(callbackData).build();
  }
}
