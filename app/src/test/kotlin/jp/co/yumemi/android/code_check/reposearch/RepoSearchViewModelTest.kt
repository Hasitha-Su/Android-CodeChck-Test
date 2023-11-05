import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.mockk
import jp.co.yumemi.android.code_check.model.Owner
import jp.co.yumemi.android.code_check.model.RepoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import androidx.lifecycle.Observer
import io.mockk.verify
import jp.co.yumemi.android.code_check.ui.repoItemDetails.RepoItemDetailsViewModel
import kotlinx.coroutines.test.*
import org.junit.*

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class RepoItemDetailsViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RepoItemDetailsViewModel
    private val testDispatcher = TestCoroutineDispatcher()
    private val selectedItemObserver: Observer<RepoItem> = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RepoItemDetailsViewModel()
        viewModel.selectedItem.observeForever(selectedItemObserver)
    }

    @After
    fun tearDown() {
        viewModel.selectedItem.removeObserver(selectedItemObserver)
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun selectItem_updatesSelectedItem() = runTest {
        val testItem = RepoItem(
            name = "Test Repo",
            owner = Owner(avatarUrl = "https://example.com/avatar.jpg"),
            forksCount = 123,
            language = "Kotlin",
            openIssuesCount = 11,
            stargazersCount = 42,
            watchersCount = 7
        )

        viewModel.selectItem(testItem)

        runCurrent()

        verify { selectedItemObserver.onChanged(testItem) }
        assertEquals(testItem, viewModel.selectedItem.value)
    }
}

