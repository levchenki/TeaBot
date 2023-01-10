package com.uqii.teabot.botapi.handlers.callbackquery;

public enum CallbackQueryNavigation {
	
	_BACK_TO_CATEGORY,
	_BACK_TO_TEAS_LIST,
	_BACK_TO_TEA,
	_RATE,
	_EDIT,
	_DELETE,
	_CREATE;
	
	@Override
	public String toString() {
		return this.name();
	}
}
