package com.example.gf5.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gf5.databinding.FragmentUserDetailsBinding
import com.example.gf5.models.UserDetailsViewModel
import com.example.gf5.viewmodels.UserDetailsViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * A fragment that displays detailed information about a user.
 */
class UserDetailsFragment : Fragment() {

    private var _binding: FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!

    private val userDetailsViewModel: UserDetailsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe user details from ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                userDetailsViewModel.userDetails.collect { user ->
                    binding.userNameTextView.text = user.name
                    binding.userEmailTextView.text = user.email
                    // Populate other UI elements as needed
                }
            }
        }

        // Fetch user details (pass user ID or fetch current user)
        val userId = arguments?.getString("USER_ID") ?: return
        userDetailsViewModel.fetchUserDetails(userId)

        // Handle edit button click
        binding.editUserButton.setOnClickListener {
            // Navigate to EditUserFragment or launch an edit dialog
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
