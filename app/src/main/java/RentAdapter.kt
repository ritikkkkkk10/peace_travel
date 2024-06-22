import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ritikprajapati.peacetravel.Rent
import com.ritikprajapati.peacetravel.R

class RentAdapter(private var rentList: List<Rent>) : RecyclerView.Adapter<RentAdapter.RentViewHolder>() {

    class RentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.textViewName)
        val contact: TextView = itemView.findViewById(R.id.textViewPhone)
        val hall: TextView = itemView.findViewById(R.id.textViewHall)
        val giveTake: TextView = itemView.findViewById(R.id.giveOrTake)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_rent, parent, false)
        return RentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RentViewHolder, position: Int) {
        val currentRent = rentList[position]
        holder.userName.text = currentRent.name
        holder.contact.text = currentRent.phone
        holder.hall.text = currentRent.hall
        holder.giveTake.text = currentRent.giveTake
    }

    override fun getItemCount() = rentList.size
    fun updateList(newList: List<Rent>) {
        rentList = newList
        notifyDataSetChanged()
    }
}
