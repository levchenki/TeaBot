package com.uqii.teabot.models;

public enum Category {
  OOLONG("Улуны", false),
  HIGHLY_FERMENTED_OOLONG("Сильноферментированный улун", true),
  MEDIUM_FERMENTED_OOLONG("Среднеферментированный улун", true),
  WEAKLY_FERMENTED_OOLONG("Слабоферментированный улун", true),
  GREEN("Зелёный чай", false),
  WHITE("Белый чай", false),
  SHEN("Шен пуэр", false),
  SHU("Шу пуэр", false),
  RED("Красный чай", false),
  BLACK("Чёрный чай", false),
  HERBAL("Травяной чай", false);

  private final String title;
  private final boolean isSubcategory;

  Category(String title, boolean isSubcategory) {
    this.title = title;
    this.isSubcategory = isSubcategory;
  }

  public String getTitle() {
    return title;
  }

  public boolean getIsSubcategory() {
    return isSubcategory;
  }
}
