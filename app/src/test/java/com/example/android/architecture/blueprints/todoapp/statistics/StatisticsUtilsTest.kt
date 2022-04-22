package com.example.android.architecture.blueprints.todoapp.statistics

import com.example.android.architecture.blueprints.todoapp.data.Task
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class StatisticsUtilsTest {
    @Test
    fun getActiveAndCompletedStats_noCompleted_returnsHundredZero() {
        // Create an active task(given)
        val tasks = listOf<Task>(
            Task("title", "desc", isCompleted = false)
        )

        // Call your function(when)
        val result = getActiveAndCompletedStats(tasks)

        // Check the result(then)
        assertThat(result.completedTasksPercent, `is`(0f))
        assertThat(result.activeTasksPercent, `is`(100f))
    }

    @Test
    fun getActiveAndCompletedStats_empty_returnsZeros() {
        //given
        val tasks = listOf<Task>()

        //when
        val result = getActiveAndCompletedStats(tasks)

        assertThat(result.completedTasksPercent, `is`(0f))
        assertThat(result.activeTasksPercent, `is`(0f))
    }

    @Test
    fun getActiveAndCompletedStats_noActive_returnZeroHundred() {
        //given
        val tasks = listOf<Task>(
            Task("title", "desc", isCompleted = true)
        )
        // Call your function(when)
        val result = getActiveAndCompletedStats(tasks)

        // Check the result(then)
        assertThat(result.completedTasksPercent, `is`(100f))
        assertThat(result.activeTasksPercent, `is`(0f))
    }

    @Test
    fun getActiveAndCompletedStats_error_returnsZeros() {
        //given
        val tasks = null

        //when
        val result = getActiveAndCompletedStats(tasks)

        //then
        assertThat(result.completedTasksPercent, `is`(0f))
        assertThat(result.activeTasksPercent, `is`(0f))
    }

    @Test
    fun getActiveAndCompletedStats_both_returnFortySixty() {
        //given
        val tasks = listOf<Task>(
            Task("title", "desc", isCompleted = true),
            Task("title", "desc", isCompleted = true),
            Task("title", "desc", isCompleted = false),
            Task("title", "desc", isCompleted = false),
            Task("title", "desc", isCompleted = false)
        )

        //when
        val result = getActiveAndCompletedStats(tasks)

        //result
        assertThat(result.completedTasksPercent, `is`(40f))
        assertThat(result.activeTasksPercent, `is`(60f))
    }
}