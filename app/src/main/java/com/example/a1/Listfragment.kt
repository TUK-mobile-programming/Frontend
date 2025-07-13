package com.example.a1

import android.content.Intent // Intent 사용을 위해 import 합니다.
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout // LinearLayout을 사용하므로 import 합니다.

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Listfragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Listfragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_list.xml 레이아웃을 인플레이트합니다.
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        // ID가 'first_list'인 LinearLayout을 찾습니다.
        // fragment_list.xml에 해당 ID가 있는 LinearLayout이 있어야 합니다.
        val firstListLayout: LinearLayout = view.findViewById(R.id.first_list)

        // 찾은 LinearLayout에 클릭 리스너를 설정합니다.
        firstListLayout.setOnClickListener {
            // ListActivity로 이동하는 Intent를 생성합니다.
            // 'activity'는 Fragment가 현재 연결된 Activity의 Context를 나타냅니다.
            val intent = Intent(activity, Listactivity::class.java)
            // 액티비티를 시작합니다.
            startActivity(intent)
        }

        // 인플레이트된 뷰를 반환합니다.
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Listfragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Listfragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}