package database

import com.intellij.util.containers.OrderedSet
import dev.pushpavel.autocp.database.Problem

interface IAutoCp : AutoCloseable {
    fun insertProblems(problems: List<Problem>): Result<Unit>
    fun associateSolutionToProblem(solutionPath: String, problem: Problem): Result<Unit>
    fun getProblem(solutionPath: String): Result<Problem>
    fun updateProblemState(problem: Problem, selectedIndex: Long): Result<Unit>
    fun updateTestcases(problem: Problem, testcases: OrderedSet<database.models.Testcase>): Result<Unit>
}