package com.example.truky.ui.listactivity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.networkdomain.model.TruckItemEntity
import com.example.truky.R
import com.example.truky.base.BaseActivity
import com.example.truky.databinding.ActivityListBinding
import com.example.truky.ui.listactivity.adapter.TruckAdapter
import com.example.truky.ui.map.MapActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListActivity : BaseActivity<ActivityListBinding>() {

    override fun layoutId(): Int = R.layout.activity_list
    val truckList = ArrayList<TruckItemEntity>()


    private val _viewModel: ListViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUi()
        addListener()
        addObservers()
    }

    private fun addListener() {
        binding.appbar.imgMap.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelableArrayList(MapActivity.KEY_LAT_LNG, truckList)
            }
            navigator.startActivityWithData(MapActivity::class.java, bundle)
        }
        binding.appbar.imgSearch.setOnClickListener {
            binding.appbar.clNormalLayout.visibility = View.GONE
            binding.appbar.clSearch.visibility = View.VISIBLE
        }
        binding.appbar.imgNormal.setOnClickListener {
            binding.appbar.clNormalLayout.visibility = View.VISIBLE
            binding.appbar.clSearch.visibility = View.GONE
            (binding.rvTrucks.adapter as TruckAdapter).restoreAllList()

        }
        binding.appbar.imgCut.setOnClickListener {
            if(binding.appbar.etSearch.text.isNullOrBlank()){
                binding.appbar.clNormalLayout.visibility = View.VISIBLE
                binding.appbar.clSearch.visibility = View.GONE
                (binding.rvTrucks.adapter as TruckAdapter).restoreAllList()
                return@setOnClickListener
            }
            binding.appbar.etSearch.text?.clear()
        }
        binding.appbar.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                (binding.rvTrucks.adapter as TruckAdapter).filter.filter(p0.toString());
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

    }


    private fun initUi() {
        with(binding) {
            appbar.tvTitle.text = getString(R.string.txt_app_namee)
            _viewModel.getTruckList()
            rvTrucks.adapter = TruckAdapter()
            rvTrucks.layoutManager = LinearLayoutManager(this@ListActivity)
        }
    }


    override fun addObservers() {
        observeTruckList()
        observeViewState()
        observeError()
    }

    private fun observeViewState() {
        _viewModel.viewState.observe(this, {
            when (it) {
                com.example.truky.base.ViewState.Loading -> {
                    uiUtil.showProgress()
                }
                com.example.truky.base.ViewState.Success() -> {
                    uiUtil.hideProgress()
                }
                else -> {}
            }
        })
    }

    private fun observeError() {
        _viewModel.setError.observe(this, {
            showToast(it.toString())
        })
    }

    private fun observeTruckList() {
        _viewModel.setTruckList.observe(this, {
            if (it?.data == null || it.responseCode == null || it.responseCode?.responseCode == null || it.responseCode!!.responseCode!! == 0) {
                uiUtil.showToast("Something went wrong")
                return@observe
            }
            truckList.clear()
            for (item in it.data!!) {
                truckList.add(item!!.toTruckItemEntity())
            }
            (binding.rvTrucks.adapter as TruckAdapter).submitList(list = truckList)
        })
    }
}





