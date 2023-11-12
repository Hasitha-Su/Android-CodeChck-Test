package jp.co.yumemi.android.code_check.itemdetails

import android.os.Build
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.testing.HiltAndroidTest
import jp.co.yumemi.android.code_check.model.Owner
import jp.co.yumemi.android.code_check.model.RepoItem
import jp.co.yumemi.android.code_check.ui.RepoItemDetailsFragment
import jp.co.yumemi.android.code_check.view.RepoItemDetailsViewModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class RepoItemDetailsFragmentTest {

    private lateinit var fragment: RepoItemDetailsFragment
    private lateinit var viewModel: RepoItemDetailsViewModel
    private lateinit var activity: FragmentActivity

    @Before
    fun setUp() {

        viewModel = RepoItemDetailsViewModel()
        fragment = RepoItemDetailsFragment().apply {
        }

        activity = Robolectric.buildActivity(FragmentActivity::class.java)
            .create()
            .start()
            .resume()
            .get()

        activity.supportFragmentManager.beginTransaction()
            .add(fragment, null)
            .commitNow()

        val testOwner = Owner(avatarUrl = "https://example.com/avatar.jpg")
        viewModel.selectItem(RepoItem(
            name = "Test Repo",
            owner = testOwner,
            forksCount = 123,
            description = "test description",
            stargazersCount = 42,
            watchersCount = 7
        ))
    }

    @Test
    fun testSelectedRepoItemDisplayedCorrectly() {

    }
}