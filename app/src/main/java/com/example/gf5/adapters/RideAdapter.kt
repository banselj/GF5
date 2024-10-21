package com.example.gf5.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gf5.databinding.ItemRideBinding
import com.example.gf5.models.Ride

class RideAdapter(
    private val onRideClick: (Ride) -> Unit
) : RecyclerView.Adapter<RideAdapter.RideViewHolder>() {

    private var rides: List<Ride> = emptyList()

    fun submitList(list: List<Ride>) {
        rides = list
        notifyDataSetChanged()
    }


    inner class RideViewHolder(private val binding: ItemRideBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ride: Ride) {
            binding.rideIdTextView.text = ride.id
            binding.pickupTextView.text = ride.pickupLocation
            binding.dropoffTextView.text = ride.dropoffLocation
            binding.statusTextView.text = ride.status
            binding.root.setOnClickListener {
                onRideClick(ride)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideViewHolder {
        val binding = ItemRideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RideViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RideViewHolder, position: Int) {
        holder.bind(rides[position])
    }

    override fun getItemCount(): Int = rides.size
}
