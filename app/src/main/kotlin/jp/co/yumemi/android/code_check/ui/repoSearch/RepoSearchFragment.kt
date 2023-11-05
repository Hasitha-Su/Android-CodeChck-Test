package jp.co.yumemi.android.code_check.ui.repoSearch

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.databinding.FragmentRepoSearchBinding
import jp.co.yumemi.android.code_check.model.RepoItem
import jp.co.yumemi.android.code_check.ui.repoItemDetails.RepoItemDetailsViewModel
import jp.co.yumemi.android.code_check.util.NetworkStatusHelper

/**
 * Fragment class for searching GitHub repositories.
 *
 * This fragment is responsible for displaying a search interface to search for GitHub repositories.
 * It uses a RecyclerView to display the search results and handles user interactions such as item click.
 *
 */

@AndroidEntryPoint
class RepoSearchFragment : Fragment() {

    val viewModel: RepoSearchViewModel by viewModels()

    private var _binding: FragmentRepoSearchBinding? = null
    private val binding get() = _binding!!
    private val detailsViewModel: RepoItemDetailsViewModel by activityViewModels()
    private var networkErrorDialog: AlertDialog? = null
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private var shouldPerformSearchOnNetworkAvailable = false
    private var isNetworkAvailable: Boolean = false
    private lateinit var networkStatusHelper: NetworkStatusHelper
    private var comingFromSettings = false
    private val handler = Handler(Looper.getMainLooper())

    private val connectivityManager by lazy {
        requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkStatusHelper = NetworkStatusHelper()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if (shouldPerformSearchOnNetworkAvailable) {
                    handler.post {
                        dismissNetworkErrorDialog()
                        viewModel.search()
                    }
                }
            }

            override fun onLost(network: Network) {
                handler.post {
                    showNetworkErrorDialog()
                }
            }
        }
    }

    fun onSearchInitiated() {
        val searchQuery = binding.searchInputText.text.toString()
        if (searchQuery.isNotEmpty()) {
            shouldPerformSearchOnNetworkAvailable = true
            performSearchWithNetworkCheck(searchQuery)
        } else {
            showToast("Please enter a search query.")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_repo_search, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        networkStatusHelper.networkStatus.observe(viewLifecycleOwner) { networkAvailable ->
            isNetworkAvailable = networkAvailable

            if (isNetworkAvailable) {
                if (shouldPerformSearchOnNetworkAvailable) {
                    dismissNetworkErrorDialog()
                    viewModel.search()
                }
            } else {
                showNetworkErrorDialog()
            }
        }

        setupRecyclerView()
        setupSearchBar()

        viewModel.results.observe(viewLifecycleOwner) { results ->
            (binding.recyclerView.adapter as? CustomAdapter)?.submitList(results)
        }

        viewModel.uiMessage.observe(viewLifecycleOwner) { uiMessage ->
            uiMessage?.let {
                showToast(it)
            }
        }
    }

    private fun setupSearchBar() {
        binding.searchInputText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchInitiated()
                true
            } else {
                false
            }
        }
    }

    private fun performSearchWithNetworkCheck(searchQuery: String) {
        if (isNetworkAvailable) {
            // Network is available, proceed with search.
            viewModel.searchQuery.value = searchQuery
            viewModel.search()
        } else {
            // No network available, show message.
            showToast("No internet connection. Please try again later.")
        }
    }

    private fun showNetworkErrorDialog() {
        if (networkErrorDialog?.isShowing != true) {
            networkErrorDialog = AlertDialog.Builder(requireContext())
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton("Open Settings") { _, _ ->
                    comingFromSettings = true
                    startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                }
                .setNegativeButton("Exit") { _, _ ->
                    requireActivity().finish()
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()

        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        isNetworkAvailable = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false

        handleInitialNetworkState()

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    private fun handleInitialNetworkState() {
        if (isNetworkAvailable) {
            dismissNetworkErrorDialog()
        } else {
            showNetworkErrorDialog()
        }
    }

    override fun onStop() {
        super.onStop()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun dismissNetworkErrorDialog() {
        if (networkErrorDialog?.isShowing == true) {
            networkErrorDialog?.dismiss()
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        handler.removeCallbacksAndMessages(null)
        if (comingFromSettings) {
            handler.postDelayed({
                checkNetworkAndHandle()
            }, 3000)
        } else {
            checkNetworkAndHandle()
        }

        comingFromSettings = false
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun checkNetworkAndHandle() {
        if (isNetworkAvailable) {
            dismissNetworkErrorDialog()
        } else {
            showNetworkErrorDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    override fun onDestroy() {
        super.onDestroy()
        dismissNetworkErrorDialog()
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
