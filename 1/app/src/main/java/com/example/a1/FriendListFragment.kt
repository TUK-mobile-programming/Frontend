package com.example.a1

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a1.capsule.CapsuleFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class FriendListFragment : Fragment() {

    private lateinit var editFriendName: EditText
    private lateinit var btnClear: ImageView
    private lateinit var searchResultRecyclerView: RecyclerView
    private lateinit var friendRecyclerView: RecyclerView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var backBtn: ImageView

    private val allFriends = mutableListOf("철수", "영희", "민수", "수진", "지훈")
    private val searchResults = mutableListOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friend_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        editFriendName = view.findViewById(R.id.editFriendName)
        btnClear = view.findViewById(R.id.btnClear)
        searchResultRecyclerView = view.findViewById(R.id.searchResultRecyclerView)
        friendRecyclerView = view.findViewById(R.id.friendRecyclerView)
        bottomNav = view.findViewById(R.id.bottomNavigationView)
        backBtn = view.findViewById(R.id.btn_back)

        // 뒤로가기
        backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // 친구 목록
        friendRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        friendRecyclerView.adapter = FriendAdapter(allFriends)

        // 검색 결과
        searchResultRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchResultRecyclerView.adapter = FriendAdapter(searchResults)

        // 실시간 검색
        editFriendName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    searchResultRecyclerView.visibility = View.GONE
                } else {
                    searchResults.clear()
                    searchResults.addAll(allFriends.filter { it.contains(query) })
                    searchResultRecyclerView.adapter?.notifyDataSetChanged()
                    searchResultRecyclerView.visibility = View.VISIBLE
                }
            }
        })

        // 키보드 검색
        editFriendName.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                val query = editFriendName.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchResults.clear()
                    searchResults.addAll(allFriends.filter { it.contains(query) })
                    searchResultRecyclerView.adapter?.notifyDataSetChanged()
                    searchResultRecyclerView.visibility = View.VISIBLE
                } else {
                    searchResultRecyclerView.visibility = View.GONE
                }
                true
            } else {
                false
            }
        }

        // Clear 버튼
        btnClear.setOnClickListener {
            editFriendName.text.clear()
            searchResultRecyclerView.visibility = View.GONE
        }

        // 하단 네비게이션
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mypagefragment -> {
                    startActivity(Intent(requireContext(), Mypagefragment::class.java))
                    true
                }

                R.id.homefragment -> {
                    startActivity(Intent(requireContext(), Homefragment::class.java))
                    true
                }

                R.id.capsulefragment -> {
                    startActivity(Intent(requireContext(), CapsuleFragment::class.java))
                    true
                }

                R.id.addfragment -> {
                    startActivity(Intent(requireContext(), AddFragment::class.java))
                    true
                }

                R.id.listfragment -> {
                    startActivity(Intent(requireContext(), Listactivity::class.java))
                    true
                }

                else -> false
            }
        }
    }


    inner class FriendAdapter(private val items: List<String>) :
        RecyclerView.Adapter<FriendAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val friendNameTextView: TextView = itemView.findViewById(R.id.friendNameTextView)
            val btnAddFriend: Button = itemView.findViewById(R.id.btnAddFriend)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = items.size
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val friendName = items[position]
            holder.friendNameTextView.text = friendName

            // 추가 버튼 클릭 리스너 (예: 토스트 출력)
            holder.btnAddFriend.setOnClickListener {
                // TODO: 실제 친구 추가 로직 넣기
                Toast.makeText(holder.itemView.context, "$friendName 친구 추가!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
