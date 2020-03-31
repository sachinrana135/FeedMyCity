package com.alfanse.feedindia.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.models.TodoEntity
import com.alfanse.feedindia.data.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class MainViewModelTest {

    lateinit var viewModel: MainViewModel

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    lateinit var repository: TodoRepository

    @Mock
    lateinit var observer: Observer<Resource<List<TodoEntity>>>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModel = MainViewModel(repository)
        viewModel.todoListLiveData.observeForever(observer)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `should success when api return proper data `() {
        runBlockingTest {
            var mockResponse = listOf(
                TodoEntity(
                    id = "1",
                    userId = "1",
                    userName = "Test",
                    title = "Test",
                    completed = true
                )
            )
            `when`(repository.getTodoList()).thenReturn(mockResponse)
            viewModel.getTodoList()
            viewModel.todoListLiveData.observeForever(observer)
            verify(observer).onChanged(Resource.loading(null))
            verify(observer).onChanged(Resource.success(mockResponse))
        }

    }

    @Test
    fun `should fail when api return empty response `() {
        runBlockingTest {
            var mockResponse = listOf<TodoEntity>()
            `when`(repository.getTodoList()).thenReturn(mockResponse)
            viewModel.getTodoList()

            verify(observer).onChanged(Resource.loading(null))
            verify(observer).onChanged(Resource.empty())
        }

    }

    @Test
    fun `should fail when api return error`() {
        runBlockingTest {
            var errorMsg = "Test Error"
            var exception = Error(errorMsg)
            `when`(repository.getTodoList()).thenThrow(exception)
            viewModel.getTodoList()
            viewModel.todoListLiveData.observeForever(observer)
            verify(observer).onChanged(Resource.loading(null))
            verify(observer).onChanged(Resource.error(errorMsg, null))
        }

    }
}