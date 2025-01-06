package com.example.gf5.databinding

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.gf5.R

class ActivityAdminPanelBinding private constructor(
    val root: View,
    val logoutButton: Button,
    val refreshButton: Button,
    val userEmptyView: TextView,
    val rideEmptyView: TextView,
    val adEmptyView: TextView,
    val usersRecyclerView: RecyclerView,
    val ridesRecyclerView: RecyclerView,
    val adsRecyclerView: RecyclerView
) : ViewBinding {
    override fun getRoot(): View = root

    companion object {
        fun inflate(layoutInflater: android.view.LayoutInflater): ActivityAdminPanelBinding {
            val root = layoutInflater.inflate(R.layout.activity_admin_panel, null)
            return ActivityAdminPanelBinding(
                root,
                root.findViewById(R.id.logoutButton),
                root.findViewById(R.id.refreshButton),
                root.findViewById(R.id.userEmptyView),
                root.findViewById(R.id.rideEmptyView),
                root.findViewById(R.id.adEmptyView),
                root.findViewById(R.id.usersRecyclerView),
                root.findViewById(R.id.ridesRecyclerView),
                root.findViewById(R.id.adsRecyclerView)
            )
        }
    }
}