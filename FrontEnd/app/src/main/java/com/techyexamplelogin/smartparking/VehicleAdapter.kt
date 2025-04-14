package com.techyexamplelogin.smartparking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class VehicleAdapter(private val vehicleList: List<Vehicle>,
                     private val onItemClick: (Vehicle) -> Unit) :
    RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val companyName: TextView = itemView.findViewById(R.id.companyName)
        val model: TextView = itemView.findViewById(R.id.model)
        val vehicleNumber: TextView = itemView.findViewById(R.id.vehicleNumber)
        val cardImage: ImageView =itemView.findViewById(R.id.cardimage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vehicle, parent, false)

        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val vehicle = vehicleList[position]
        holder.companyName.text = vehicle.companyName
        holder.model.text = vehicle.model
        holder.vehicleNumber.text = vehicle.vehicleNumber
        if(vehicle.type=="car"){
            holder.cardImage.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.ic_car
                )
            )
        }
        else if(vehicle.type=="bike"){
            holder.cardImage.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.ic_bike
                )
            )
        }
        else if(vehicle.type=="jeep"){
            holder.cardImage.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.ic_bus
                )
            )
        }
        holder.itemView.setOnClickListener {
            onItemClick(vehicle)
        }
    }

    override fun getItemCount(): Int = vehicleList.size
}
