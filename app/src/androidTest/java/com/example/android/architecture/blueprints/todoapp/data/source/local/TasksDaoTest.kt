package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TasksDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ToDoDatabase

    @Before
    fun initDao() {
        //테스트가 끝나면 데이터베이스 지워짐
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertTaskAndGetById() = runBlockingTest {
        //given
        val task = Task("title", "des")
        database.taskDao().insertTask(task)

        //when
        val loaded = database.taskDao().getTaskById(task.id)

        //then
        assertThat<Task>(loaded as Task, notNullValue())
        assertThat(loaded.id, `is`(task.id))
        assertThat(loaded.title, `is`(task.title))
        assertThat(loaded.description, `is`(task.description))
        assertThat(loaded.isCompleted, `is`(task.isCompleted))
    }

    @Test
    fun updateTaskAndGetById() = runBlockingTest {
        //given
        val task = Task("title", "des")
        database.taskDao().insertTask(task)

        //when
        val updateTask = task.copy("updateTitle", "updateDes")
        database.taskDao().updateTask(updateTask)

        //then
        val loaded = database.taskDao().getTaskById(taskId = task.id)
        assertThat<Task>(loaded as Task, notNullValue())
        assertThat(loaded.id, `is`(updateTask.id))
        assertThat(loaded.title, `is`(updateTask.title))
        assertThat(loaded.description, `is`(updateTask.description))
        assertThat(loaded.isCompleted, `is`(updateTask.isCompleted))
    }
}