package com.example.introductiontoroom

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introductiontoroom.databinding.ActivityMainBinding
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), AddEditPersonFragment.AddEditPersonListener, PersonDetailsAdapter.PersonDetailsClickListener {

    private lateinit var binding: ActivityMainBinding
    private var dao: PersonDao? = null
    private lateinit var adapter: PersonDetailsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVars()
        attachUiListener()
        subscribeDataStreams()
    }

    private fun subscribeDataStreams() {
        lifecycleScope.launch {
            dao?.getAllData()?.collect { mList ->
                adapter.submitList(mList)

            }
        }
    }

    private fun initVars() {
        dao = AppDatabase.getDatabase(this).personDao()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PersonDetailsAdapter(this)
        binding.recyclerView.adapter = adapter
    }

    private fun attachUiListener() {
        binding.floatingActionButton.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun showBottomSheet(person: Person? = null){
        val bottomSheet = AddEditPersonFragment(this, person)
        bottomSheet.show(supportFragmentManager, AddEditPersonFragment.TAG)
    }

    override fun onSaveBtnClicked(isUpdate: Boolean, person: Person) {

        lifecycleScope.launch(Dispatchers.IO) {
            dao?.savePerson(person)
        }

    }

    override fun onEditBtnClicked(person: Person) {
        showBottomSheet(person)
    }

    override fun onDeleteBtnClicked(person: Person) {
        deletePersonConfirmation(person)
//        lifecycleScope.launch(Dispatchers.IO) {
//            // dao?.deletePerson(person)
//            dao?.deletePersonById(person.pId)
//        }
    }

    private fun deletePersonConfirmation(person: Person) {
        val builder = AlertDialog.Builder(this)

        // Set the message to show for the Alert time
        builder.setMessage("Are you sure yo want to delete!")

        // Set Alert title
        builder.setTitle("Alert!")
        // Set cancelable false for when the user clicks
        // on the outside the Dialog Box then it will remain shown
        builder.setCancelable(false)

        // Set the Positive button with delete name lambda
        // deletes the selected data when clicked
        builder.setPositiveButton("DELETE") {
            dialog, which -> lifecycleScope.launch(Dispatchers.IO) {
                // dao?.deletePerson(person)
                dao?.deletePersonById(person.pId)
            }
            Toasty.success(this, "Person Deleted Successfully", Toasty.LENGTH_SHORT, true).show()
        }

        // Dialog is cancelled when the button is clicked
        builder.setNegativeButton("CANCEL") {
            dialog, which -> dialog.cancel()
        }

        // Create the Alert Dialog
        val alertDialog = builder.create()
        // Show the Alert Dialog box
        alertDialog.show()
    }


}