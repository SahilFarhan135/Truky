package com.example.truky.ui.listactivity.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.networkdomain.model.TruckItemEntity
import com.example.truky.R
import com.example.truky.databinding.IndiviewTruckListBinding
import java.text.ParseException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class TruckAdapter : RecyclerView.Adapter<TruckAdapter.TruckViewHolder>(), Filterable {

    private val items = ArrayList<TruckItemEntity>()
    private val itemsAll = ArrayList<TruckItemEntity>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: ArrayList<TruckItemEntity>) {
        items.clear()
        itemsAll.clear()
        items.addAll(list)
        itemsAll.addAll(list)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun restoreAllList() {
        items.addAll(itemsAll)
        notifyDataSetChanged()
    }

    inner class TruckViewHolder(private val binding: IndiviewTruckListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: TruckItemEntity) {
            with(binding) {
                tvTruckName.text = item.truckNumber
                tvTruckDistance.text = String.format("%.2f", item.lastWaypoint?.speed).trim()
                if (item.lastWaypoint?.speed != null) {
                    tvTruckUnit.text = " k/h "
                }
                val ago: Pair<String, String>? =
                    item.lastWaypoint?.createTime?.let { differenceInTime(it) }
                if (ago != null) {
                    if (ago.second.contains("days")) {
                        labelTruckDays.text = "days ago"
                    } else if (ago.second.contains("hour")) {
                        labelTruckDays.text = "hour ago"
                    } else if (ago.second.contains("min")) {
                        labelTruckDays.text = "min ago"
                    }
                }
                if (item.lastRunningState != null && item.lastRunningState?.truckRunningState != null) {
                    val status: Pair<String, String>? =
                        item.lastRunningState?.stopStartTime?.let { differenceInTime(it) }
                    if (item.lastRunningState?.truckRunningState == 0) {
                        tvTruckStatus.text =
                            itemView.context.getString(R.string.txt_stopped_since) + " ${status?.first} ${status?.second}"
                    } else if (item.lastRunningState?.truckRunningState == 1) {
                        tvTruckStatus.text =
                            itemView.context.getString(R.string.txt_running_since_) + " ${status?.first} ${status?.second}"
                    }
                }
                tvTruckDays.text = ago?.first?.trim()
            }
        }
    }


    fun differenceInTime(longTime: Long): Pair<String, String> {
        var day = 0
        var hh = 0
        var mm = 0
        try {
            val cDate = Date()
            val timeDiff = cDate.time - longTime
            day = TimeUnit.MILLISECONDS.toDays(timeDiff).toInt()
            hh =
                (TimeUnit.MILLISECONDS.toHours(timeDiff) - TimeUnit.DAYS.toHours(day.toLong())).toInt()
            mm = (TimeUnit.MILLISECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(timeDiff)
            )).toInt()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return if (mm <= 60 && hh != 0) {
            if (hh <= 60 && day != 0) {
                Pair("$day", "days")
            } else {
                Pair("$hh", "hour")
            }
        } else {
            Pair("$mm", "min")
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TruckAdapter.TruckViewHolder {
        val binding =
            IndiviewTruckListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TruckViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TruckAdapter.TruckViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
    override fun getFilter(): Filter {
        return filterr
    }

    val filterr = object : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val localList = ArrayList<TruckItemEntity>()

            if (p0 == null || p0.isEmpty()) {
                localList.addAll(itemsAll)
            } else {
                for (item in itemsAll) {
                    if (item.truckNumber != null && item.truckNumber?.lowercase(Locale.ENGLISH) != null) {
                        if (item.truckNumber!!.lowercase(Locale.ENGLISH)
                                .contains(p0.toString().lowercase(Locale.ENGLISH))
                        ) {
                            localList.add(item)
                        }
                    }
                }
            }

            val filterResults = FilterResults()
            filterResults.values = localList
            return filterResults
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            items.clear()
            if (p1 != null) {
                items.addAll(p1.values as Collection<TruckItemEntity>)
            }
            notifyDataSetChanged()
        }

    }
}