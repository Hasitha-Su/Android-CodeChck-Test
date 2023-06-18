package jp.co.yumemi.android.code_check.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.yumemi.android.code_check.model.RepoItem
import javax.inject.Inject

/**
 * View model class for managing the selected GitHub repository item in the details screen.
 *
 * This view model is responsible for holding the selected repository item and updating the UI accordingly.
 *
 */
@HiltViewModel
class RepoItemDetailsViewModel @Inject constructor() : ViewModel() {

    val selectedItem = MutableLiveData<RepoItem>()

    fun selectItem(item: RepoItem) {
        selectedItem.value = item
    }
}
