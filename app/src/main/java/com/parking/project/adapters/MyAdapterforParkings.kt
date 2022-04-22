package com.parking.project.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.AppCompatTextView
import com.google.firebase.auth.FirebaseUser
import com.parking.project.R
import com.parking.project.actiivites.MainActivity
import com.parking.project.models.ParkingModel
import kotlin.collections.ArrayList


class MyAdapterforParkings(var context: Context, var data: ArrayList<ParkingModel>,var user : FirebaseUser) : RecyclerView.Adapter<MyAdapterforParkings.MyViewHolder>() {
    var TAG = "***Adapter"

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var key: AppCompatTextView
        var discription: AppCompatTextView
        var status: AppCompatTextView
        init {
            key = view.findViewById(R.id.key_id)
            discription = view.findViewById(R.id.discription)
            status = view.findViewById(R.id.status)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.parking_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.key.text = context.getString(R.string.parking_number)+position.toString()
        holder.status.text = data[position].parkingStatus
        holder.discription.text = data[position].description

        holder.itemView.setOnClickListener {
            (context as MainActivity).updateParking(data[position])
        }

        holder.itemView.setOnLongClickListener {
            if(!user.uid.equals(context.getString(R.string.admin_id))){
                true
            }else{
                deleteItem(data[position])
                true
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun deleteItem(parkingModel: ParkingModel){
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.delete_confirmation))
            .setMessage(context.getString(R.string.dleteion_dis)) // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(android.R.string.yes) { dialog, which ->

                data.remove(parkingModel)
                notifyDataSetChanged()
                if(data.size == 0)
                    (context as MainActivity).deleteParking(parkingModel)
                dialog.dismiss()
            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setFilter(newList: ArrayList<ParkingModel>) {
        data = ArrayList()
        data.addAll(newList)
        notifyDataSetChanged()
    }


}