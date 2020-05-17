package app.roaim.dtbazar.ui.food

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import app.roaim.dtbazar.databinding.FragmentFoodBinding
import app.roaim.dtbazar.di.Injectable
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.autoCleared
import app.roaim.dtbazar.utils.log
import javax.inject.Inject

class FoodFragment : Fragment(), Injectable, Loggable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val foodViewModel: FoodViewModel by viewModels { viewModelFactory }

    private var foodAdapter by autoCleared<FoodAdapter>()
    private var binding by autoCleared<FragmentFoodBinding>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFoodBinding.inflate(layoutInflater, container, false)
        foodAdapter = FoodAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.retryCallback = foodViewModel
        binding.rvFood.adapter = foodAdapter
        foodViewModel.foodList.observe(viewLifecycleOwner, Observer {
            log("FOOD_LIST: $it")
            binding.result = it
            if (it.status == Status.SUCCESS) {
                foodAdapter.submitList(it.data)
            }
        })
    }
}
