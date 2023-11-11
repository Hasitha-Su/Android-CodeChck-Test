package jp.co.yumemi.android.code_check.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.databinding.FragmentRepoSearchBinding
import jp.co.yumemi.android.code_check.view.RepoSearchViewModel
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.model.RepoItem
import jp.co.yumemi.android.code_check.utils.MessageUtils
import jp.co.yumemi.android.code_check.utils.NetworkUtils
import jp.co.yumemi.android.code_check.view.RepoItemDetailsViewModel
import javax.inject.Inject

/**
 * Fragment class for searching GitHub repositories.
 *
 * This fragment is responsible for displaying a search interface to search for GitHub repositories.
 * It uses a RecyclerView to display the search results and handles user interactions such as item click.
 *
 */

@AndroidEntryPoint
class RepoSearchFragment : Fragment(R.layout.fragment_repo_search) {

    @Inject
    lateinit var networkUtils: NetworkUtils
    private val viewModel: RepoSearchViewModel by viewModels()
    private var _binding: FragmentRepoSearchBinding? = null
    private val binding get() = _binding!!
    private var networkErrorDialog: AlertDialog? = null

    private val detailsViewModel: RepoItemDetailsViewModel by activityViewModels()

    /**
     * Sets up observers and initializes the fragment's UI components.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        observeNetworkChanges()
        _binding = FragmentRepoSearchBinding.bind(view)

        setupRecyclerView()
        setupSearchBar()

        viewModel.results.observe(viewLifecycleOwner) { results ->
            (binding.recyclerView.adapter as? CustomAdapter)?.submitList(results)
        }
    }

    /**
     * Observes network changes and shows or dismisses the network error dialog accordingly.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun observeNetworkChanges() {
        networkUtils.networkStatus.observe(viewLifecycleOwner) { isAvailable ->
            if (isAvailable) {
                dismissNetworkErrorDialog()
            } else {
                showNetworkErrorDialog()
            }
        }
    }

    /**
     * Checks network availability on resume and shows or dismisses the network error dialog.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()
        if (!networkUtils.isNetworkAvailable()) {
            showNetworkErrorDialog()
        } else {
            networkErrorDialog?.dismiss()
        }
    }

    /**
     * Shows a dialog when the network is unavailable.
     */
    private fun showNetworkErrorDialog() {
        if (networkErrorDialog?.isShowing != true) {
            networkErrorDialog = MessageUtils.showNetworkErrorDialog(
                context = requireContext(),
                onSettingsClick = {
                    startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                },
                onDismiss = {
                    networkErrorDialog?.dismiss()
                }
            )
        }
    }

    /**
     * Dismisses the network error dialog if it's showing.
     */
    private fun dismissNetworkErrorDialog() {
        if (networkErrorDialog?.isShowing == true) {
            networkErrorDialog?.dismiss()
        }
    }

    /**
     * Sets up the RecyclerView used for displaying repository search results.
     */
    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.recyclerView.apply {
            this.layoutManager = layoutManager
            addItemDecoration(dividerItemDecoration)
            adapter = CustomAdapter { item -> gotoRepositoryFragment(item) }
        }
    }

    /**
     * Configures the search bar for initiating repository searches.
     */
    private fun setupSearchBar() {
        binding.searchInputText.setOnEditorActionListener { editText, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.searchQuery.value = editText.text.toString()
                val searchQuery = binding.searchInputText.text.toString()

                if (searchQuery.isNotEmpty()) {
                    viewModel.search()
                } else {
                    Toast.makeText(requireContext(), "Please enter a search query", Toast.LENGTH_SHORT).show()
                }
                true
            } else false
        }
    }

    /**
     * Cleans up resources and observers when the view is being destroyed.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDestroyView() {
        super.onDestroyView()
        networkUtils.unregisterNetworkCallback()
        viewModel.results.removeObservers(viewLifecycleOwner)
        _binding = null
    }

    /**
     * Navigates to the repository details fragment for the selected repository item.
     */
    private fun gotoRepositoryFragment(item: RepoItem) {
        detailsViewModel.selectItem(item)
        findNavController().navigate(R.id.action_repositoriesFragment_to_repositoryFragment)
    }
}

/**
 * Utility object for comparing repository items, used by the CustomAdapter.
 */
val diffUtil = object : DiffUtil.ItemCallback<RepoItem>() {
    override fun areItemsTheSame(oldItem: RepoItem, newItem: RepoItem): Boolean = oldItem.name == newItem.name
    override fun areContentsTheSame(oldItem: RepoItem, newItem: RepoItem): Boolean = oldItem == newItem
}

/**
 * Adapter for the RecyclerView used in RepoSearchFragment.
 *
 * This adapter handles the display of repository items in the RecyclerView.
 * It uses a DiffUtil for efficient item comparison and updates.
 */
class CustomAdapter(
    private val itemClickListener: (RepoItem) -> Unit
) : ListAdapter<RepoItem, CustomAdapter.ViewHolder>(diffUtil) {

    /**
     * ViewHolder for repository items in the RecyclerView.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        (holder.itemView.findViewById<View>(R.id.repositoryNameView) as TextView).text = item.name
        holder.itemView.setOnClickListener { itemClickListener(item) }
    }
}
