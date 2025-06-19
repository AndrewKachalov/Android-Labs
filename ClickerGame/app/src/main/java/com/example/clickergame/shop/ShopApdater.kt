package com.example.clickergame.adapters

import android.view.*
import android.widget.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.clickergame.R
import com.example.clickergame.models.ShopItem
import com.example.clickergame.viewmodels.ClickerViewModel

//Відображення товару у магазині
class ShopAdapter(
    private var items: List<ShopItem>,
    private val viewModel: ClickerViewModel
) : RecyclerView.Adapter<ShopAdapter.ViewHolder>() {
    //список усього
    private val observers = mutableListOf<Observer<Int>>()

    //Збереження UI
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.itemIcon)
        val name: TextView = view.findViewById(R.id.itemName)
        val level: TextView = view.findViewById(R.id.itemLevel)
        val buyButton: ImageButton = view.findViewById(R.id.buyButton)
        val quantitySpinner: Spinner = view.findViewById(R.id.quantitySpinner)
        val price: TextView = view.findViewById(R.id.totalPrice)
    }
    //оновлення списку товарів
    fun updateItems(newItems: List<ShopItem>) {
        items = newItems
        notifyDataSetChanged()
    }
    //створення моделі
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shop_item, parent, false)
        return ViewHolder(view)
    }
    //заповнення конкретного елементу
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        //встановлення UI
        holder.icon.setImageResource(item.iconResId)
        holder.name.text = item.name
        holder.level.text = "Level: ${item.level}"
        //отримання кількості
        fun getQty(): Int = holder.quantitySpinner.selectedItem?.toString()?.toIntOrNull() ?: 1
        //стан кнопки
        fun updateButtonState() {
            val cost = item.getPriceForNext(getQty())
            val cookies = viewModel.cookieCount.value ?: 0
            holder.price.text = "Cost: $cost"
            holder.buyButton.isEnabled = cookies >= cost
            holder.buyButton.alpha = if (holder.buyButton.isEnabled) 1.0f else 0.3f
        }
        //оновлення ціни
        holder.quantitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                updateButtonState()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        //оновлюємо кількість печива
        val observer = Observer<Int> { updateButtonState() }
        viewModel.cookieCount.observeForever(observer)
        observers.add(observer)
        //обробник кнопки
        holder.buyButton.setOnClickListener {
            val qty = getQty()
            val cost = item.getPriceForNext(qty)
            if (!viewModel.spendCookies(cost)) return@setOnClickListener

            viewModel.upgradeItem(item.id, qty)
        }
        //розрахунок вартості і стану
        updateButtonState()
    }
    //оцінюємо кількість елементів у списку
    override fun getItemCount(): Int = items.size
    //очистка
    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        if (observers.isNotEmpty()) {
            val observer = observers.removeAt(0)
            viewModel.cookieCount.removeObserver(observer)
        }
    }
}
