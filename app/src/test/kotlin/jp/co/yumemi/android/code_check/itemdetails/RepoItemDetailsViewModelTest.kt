package jp.co.yumemi.android.code_check.itemdetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.mockk
import io.mockk.verify
import jp.co.yumemi.android.code_check.model.Owner
import jp.co.yumemi.android.code_check.model.RepoItem
import jp.co.yumemi.android.code_check.ui.repoItemDetails.RepoItemDetailsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

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
        Assert.assertEquals(testItem, viewModel.selectedItem.value)
    }
}

