package com.example.worldfactory

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DataAdapter : RecyclerView.Adapter<DataAdapter.DataAdapterViewHolder>() {

    private val adapterData = ArrayList<WordListModel>()

    class DataAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view){

        private fun bindHeader(item : WordListModel.Header){
            itemView.findViewById<TextView>(R.id.text_header).text = item.word
            itemView.findViewById<TextView>(R.id.text_whatis).text = item.phonetic

            if(item.audio == ""){
                itemView.findViewById<ImageButton>(R.id.Audio_play).visibility = View.GONE
            } else {
                itemView.findViewById<ImageButton>(R.id.Audio_play).visibility = View.VISIBLE
            }
        }

        private fun bindPartOfSpeech(item : WordListModel.PartOfSpeech){
            itemView.findViewById<TextView>(R.id.text_whatis_noun).text = item.partOfSpeech
        }

        private fun bindMeaningHeader(item : WordListModel.MeaningHeader){
            //
        }

        private fun bindDefinitionExample(item : WordListModel.DefinitionExample){
            itemView.findViewById<TextView>(R.id.text_meaning).text = item.definition

            if(item.example.length != 1){
                itemView.findViewById<TextView>(R.id.text_example).text = item.example
            } else {
                itemView.findViewById<TextView>(R.id.text_example).visibility = View.GONE
            }
        }

        fun bind(wordModel : WordListModel){
          //  Log.d("AdapterView", "Binded card")
            when (wordModel) {
                is WordListModel.Header -> bindHeader(wordModel)
                is WordListModel.PartOfSpeech -> bindPartOfSpeech(wordModel)
                is WordListModel.MeaningHeader -> bindMeaningHeader(wordModel)
                is WordListModel.DefinitionExample -> bindDefinitionExample(wordModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataAdapterViewHolder {
        //Log.d("AdapterView", "Added card = $viewType")
        val layout = when (viewType) {
            0 -> R.layout.card_item_header
            1 -> R.layout.card_item_pos
            2 -> R.layout.card_item_meanheader
            3 -> R.layout.card_item_mean
            4 -> R.layout.card_item_empty
            else -> throw IllegalArgumentException("Invalid type")
        }

        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)

        return DataAdapterViewHolder(view)
    }

    override fun getItemCount(): Int = adapterData.size

    override fun onBindViewHolder(holder: DataAdapterViewHolder, position: Int) {
        holder.bind(adapterData[position])
    }

    override fun getItemViewType(position: Int): Int {
      //  Log.d("AdapterView", "Typed card = $position")
        return when (adapterData[position]) {
            is WordListModel.Header -> 0
            is WordListModel.PartOfSpeech -> 1
            is WordListModel.MeaningHeader -> 2
            is WordListModel.DefinitionExample -> 3
            else -> 4
        }
    }

    fun setData(data: List<WordListModel>) {
        adapterData.apply {
            clear()
            addAll(data)
        }
        notifyDataSetChanged()
    }
}