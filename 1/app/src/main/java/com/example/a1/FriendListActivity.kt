package com.example.a1

import CapsuleFragment
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class FriendListActivity : AppCompatActivity() {
    private lateinit var editFriendName: EditText
    private lateinit var btnClear: ImageView
    private lateinit var searchResultRecyclerView: RecyclerView
    private lateinit var friendRecyclerView: RecyclerView
    private lateinit var bottomNav: BottomNavigationView

    private val allFriends = mutableListOf("철수", "영희", "민수", "수진", "지훈")
    private val searchResults = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)

        editFriendName = findViewById(R.id.editFriendName)
        btnClear = findViewById(R.id.btnClear)
        searchResultRecyclerView = findViewById(R.id.searchResultRecyclerView)
        friendRecyclerView = findViewById(R.id.friendRecyclerView)
        bottomNav = findViewById(R.id.bottomNavigationView)

        // 친구 리스트
        friendRecyclerView.layoutManager = LinearLayoutManager(this)
        friendRecyclerView.adapter = FriendAdapter(allFriends)

        // 검색 결과 리스트
        searchResultRecyclerView.layoutManager = LinearLayoutManager(this)
        searchResultRecyclerView.adapter = FriendAdapter(searchResults)

        // 검색 텍스트 감지
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

        // Clear 버튼
        btnClear.setOnClickListener {
            editFriendName.text.clear()
            searchResultRecyclerView.visibility = View.GONE
        }

        // 하단 메뉴 클릭 처리
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mypagefragment -> {
                    startActivity(Intent(this, Mypagefragment::class.java))
                    true
                }
                // 나머지 메뉴는 필요에 따라 연결
                R.id.homefragment -> {
                    startActivity(Intent(this, Homefragment::class.java))
                    true
                }
                R.id.capsulefragment -> {
                    startActivity(Intent(this, CapsuleFragment::class.java))
                    true
                }
                R.id.addfragment -> {
                    startActivity(Intent(this, Addfragment::class.java))
                    true
                }
                R.id.listfragment -> {
                    startActivity(Intent(this, Listactivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    inner class FriendAdapter(private val items: List<String>) :
        RecyclerView.Adapter<FriendAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val text: TextView = itemView.findViewById(android.R.id.text1)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.text.text = items[position]
        }
    }
}