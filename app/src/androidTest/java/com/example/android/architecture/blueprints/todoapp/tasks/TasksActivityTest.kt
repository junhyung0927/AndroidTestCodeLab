package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.ServiceLocator
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import kotlinx.coroutines.runBlocking
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.util.DataBindingIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.monitorActivity
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class TasksActivityTest {

    private lateinit var repository: TasksRepository
    private val dataBindingIdlingResource = DataBindingIdlingResource()


    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)

        repository = ServiceLocator.provideTasksRepository(
            ApplicationProvider.getApplicationContext()
        )
        runBlocking {
            repository.deleteAllTasks()
        }
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)

        ServiceLocator.resetRepository()
    }

    @Test
    fun editTask() = runBlocking {
        //given
        repository.saveTask(Task("TITLE1", "DES"))

        //when
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //then
        onView(withText("TITLE1")).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("TITLE1")))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("DES")))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))

        onView(withId(R.id.edit_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("NEW TITLE"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("NEW DES"))
        onView(withId(R.id.save_task_fab)).perform(click())

        onView(withText("NEW TITLE")).check(matches(isDisplayed()))
        onView(withText("TITLE1")).check(doesNotExist())
        activityScenario.close()
    }

    @Test
    fun createOneTask_deleteTask() {

        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // 추가
        onView(withId(R.id.add_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("New Title"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("New Des"))

        // 저장
        onView(withId(R.id.save_task_fab)).perform(click())

        // 저장됐는지 체크
        onView(withText("New Title")).perform(click())

        // 삭제
        onView(withId(R.id.menu_delete)).perform(click())

        // 삭제 체크
        onView(withText("New Title")).check(doesNotExist())

        activityScenario.close()
    }
}