package tlambert.examen

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import tlambert.examen.model.Company
import tlambert.examen.tier.CompanyDB
import tlambert.examen.tier.CompanyService

class MainActivity : AppCompatActivity() {

    inner class QueryCompany(): AsyncTask<Void, Void, Boolean>(){

        var ListCompany = emptyList<Company>()

        override fun doInBackground(vararg params: Void?): Boolean {

            var companyService = CompanyService()
            ListCompany = companyService.getCompany(SearchBar.text.toString())
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            if((result != null) && result) {
                SearchListView.adapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    ListCompany
                )
                println("""Company3 ${ListCompany}""")
            }
        }

    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //BUTTON SEARCH CLICK
        SearchButton.setOnClickListener {
            //SHOW LIST VIEW
            QueryCompany().execute()


        }



    }


}