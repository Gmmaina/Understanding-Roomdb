package com.example.introductiontoroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.introductiontoroom.databinding.FragmentAddEditPersonBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AddEditPersonFragment(private val listener: AddEditPersonListener, private val person: Person?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddEditPersonBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddEditPersonBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(person != null){
            setExistingDataOnUi(person)
        }
        attachUiListener()
    }

    private fun setExistingDataOnUi(person: Person) {
        binding.personNameEt.setText(person.name)
        binding.personAgeEt.setText(person.age.toString())
        binding.personCityEt.setText(person.city)
        binding.saveBtn.text = "Update"
    }

    private fun attachUiListener() {
        binding.saveBtn.setOnClickListener {
            val name = binding.personNameEt.text.toString()
            val age = binding.personAgeEt.text.toString()
            val city = binding.personCityEt.text.toString()

            if (name.isNotEmpty() && age.isNotEmpty() && city.isNotEmpty()){
                val person1 = Person(person?.pId?: 0,name,age.toInt(),city)
                listener.onSaveBtnClicked(person != null, person1)
            }
            dismiss()
        }
    }

    companion object{
        const val TAG = "AddEditPersonFragment"
    }

    interface AddEditPersonListener{
        fun  onSaveBtnClicked(isUpdate: Boolean, person: Person)
    }
}