package app.roaim.dtbazar.ui.food

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import app.roaim.dtbazar.databinding.FragmentFoodBinding
import app.roaim.dtbazar.databinding.ViewAddNewFoodBinding
import app.roaim.dtbazar.di.Injectable
import app.roaim.dtbazar.model.Food
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.ui.ListItemClickListener
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.autoCleared
import app.roaim.dtbazar.utils.log
import app.roaim.dtbazar.utils.snackbar
import javax.inject.Inject

class FoodFragment : Fragment(), Injectable, Loggable, ListItemClickListener<Food> {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val foodViewModel: FoodViewModel by viewModels { viewModelFactory }

    private var foodAdapter by autoCleared<FoodAdapter>()
    private var binding by autoCleared<FragmentFoodBinding>()
    var addFoodBinding by autoCleared<ViewAddNewFoodBinding>()
    var addFoodDialog by autoCleared<AlertDialog>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFoodBinding.inflate(layoutInflater, container, false)
        foodAdapter = FoodAdapter()
        foodAdapter.setItemClickListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonAddFood.setOnClickListener {
            addFoodDialog.show()
        }
        initAddFoodDialog()
        binding.retryCallback = foodViewModel
        binding.rvFood.adapter = foodAdapter
        foodViewModel.foodList.observe(viewLifecycleOwner, Observer {
            log("FOOD_LIST: $it")
            binding.result = it
        })
        foodViewModel.cachedFoods.observe(
            viewLifecycleOwner,
            Observer { foodAdapter.submitList(it.data) }
        )
    }

    override fun onItemClick(food: Food?, itemView: View, isLongClick: Boolean) {
        if (isLongClick && food != null) {
            itemView.snackbar("Delete: ${food.name}?") {
                foodViewModel.deleteFood(food).observe(viewLifecycleOwner, Observer {
                    log("DELETE_FOOD: $it")
                    if (it.status == Status.FAILED) itemView.snackbar(
                        "Failed to delete, ${food.name}. ${it.msg}",
                        "DISMISS"
                    )
                })
            }
        }
    }
}
