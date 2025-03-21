package com.example.introductiontoroom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.introductiontoroom.databinding.SingleItemBinding

class PersonDetailsAdapter(private val listener: PersonDetailsClickListener) : ListAdapter<Person, PersonDetailsAdapter.PersonDetailsViewHolder>(
    DiffUtilCallBack()
){
    inner class PersonDetailsViewHolder(private val binding: SingleItemBinding) : RecyclerView.ViewHolder(binding.root){

        init{
            binding.editBtn.setOnClickListener {
                listener.onEditBtnClicked(getItem(adapterPosition))
            }
            binding.deleteBtn.setOnClickListener {
                listener.onDeleteBtnClicked(getItem(adapterPosition))
            }
        }

        fun bind(person: Person){
            binding.personNameTv.text = person.name
            binding.personAgeTv.text = person.age.toString()
            binding.personCityTv.text = person.city
        }
    }

    class DiffUtilCallBack : DiffUtil.ItemCallback<Person>(){
        override fun areItemsTheSame(
            oldItem: Person,
            newItem: Person
        ) = oldItem.pId == newItem.pId

        override fun areContentsTheSame(
            oldItem: Person,
            newItem: Person
        ) = oldItem == newItem

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PersonDetailsViewHolder {
        return PersonDetailsViewHolder(SingleItemBinding.inflate(LayoutInflater.from(parent.context),parent,false ))
    }

    override fun onBindViewHolder(
        holder: PersonDetailsViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    interface PersonDetailsClickListener{
        fun onEditBtnClicked(person: Person)

        fun onDeleteBtnClicked(person: Person)

    }

}