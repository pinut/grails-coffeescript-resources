package org.grails.plugins.coffeescript

class NodeJSCoffeeScriptEngine {

	static isAvailable() {
		println "OS Linux: ${isOperatingSystemSupported()}"
		println "Node path: ${getNodePath()}"
		println "Coffee path: ${getCoffeePath()}"
		isOperatingSystemSupported() && getNodePath() && getCoffeePath()
	}

	static isOperatingSystemSupported() {
		switch (System.getProperty("os.name").toLowerCase()) {
			case "linux": true; break
			default: false
		}
	}

	static getCoffeePath() {
		getPathTo "coffee"
	}

	static getNodePath() {
		getPathTo "node"
	}

	static getPathTo(program) {
		def p = "/usr/bin/which $program".execute()
		p.waitFor()
		def path = p.text.trim()
		new File(path).exists() ? path : null
	}

	def compile(input, target) {
		print "compiling coffee file '$input'... "
		def args = createArgs(input)
		def (process, output) = execute(args)
		if (process.exitValue()) {
			throw new NodeJSCoffeeScriptException(output.toString())
		}
		target.write output.toString()
		println "done"
	}

	private createArgs(input) {
		def node = getNodePath()
		def coffee = getCoffeePath()
		[node, coffee, "-cp", input.absolutePath]
	}

	private execute(args) {
		def p = args.execute()
		def sb = new StringBuilder()
		p.consumeProcessOutput(sb, sb)
		p.waitFor()
		[p, sb]
	}
}
