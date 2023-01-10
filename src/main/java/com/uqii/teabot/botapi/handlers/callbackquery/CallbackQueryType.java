package com.uqii.teabot.botapi.handlers.callbackquery;


public enum CallbackQueryType {
	
	CATEGORY_, TEA_;
	
	@Override
	public String toString() {
		return this.name();
	}
}
