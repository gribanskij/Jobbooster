package com.gribansky.jobbooster.ui.home


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.gribansky.jobbooster.R
import com.gribansky.jobbooster.databinding.FragmentHomeBinding
import com.gribansky.jobbooster.datastore.PrefManager
import com.gribansky.jobbooster.work.PublishWork
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment(R.layout.fragment_home) {


    private val wTag = "publishing"

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val manager by lazy { PrefManager(requireContext()) }

    private val model by viewModel<HomeViewModel>()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        binding.textSuccess.text = manager.boostResult
        binding.textError.text = manager.errorDesc

        binding.stop.setOnClickListener {
            stopPublish()
        }

        binding.start.setOnClickListener {
            startPublish()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun startPublish(){
        val publishWork = PeriodicWorkRequestBuilder<PublishWork>(4, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(wTag,
            ExistingPeriodicWorkPolicy.KEEP,publishWork)

    }

    private fun stopPublish(){
        WorkManager.getInstance(requireContext()).cancelUniqueWork(wTag)
    }
}