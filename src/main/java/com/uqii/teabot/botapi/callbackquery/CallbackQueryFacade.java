package com.uqii.teabot.botapi.callbackquery;

import com.uqii.teabot.botapi.MethodWrapper;
import com.uqii.teabot.botapi.callbackquery.enums.CallbackQueryType;
import com.uqii.teabot.exceptions.UnknownCallbackQueryException;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Log4j2
@Component
@AllArgsConstructor
public class CallbackQueryFacade {

  private final List<CallbackQueryHandler> handlers;

  public MethodWrapper handleCallbackQuery(CallbackQuery callbackQuery) {
    String data = callbackQuery.getData();
    Long userId = callbackQuery.getFrom().getId();

    log.info("Callback data from " + userId + ": " + data);

    CallbackQueryType cbType = CallbackQueryType.valueOf(data.split(":")[0]);

    Optional<CallbackQueryHandler> handler = handlers.stream()
        .filter(cbQuery -> cbQuery.getHandlerQueryType().equals(cbType))
        .findFirst();

    return handler.map(queryHandler -> queryHandler.handleCallbackQuery(callbackQuery))
        .orElseThrow(UnknownCallbackQueryException::new);
  }
}
