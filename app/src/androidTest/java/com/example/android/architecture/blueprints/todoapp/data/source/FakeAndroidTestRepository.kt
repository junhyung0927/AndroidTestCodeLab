package com.example.android.architecture.blueprints.todoapp.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.runBlocking
import java.lang.Exception

class FakeAndroidTestRepository: TasksRepository {
    //network
    var tasksServiceData: LinkedHashMap<String, Task> = LinkedHashMap()

    //error
    private var shouldReturnError = false

    // local
    private val observableTasks = MutableLiveData<Result<List<Task>>>()

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        //network
        if (shouldReturnError) {
            return Result.Error(Exception("Test Exception"))
        }
        return Result.Success(tasksServiceData.values.toList())
    }

    override suspend fun refreshTasks() {
        //local -> LiveData delete 필요 없음
        observableTasks.value = getTasks()
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        runBlocking { refreshTasks() }
        return observableTasks
    }

    fun addTasks(vararg tasks: Task) {
        for (task in tasks) {
            tasksServiceData[task.id] = task
        }
        runBlocking { refreshTasks() }
    }

    override suspend fun refreshTask(taskId: String) {
        TODO("Not yet implemented")
    }

    //하나의 테스크에 대한 옵저브
    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        runBlocking { refreshTasks() }
        return observeTasks().map { tasks ->
            when(tasks) {
                is Result.Loading -> Result.Loading
                is Result.Error -> Result.Error(tasks.exception)
                is Result.Success -> {
                    val task = tasks.data.firstOrNull() { it.id == taskId }
                        ?: return@map  Result.Error(Exception("Not Found"))
                    Result.Success(task)
                }
            }
        }
    }

    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
        if (shouldReturnError) {
            return Result.Error(Exception("Test Exception"))
        }
        tasksServiceData[taskId]?.let {
            return Result.Success(it)
        }
        return Result.Error(Exception("Could not find task"))
    }

    override suspend fun saveTask(task: Task) {
        tasksServiceData[task.id] = task
    }

    override suspend fun completeTask(task: Task) {
        //무조건 complete
        val completedTask = task.copy(isCompleted = true)
        tasksServiceData[task.id] = completedTask
    }

    override suspend fun completeTask(taskId: String) {
        //remote 안하니깐 refreshTasks 안함
        throw NotImplementedError()
    }

    override suspend fun activateTask(task: Task) {
        //무조건 active
        val activeTask = task.copy(isCompleted = false)
        tasksServiceData[task.id] = activeTask
    }

    override suspend fun activateTask(taskId: String) {
        throw NotImplementedError()
    }

    override suspend fun clearCompletedTasks() {
        tasksServiceData  = tasksServiceData.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Task>
    }

    override suspend fun deleteAllTasks() {
        tasksServiceData.clear()
        refreshTasks()
    }

    override suspend fun deleteTask(taskId: String) {
        tasksServiceData.remove(taskId)
        refreshTasks()
    }
}