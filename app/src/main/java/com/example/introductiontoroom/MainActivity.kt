package com.example.introductiontoroom

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introductiontoroom.databinding.ActivityMainBinding
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), AddEditPersonFragment.AddEditPersonListener, PersonDetailsAdapter.PersonDetailsClickListener {

    private lateinit var binding: ActivityMainBinding
    private var dao: PersonDao? = null
    private lateinit var adapter: PersonDetailsAdapter

    private lateinit var searchQueryLiveData : MutableLiveData<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVars()
        attachUiListener()
        subscribeDataStreams()
    }

    private fun subscribeDataStreams() {
        searchQueryLiveData.observe(this){ query ->
            lifecycleScope.launch {
                dao?.getSearchedData(query)?.firstOrNull()?.let {
                    adapter.submitList(it)
                }
            }
        }

        lifecycleScope.launch {
            dao?.getAllData()?.collect { mList ->
//                adapter.submitList(mList)

                lifecycleScope.launch {
                    dao?.getSearchedData(searchQueryLiveData.value ?: "")?.firstOrNull()?.let {
                        adapter.submitList(it)
                    }
                }

            }
        }
    }

    private fun initVars() {
        dao = AppDatabase.getDatabase(this).personDao()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PersonDetailsAdapter(this)
        binding.recyclerView.adapter = adapter
        searchQueryLiveData = MutableLiveData()
    }

    private fun attachUiListener() {
        binding.floatingActionButton.setOnClickListener {
            showBottomSheet()
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText != null) {
                    onQueryChanged(newText)
                    return true
                }
                return false
            }
        })
    }

    private fun onQueryChanged(query: String) {
        searchQueryLiveData.postValue(query)
//        lifecycleScope.launch {
//           dao?.getSearchedData(query)?.firstOrNull()?.let {
//                adapter.submitList(it)
//            }
//        }
    }


    private fun showBottomSheet(person: Person? = null){
        val bottomSheet = AddEditPersonFragment.newInstance(person)
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
        builder.setTitle("Delete Warning!")
        // Set cancelable false for when the user clicks
        // on the outside the Dialog Box then it will remain shown
        builder.setCancelable(false)

        // Set the Positive button with delete name lambda
        // deletes the selected data when clicked
        builder.setPositiveButton("DELETE") {
            dialog, which ->
            lifecycleScope.launch(Dispatchers.IO) {
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