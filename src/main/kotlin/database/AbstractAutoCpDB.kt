package database

import com.intellij.openapi.project.Project
import database.models.ProblemSpec
import database.models.ProblemState
import database.models.TestcaseSpec
import database.tables.*
import database.utils.decodedSplit
import database.utils.encodedJoin
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

abstract class AbstractAutoCpDB(project: Project) : AutoCp {

    final override val instance = Database.connect("jdbc:sqlite:${project.basePath}/.autocp", "org.sqlite.JDBC")

    init {
        // initializing Tables
        transaction(instance) {
            SchemaUtils.create(ProblemSpecs, TestcaseSpecs, SolutionSpecs)
        }
    }


    protected fun addProblemSpecAndState(spec: ProblemSpec, state: ProblemState): String {
        return ProblemSpecs.insert {
            it[problemId] = encodedJoin(spec.name, spec.group)
            it[selectedIndex] = state.selectedIndex
        }[ProblemSpecs.problemId]
    }

    protected fun addTestcase(problemId: String, testcaseSpecs: List<TestcaseSpec>) {
        TestcaseSpecs.batchInsert(testcaseSpecs) { spec ->
            this[TestcaseSpecs.problemId] = problemId
            this[TestcaseSpecs.name] = spec.name
            this[TestcaseSpecs.input] = spec.input
            this[TestcaseSpecs.output] = spec.output
        }
    }


    protected fun getProblemSpecAndState(problemId: String): Pair<ProblemSpec, ProblemState>? {
        return ProblemSpecs.select { ProblemSpecs.problemId eq problemId }
            .firstOrNull()
            ?.let {
                it[ProblemSpecs.problemId]
                    .decodedSplit()
                    .let { data ->
                        Pair(
                            ProblemSpec(data[0], data[1]),
                            ProblemState(problemId, it[ProblemSpecs.selectedIndex])
                        )
                    }
            }
    }
}