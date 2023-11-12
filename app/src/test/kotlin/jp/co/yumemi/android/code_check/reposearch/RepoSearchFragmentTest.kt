package jp.co.yumemi.android.code_check.reposearch

import android.os.Build
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import jp.co.yumemi.android.code_check.HiltTestActivity
import jp.co.yumemi.android.code_check.databinding.FragmentRepoSearchBinding
import jp.co.yumemi.android.code_check.model.Owner
import jp.co.yumemi.android.code_check.model.RepoItem
import jp.co.yumemi.android.code_check.ui.CustomAdapter
import jp.co.yumemi.android.code_check.ui.RepoSearchFragment
import jp.co.yumemi.android.code_check.view.RepoSearchViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config


@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], application = HiltTestApplication::class)
class RepoSearchFragmentTest {


    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var binding: FragmentRepoSearchBinding
    private lateinit var fragment: RepoSearchFragment
    private lateinit var activity: FragmentActivity

    @MockK
    private lateinit var mockViewModel: RepoSearchViewModel

    @Before
    fun setUp() {
        hiltRule.inject()
        MockKAnnotations.init(this)

        mockViewModel = mockk(relaxed = true)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return mockViewModel as T
            }
        }

        activity = Robolectric.buildActivity(HiltTestActivity::class.java)
            .create()
            .start()
            .resume()
            .get()

        fragment = RepoSearchFragment()

        activity.supportFragmentManager.beginTransaction()
            .add(fragment, null)
            .commitNow()

        ViewModelProvider(fragment, factory)[RepoSearchViewModel::class.java]

        binding = FragmentRepoSearchBinding.bind(fragment.requireView())
        binding.executePendingBindings()
    }

    @Test
    fun searchInput_performsSearch_displaysResults() {

        val searchQueryLiveData = MutableLiveData<String>()
        val resultsLiveData = MutableLiveData<List<RepoItem>>()

        every { mockViewModel.searchQuery } returns searchQueryLiveData
        every { mockViewModel.results } returns resultsLiveData

        val searchString = "repo1"
        searchQueryLiveData.value = searchString

        binding.searchInputText.setText(searchString)

        val expectedItems = listOf(
            RepoItem(
                name = "repo1",
                owner = Owner(avatarUrl = "https://example.com/avatar.jpg"),
                forksCount = 42,
                description = "test description",
                stargazersCount = 100,
                watchersCount = 50
            )
        )

        resultsLiveData.value = expectedItems

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        val observer = Observer<List<RepoItem>> {}

        try {
            mockViewModel.results.observeForever(observer)
            resultsLiveData.value = expectedItems

            val recyclerView = binding.recyclerView
            val adapter = recyclerView.adapter as? CustomAdapter
                ?: throw AssertionError("RecyclerView adapter is not of type CustomAdapter")

            assertEquals("Adapter item count", expectedItems.size, adapter.itemCount)

            expectedItems.forEachIndexed { index, repoItem ->
                val actualItem = adapter.currentList[index]
                assertEquals(repoItem.name, actualItem.name)
            }
        } finally {
            mockViewModel.results.removeObserver(observer)
        }

        verify { mockViewModel.search() }

    }
}