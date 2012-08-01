import org.grails.plugin.resource.mapper.MapperPhase
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.support.aware.GrailsApplicationAware
import org.mozilla.javascript.EvaluatorException
import org.grails.plugins.coffeescript.NodeJSCoffeeScriptEngine
import org.grails.plugins.coffeescript.NodeJSCoffeeScriptException

class CoffeeScriptResourceMapper implements GrailsApplicationAware {
	def phase = MapperPhase.GENERATION
	static defaultIncludes = ['**/*.coffee']
	static String COFFEE_FILE_EXTENSION = '.coffee'

	GrailsApplication grailsApplication

	def map(resource, config) {
		if (isCoffeeResource(resource)) {
			try {
				mapCoffeeScript resource
			}
			catch (final Exception e) {
				log.error """
	Problems compiling CoffeeScript ${resource.originalUrl}
	$e
	            """
			}
		}
	}

	private isCoffeeResource(resource) {
		resource.sourceUrl && resource.processedFile.name.toLowerCase().endsWith(COFFEE_FILE_EXTENSION)
	}

	private mapCoffeeScript(resource) {
		File original = resource.processedFile
		File input = getOriginalFileSystemFile(resource.sourceUrl)
		File target = generateCompiledFileFromOriginal(original)

		if (log.debugEnabled)
			log.debug "Compiling coffeescript file ${original} into ${target}"

		compile(input, target)

		resource.processedFile = target
		resource.updateActualUrlFromProcessedFile()
		resource.sourceUrlExtension = 'js'
		resource.actualUrl = generateCompiledFilename(resource.originalUrl)
		resource.contentType = 'text/javascript'
	}

	private File generateCompiledFileFromOriginal(File original) {
		new File(generateCompiledFilename(original.absolutePath))
	}

	private String generateCompiledFilename(String filename) {
		filename.replaceAll(/(?i)\.coffee/, '.js')
	}

	private File getOriginalFileSystemFile(String sourcePath) {
		grailsApplication.parentContext.getResource(sourcePath).file
	}

	private compile(input, target) {
		if (NodeJSCoffeeScriptEngine.isAvailable()) {
			new NodeJSCoffeeScriptEngine().compile input, target
		}
		else {
			String output = new org.grails.plugins.coffeescript.CoffeeScriptEngine().compile(input.text)
			target.write(output)
		}
	}
}
