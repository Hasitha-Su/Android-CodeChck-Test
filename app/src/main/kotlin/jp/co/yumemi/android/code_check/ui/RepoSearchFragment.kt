package jp.co.yumemi.android.code_check.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.databinding.FragmentRepoSearchBinding
import jp.co.yumemi.android.code_check.view.RepoSearchViewModel
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.model.RepoItem
import jp.co.yumemi.android.code_check.view.RepoItemDetailsViewModel
/**
 * Fragment class for searching GitHub repositories.
 *
 * This fragment is responsible for displaying a search interface to search for GitHub repositories.
 * It uses a RecyclerView to display the search results and handles user interactions such as item click.
 *
 */

@AndroidEntryPoint
class RepoSearchFragment : Fragment(R.layout.fragment_repo_search) {

    private val viewModel: RepoSearchViewModel by viewModels()
    private var _binding: FragmentRepoSearchBinding? = null
    private val binding get() = _binding!!

    private val detailsViewModel: RepoItemDetailsViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRepoSearchBinding.bind(view)

        setupRecyclerView()
        setupSearchBar()

        viewModel.results.observe(viewLifecycleOwner) { results ->
            (binding.recyclerView.adapter as? CustomAdapter)?.submitList(results)
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.recyclerView.apply {
            this.layoutManager = layoutManager
            addItemDecoration(dividerItemDecoration)
            adapter = CustomAdapter { item -> gotoRepositoryFragment(item) }
        }
    }

    private fun setupSearchBar() {
        binding.searchInputText.setOnEditorActionListener { editText, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.searchQuery.value = editText.text.toString()
                viewModel.search()
                true
            } else false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.results.removeObservers(viewLifecycleOwner)
        _binding = null
    }

    private fun gotoRepositoryFragment(item: RepoItem) {
        detailsViewModel.selectItem(item)
        findNavController().navigate(R.id.action_repositoriesFragment_to_repositoryFragment)
    }
}

val diffUtil = object : DiffUtil.ItemCallback<RepoItem>() {
    override fun areItemsTheSame(oldItem: RepoItem, newItem: RepoItem): Boolean = oldItem.name == newItem.name
    override fun areContentsTheSame(oldItem: RepoItem, newItem: RepoItem): Boolean = oldItem == newItem
}

class CustomAdapter(
    private val itemClickListener: (RepoItem) -> Unit
) : ListAdapter<RepoItem, CustomAdapter.ViewHolder>(diffUtil) {

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
