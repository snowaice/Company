package tlambert.examen

import android.os.AsyncTask
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import tlambert.examen.model.Company
import tlambert.examen.tier.CompanyDB
import tlambert.examen.tier.CompanyService
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    inner class QueryCompany(): AsyncTask<Void, Void, Boolean>(){

        var ListCompany = emptyList<Company>()

        override fun onPreExecute() {
            SearchListView.setAdapter(null);
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Void?): Boolean {

            val db = CompanyDB.getDatabase(this@MainActivity)
            val companyDAO = db.companyDAO()
            val searchDAO= db.searchDAO()
            val linkDAO = db.linkDAO()
            val DateNow = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val text = SearchBar.text.toString()


            if(Departement.text.toString().isNotBlank() || codePostal.text.toString().isNotBlank()){
                if(Departement.text.toString().isNotBlank()){

                    ListCompany = if(searchDAO.counteDEP(DateNow,text,codePostal.text.toString()) != 0){
                        companyDAO.getDEP(DateNow,text,codePostal.text.toString())
                    }else {
                        val companyService = CompanyService(companyDAO,searchDAO,linkDAO,SearchBar.text.toString())
                        companyService.getCompany(text,Departement.text.toString())
                    }

                }

                if(codePostal.text.toString().isNotBlank()){

                    ListCompany = if(searchDAO.counteCP(DateNow,text,codePostal.text.toString()) != 0){
                        println("ok")
                        companyDAO.getCP(DateNow,text,codePostal.text.toString())
                    }else {
                        println("ok2")
                        val companyService = CompanyService(companyDAO, searchDAO, linkDAO, SearchBar.text.toString())
                        companyService.getCompany(text, "", codePostal.text.toString())
                    }

                }
            }
            else{
                ListCompany = if(searchDAO.countee(DateNow,text) != 0){
                    companyDAO.getEntrepriseByRecherche(DateNow,text)
                }else {
                    val companyService = CompanyService(companyDAO, searchDAO, linkDAO, SearchBar.text.toString())
                    companyService.getCompany(text)
                }
            }


            return true

        }

        override fun onPostExecute(result: Boolean?) {
            if((result != null) && result) {

                SearchListView.adapter = ArrayAdapter(
                        this@MainActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        ListCompany
                )


            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //BUTTON SEARCH CLICK
        SearchButton.setOnClickListener {
            QueryCompany().execute()
        }

    }
}