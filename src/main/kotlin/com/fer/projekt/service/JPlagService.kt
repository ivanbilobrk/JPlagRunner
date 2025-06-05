package com.fer.projekt.service

import de.jplag.*
import de.jplag.c.CLanguage
import de.jplag.cpp.CPPLanguage
import de.jplag.java.JavaLanguage
import de.jplag.javascript.JavaScriptLanguage
import de.jplag.python3.PythonLanguage
import de.jplag.typescript.TypeScriptLanguage
import org.springframework.stereotype.Service
import java.io.File
import java.util.*
import de.jplag.options.JPlagOptions
import de.jplag.reporting.reportobject.mapper.MetricMapper
import de.jplag.reporting.reportobject.model.TopComparison
import de.jplag.text.NaturalLanguage
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import java.nio.file.Files
import java.util.concurrent.ExecutorService
import java.util.function.Function

@Service
class JPlagService(
    @Qualifier("jPlagExecutorService")
    private val executorService: ExecutorService,
) {

    private val dispatcher = executorService.asCoroutineDispatcher()

    suspend fun runJplag(
        firstSubmission: String,
        secondSubmission: String,
        language: String
    ): List<TopComparison> {
        val jPlagLanguage = languageToLanguageObject(language.lowercase())
        if (language !in ALLOWED_LANGUAGES) {
            throw IllegalArgumentException("Language not supported: $language. Allowed languages are: $ALLOWED_LANGUAGES")
        }

        val fileSuffix = languageToFileSuffix(language.lowercase())
        val tempRootFileName = UUID.randomUUID().toString()
        val submissionRootFile = File("$TEMP_RESOURCES_PATH/$tempRootFileName")
        createTempFile(firstSubmission, fileSuffix, tempRootFileName)
        createTempFile(secondSubmission, fileSuffix, tempRootFileName)

        val jPlagOptions = JPlagOptions(
            jPlagLanguage, //language
            setOf(submissionRootFile), //submissions
            emptySet() //old submissions
        )

        val jPlagResult = withContext(dispatcher) {
            val jPlagResult = JPlag.run(jPlagOptions)
            deleteTempFile(submissionRootFile)
            jPlagResult
        }
        return getTopComparisons(jPlagResult)
    }

    companion object {
        private fun getTopComparisons(result: JPlagResult): List<TopComparison> {
            return MetricMapper(getSubmissionToIdFunction()).getTopComparisons(result)
        }

        private fun getSubmissionToIdFunction() =
            Function { submission: Submission ->
                if (submission.name.contains("/")) {
                    submission.name.substringAfterLast("/")
                } else {
                    submission.name
                }
            }

        fun deleteTempFile(file: File) {
            if (file.exists()) {
                file.deleteRecursively()
            }
        }

        fun createTempFile(submission: String, suffix: String, tempRootFileName: String): File {
            val fileName = UUID.randomUUID().toString() + suffix
            val tempFile = File("$TEMP_RESOURCES_PATH/$tempRootFileName", fileName)

            tempFile.parentFile.mkdirs()

            tempFile.writeText(submission)

            return tempFile
        }

        fun languageToLanguageObject(language: String): Language {
            return when (language) {
                "java" -> JavaLanguage()
                "c" -> CLanguage()
                "cpp" -> CPPLanguage()
                "python" -> PythonLanguage()
                "javascript" -> JavaScriptLanguage()
                "typescript" -> TypeScriptLanguage()
                "text" -> NaturalLanguage()
                else -> throw IllegalArgumentException("Language not supported")
            }
        }

        fun languageToFileSuffix(language: String): String {
            return when (language) {
                "java" -> ".java"
                "c" -> ".c"
                "cpp" -> ".cpp"
                "python" -> ".py"
                "javascript" -> ".js"
                "typescript" -> ".ts"
                "text" -> ".txt"
                else -> throw IllegalArgumentException("Language not supported")
            }
        }
    }
}

const val TEMP_RESOURCES_PATH = "src/main/resources/temp"
const val ALLOWED_LANGUAGES = "java, c, cpp, python, javascript, typescript, text"
