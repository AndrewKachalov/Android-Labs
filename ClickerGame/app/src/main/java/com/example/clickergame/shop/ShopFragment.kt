package com.example.clickergame.shop

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clickergame.R
import com.example.clickergame.adapters.ShopAdapter
import com.example.clickergame.viewmodels.ClickerViewModel

//Фрагмент магазин
class ShopFragment : Fragment() {
    //отримання вьюв моделі
    private val viewModel: ClickerViewModel by activityViewModels()
    //UI компонент
    private lateinit var recyclerView: RecyclerView
    private lateinit var shopAdapter: ShopAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //відображення лайауту магазину
        val view = inflater.inflate(R.layout.fragment_shop, container, false)
        //пошук в макетах
        recyclerView = view.findViewById(R.id.shopRecyclerView)
        //аналізуємо зміну списку товарів
        viewModel.shopItems.observe(viewLifecycleOwner) { updatedItems ->
            //Якщо позиція вже є оновлюємо її
            if (::shopAdapter.isInitialized) {
                shopAdapter.updateItems(updatedItems)
                //або створюємо якщо немає
            } else {
                shopAdapter = ShopAdapter(updatedItems, viewModel)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = shopAdapter
            }
        }
        //поверенення готово ui
        return view
    }
}
