package com.example.surgeryapptest.ui.streamChat

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.FragmentUsersBinding
import com.example.surgeryapptest.utils.adapter.UsersAdapter
import com.example.surgeryapptest.utils.constant.Constants
import com.example.surgeryapptest.view_models.doctor.PatientListViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryUsersRequest
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UsersFragment : Fragment() {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private lateinit var patientListViewModel: PatientListViewModel
    private val usersAdapter by lazy { UsersAdapter() }

    private lateinit var patientList: String
    private lateinit var userType: String
    private var patientNameArrayList = listOf<String>()

    private val client = ChatClient.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        patientListViewModel =
            ViewModelProvider(requireActivity()).get(PatientListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUsersBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        loadUserType()

        lifecycleScope.launch {
            patientListViewModel.readPatientNameList.collect { values ->
                patientList = values
                patientNameArrayList = patientList.split(",")
            }
        }

        println("FORMATTED PATIENT LIST: $patientNameArrayList")

        setupToolbar()
        setupRecyclerView()
        queryAllUsers()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.users_menu, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query!!.isEmpty()) {
                    queryAllUsers()
                } else {
                    searchUser(query)
                }
                return true
            }
        })
        searchView?.setOnCloseListener {
            queryAllUsers()
            false
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        binding.usersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.usersRecyclerView.adapter = usersAdapter
    }

    private fun searchUser(query: String) {
        val filters = Filters.and(
            Filters.autocomplete("id", query),
            Filters.ne("id", client.getCurrentUser()!!.id)
        )
        val request = QueryUsersRequest(
            filter = filters,
            offset = 0,
            limit = 100
        )
        client.queryUsers(request).enqueue { result ->
            if (result.isSuccess) {
                val users: List<User> = result.data()
                usersAdapter.setData(users)
            } else {
                Log.e("UsersFragment", result.error().message.toString())
            }
        }
    }

    private fun queryAllUsers() {
        if (userType == "D") {
            val chatUser = patientNameArrayList
            val request = QueryUsersRequest(
                filter = Filters.`in`("id", chatUser), //--> this works well
                //filter = Filters.ne("id", client.getCurrentUser()!!.id),
                offset = 0,
                limit = 100
            )

            client.queryUsers(request).enqueue { result ->
                if (result.isSuccess) {
                    val users: List<User> = result.data()
                    usersAdapter.setData(users)
                } else {
                    Log.e("UsersFragment", result.error().message.toString())
                }
            }
        } else {
            //val chatUser = patientNameArrayList
            val request = QueryUsersRequest(
                filter = Filters.autocomplete("name", "_D"),  //--> this works well
                //filter = Filters.ne("id", client.getCurrentUser()!!.id),
                offset = 0,
                limit = 100
            )

            client.queryUsers(request).enqueue { result ->
                if (result.isSuccess) {
                    val users: List<User> = result.data()
                    usersAdapter.setData(users)
                } else {
                    Log.e("UsersFragment", result.error().message.toString())
                }
            }
        }
//        client.queryUsers(request).enqueue { result ->
//            if (result.isSuccess) {
//                val users: List<User> = result.data()
//                usersAdapter.setData(users)
//            } else {
//                Log.e("UsersFragment", result.error().message.toString())
//            }
//        }
    }

    private fun loadUserType() {
        viewLifecycleOwner.lifecycleScope.launch {
            patientListViewModel.readUserProfileDetail.collect { values ->
                userType = values.userType
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}