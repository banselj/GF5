package com.example.gf5.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gf5.databinding.FragmentAnotherBinding

/**
 * A simple [Fragment] subclass representing Another Fragment.
 */
class AnotherFragment : Fragment() {

    // Binding object instance corresponding to the fragment_another.xml layout
    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentAnotherBinding? = null
    private val binding get() = _binding!!


    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return Return the View for the fragment's UI.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using view binding
        _binding = FragmentAnotherBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called immediately after onCreateView().
     *
     * @param view The View returned by onCreateView().
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up click listener for the action button
        binding.buttonAction.setOnClickListener {
            performAction()
        }
    }

    /**
     * Performs the action when the button is clicked.
     * Currently, it displays a Toast message. You can customize this to perform any desired action.
     */
    private fun performAction() {
        Toast.makeText(requireContext(), "Action performed!", Toast.LENGTH_SHORT).show()
    }

    /**
     * Called when the view previously created by onCreateView() has been detached from the fragment.
     * Cleans up the binding to prevent memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
