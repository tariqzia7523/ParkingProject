package com.parking.project.actiivites

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.parking.project.R
import com.parking.project.adapters.MyAdapterforParkings
import com.parking.project.databinding.ActivityAddParkingSlotsBinding
import com.parking.project.models.ParkingModel


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddParkingSlotsBinding
    lateinit var myRef : DatabaseReference
    var list = ArrayList<ParkingModel>()
    val TAG = "***ParkingSLot"
    lateinit var progressDialog: ProgressDialog
    lateinit var adapter : MyAdapterforParkings
    var user : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddParkingSlotsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.please_wait))

        user = FirebaseAuth.getInstance().currentUser

        binding.allParkingList.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapterforParkings(this,list,user!!)
        binding.allParkingList.adapter = adapter

        myRef = FirebaseDatabase.getInstance().getReference("PARKINGS")
        progressDialog.show()
        getAllData()

        binding.addParkingSlots.setOnClickListener {
            updateParking(null)
        }

        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@MainActivity,LoginActivity::class.java))
            finish()
        }

        if(!user!!.uid.equals(getString(R.string.admin_id)))
            binding.addParkingSlots.visibility = View.GONE

    }



    private fun getAllData() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    var avalibelParking = 0
                    Log.e(TAG, "user data changed")
                    Log.e(TAG, "user data changed " + dataSnapshot.key)
                    list.clear()
                    for (dataSnapshot1 in dataSnapshot.children) {
                        val parkingModel: ParkingModel? = dataSnapshot1.getValue(ParkingModel::class.java)
                        parkingModel!!.id = dataSnapshot1.key
                        if(parkingModel.parkingStatus.contains(getString(R.string.avalibel),true))
                            avalibelParking += 1
                        list.add(parkingModel)
                    }
                    adapter.notifyDataSetChanged()
                    progressDialog.dismiss()
                    binding.totalParkings.text = getString(R.string.total_parkings)+ list.size.toString()
                    binding.avalibleParkings.text = getString(R.string.avalibe_parking)+ avalibelParking.toString()
                } catch (c: Exception) {
                    c.printStackTrace()
                    Log.e(TAG, "exception while retriving data from users")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "exception")
                Log.e(TAG, databaseError.message)
                databaseError.toException().printStackTrace()
                progressDialog.dismiss()
            }
        })
    }

    fun updateParking(parkingModel: ParkingModel?) {
        if(!user!!.uid.equals(getString(R.string.admin_id))){
            return
        }
        val dialog = Dialog(this@MainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.add_parking_pop_up)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val discription: AppCompatEditText = dialog.findViewById(R.id.discription)
        val status: Spinner = dialog.findViewById(R.id.status)
        val add_parking_slots: AppCompatTextView = dialog.findViewById(R.id.add_parking_slots)

        if(parkingModel != null){
            discription.setText(parkingModel.description)
            if(parkingModel.parkingStatus.contains(getString(R.string.avalibel),false))
                status.setSelection(1)
            else
                status.setSelection(2)
            add_parking_slots.text = getString(R.string.update_parking)
        }


        add_parking_slots.setOnClickListener {
            if(discription.text.toString().equals("") || status.selectedItemPosition == 0 ){
                Toast.makeText(this@MainActivity, getString(R.string.add_all_data), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(parkingModel == null){
                val parkingModel = ParkingModel()
                parkingModel.parkingStatus = status.selectedItem.toString()
                parkingModel.description = discription.text.toString()
                addParking(parkingModel)
            }else{
                parkingModel.description = discription.text.toString()
                parkingModel.parkingStatus = status.selectedItem.toString()
                updateData(parkingModel)
            }
            dialog.dismiss()
        }

        dialog.show()
        val window: Window = dialog.getWindow()!!
        window.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

    }

    fun deleteParking(parkingModel: ParkingModel) {
        if(!user!!.uid.equals(getString(R.string.admin_id))){
            return
        }
        progressDialog.show()
        myRef.child(parkingModel.id!!).removeValue()
    }

    fun updateData(parkingModel: ParkingModel) {
        if(!user!!.uid.equals(getString(R.string.admin_id))){
            return
        }
        progressDialog.show()
        val update = HashMap<String , Any>()
        update.put("parkingStatus",parkingModel.parkingStatus)
        update.put("description",parkingModel.description)

        myRef.child(parkingModel.id!!).updateChildren(update)
    }
    fun addParking(parkingModel: ParkingModel) {
        if(!user!!.uid.equals(getString(R.string.admin_id))){
            return
        }
        progressDialog.show()
        myRef.push().setValue(parkingModel)
    }
}