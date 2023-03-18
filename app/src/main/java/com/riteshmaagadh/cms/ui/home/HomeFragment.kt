package com.riteshmaagadh.cms.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.riteshmaagadh.cms.data.adapters.GroupsAdapter
import com.riteshmaagadh.cms.data.models.Group
import com.riteshmaagadh.cms.databinding.FragmentHomeBinding
import com.riteshmaagadh.cms.ui.Utils
import com.riteshmaagadh.cms.ui.error.ErrorActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var isScrolling = false
    private var currentItems = 0
    private var totalItems = 0
    private var scrolledOutItems = 0
    private val collectionRef = FirebaseFirestore.getInstance()
        .collection("whatsapp_groups")
    private lateinit var lastDoc: DocumentSnapshot
    private lateinit var groupsAdapter: GroupsAdapter
    private val groupList: ArrayList<Group> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        initUI()
        return binding.root
    }

    private fun initUI() {
        if (Utils.isOnline(requireContext())){
            binding.progressBar.visibility = View.VISIBLE

            val layoutManager = LinearLayoutManager(requireContext())

            binding.recyclerView.layoutManager = layoutManager

            groupsAdapter = GroupsAdapter(groupList, requireContext(), object : GroupsAdapter.AdapterCallbacks {
                override fun onJoinButtonClicked(groupLink: String) {
                    Utils.openWhatsapp(requireContext(), groupLink)
                }
            })
            binding.recyclerView.adapter = groupsAdapter



            binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        isScrolling = true
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    currentItems = layoutManager.childCount
                    totalItems = layoutManager.itemCount
                    scrolledOutItems = layoutManager.findFirstVisibleItemPosition()

                    if (isScrolling && (currentItems + scrolledOutItems == totalItems)) {
                        isScrolling = false
                        fetchData()
                    }
                }
            })

            lifecycleScope.launch(Dispatchers.IO){
                collectionRef
                    .orderBy("index", Query.Direction.DESCENDING)
                    .whereEqualTo("active",false)
                    .limit(10)
                    .get()
                    .addOnSuccessListener {
                        lastDoc = it.documents[it.size() - 1]
                        val groups = it.toObjects(Group::class.java)
                        groupList.addAll(groups)
                        groupsAdapter.notifyDataSetChanged()
                        binding.progressBar.visibility = View.GONE
                    }
                    .addOnFailureListener {
                        binding.progressBar.visibility = View.GONE
                        val intent = Intent(requireContext(), ErrorActivity::class.java)
                        intent.putExtra("has_network_gone",false)
                        startActivity(intent)
                        requireActivity().finish()
                    }
            }

        } else {
            val intent = Intent(requireContext(), ErrorActivity::class.java)
            intent.putExtra("has_network_gone",true)
            startActivity(intent)
            requireActivity().finish()
        }
    }


    private fun fetchData() {
        lifecycleScope.launch(Dispatchers.IO) {
            collectionRef
                .orderBy("index", Query.Direction.DESCENDING)
                .whereEqualTo("active",false)
                .startAfter(lastDoc)
                .limit(10)
                .get()
                .addOnSuccessListener {
                    if (it.documents.isNotEmpty()) {
                        lastDoc = it.documents[it.size() - 1]
                        val groups = it.toObjects(Group::class.java)
                        groupList.addAll(groups)
                        groupsAdapter.notifyDataSetChanged()
                    }
                }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment()
    }
}