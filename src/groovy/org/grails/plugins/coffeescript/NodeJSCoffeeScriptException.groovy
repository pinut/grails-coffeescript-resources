package org.grails.plugins.coffeescript

class NodeJSCoffeeScriptException extends RuntimeException {
	NodeJSCoffeeScriptException() {
	}

	NodeJSCoffeeScriptException(String s) {
		super(s)
	}

	NodeJSCoffeeScriptException(String s, Throwable throwable) {
		super(s, throwable)
	}

	NodeJSCoffeeScriptException(Throwable throwable) {
		super(throwable)
	}
}
