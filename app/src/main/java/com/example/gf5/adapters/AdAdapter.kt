package com.example.gf5.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gf5.databinding.ItemAdBinding
import com.example.gf5.models.Ad

/**
 * Adapter for displaying a list of advertisements in a RecyclerView.
 *
 * @param onAdClick Lambda function to handle ad item clicks.
 */
class AdAdapter(
    private val onAdClick: (Ad) -> Unit
) : RecyclerView.Adapter<AdAdapter.AdViewHolder>() {

    private var ads: List<Ad> = emptyList()

    /**
     * Updates the adapter's data set with a new list of ads.
     *
     * @param list The new list of ads to display.
     */
    fun submitList(list: List<Ad>) {
        ads = list
        notifyDataSetChanged()
    }

    /**
     * ViewHolder class for ad items.
     *
     * @param binding The binding object for the ad item layout.
     */
    inner class AdViewHolder(private val binding: ItemAdBinding) : RecyclerView.ViewHolder(binding.root) {
        /**
         * Binds an [Ad] object to the UI elements.
         *
         * @param ad The [Ad] object to bind.
         */
        fun bind(ad: Ad) {
            binding.campaignNameTextView.text = ad.campaignName
            binding.descriptionTextView.text = ad.description
            binding.startDateTextView.text = formatTimestamp(ad.startDate)
            binding.endDateTextView.text = formatTimestamp(ad.endDate)
            binding.root.setOnClickListener {
                onAdClick(ad)
            }
        }

        /**
         * Formats a timestamp into a readable date string.
         *
         * @param timestamp The timestamp to format.
         * @return A formatted date string.
         */
        private fun formatTimestamp(timestamp: Long): String {
            // Implement your date formatting logic here.
            // For simplicity, returning the timestamp as a string.
            return timestamp.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdViewHolder {
        val binding = ItemAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdViewHolder, position: Int) {
        holder.bind(ads[position])
    }

    override fun getItemCount(): Int = ads.size
}
