package tlambert.examen

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import tlambert.examen.model.Company
import tlambert.examen.model.Search
import tlambert.examen.tier.CompanyDAO
import tlambert.examen.tier.CompanyDB
import tlambert.examen.tier.CompanyService
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    inner class QueryCompany(): AsyncTask<Void, Void, Boolean>(){

        var ListCompany = emptyList<Company>()

        override fun onPreExecute() {
            SearchListView.adapter = null;
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Void?): Boolean {

            val db = CompanyDB.getDatabase(this@MainActivity)
            val companyDAO = db.companyDAO()
            val searchDAO= db.searchDAO()
            val linkDAO = db.linkDAO()
            val dateNow = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val text = SearchBar.text.toString()


            if(Departement.text.toString().isNotBlank() || codePostal.text.toString().isNotBlank()){
                if(Departement.text.toString().isNotBlank()){
                    println("""testdep ${searchDAO.counteDEP(dateNow, text, codePostal.text.toString())}""")
                    ListCompany = if(searchDAO.counteDEP(dateNow, text, Departement.text.toString()) != 0){
                        println("ok3")
                        companyDAO.getDEP(dateNow, text, Departement.text.toString())
                    }else {
                        println("ok4")
                        val companyService = CompanyService(companyDAO, searchDAO, linkDAO, SearchBar.text.toString())
                        companyService.getCompany(text, Departement.text.toString(),"")
                    }

                }

                if(codePostal.text.toString().isNotBlank()){
                    println("""test ${searchDAO.counteCP(dateNow, text, codePostal.text.toString())}""")
                    ListCompany = if(searchDAO.counteCP(dateNow, text, codePostal.text.toString()) != 0){
                        println("ok")
                        companyDAO.getCP(dateNow, text, codePostal.text.toString())
                    }else {
                        println("ok2")
                        val companyService = CompanyService(companyDAO, searchDAO, linkDAO, SearchBar.text.toString())
                        companyService.getCompany(text, "", codePostal.text.toString())
                    }

                }
            }
            else{
                ListCompany = if(searchDAO.countee(dateNow, text) != 0){
                    companyDAO.getCompanyDate(dateNow, text)
                }else {
                    val companyService = CompanyService(companyDAO, searchDAO, linkDAO, SearchBar.text.toString())
                    companyService.getCompany(text,"","")
                }
            }

            println("""List ${searchDAO.getSearch()}""")

            return true

        }

        override fun onPostExecute(result: Boolean?) {
            if((result != null) && result) {

                SearchListView.adapter = ArrayAdapter(
                        this@MainActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        ListCompany
                )

                if(codePostal.text.toString().isNotBlank()){
                    TV_Titre.text = "Recherche de l'entreprise '${SearchBar.text} (${codePostal.text})'"
                }
                if(Departement.text.toString().isNotBlank()){
                    TV_Titre.text = "Recherche de l'entreprise '${SearchBar.text} (${Departement.text})'"
                }
                if(codePostal.text.toString().isBlank() && Departement.text.toString().isBlank()){
                    TV_Titre.text = "Recherche de l'entreprise '${SearchBar.text}'"
                }
                SearchBar.setText("")
                codePostal.setText("")
                Departement.setText("")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //BUTTON SEARCH CLICK
        SearchButton.setOnClickListener {
            if(SearchBar.text.toString().isNotBlank()){
                QueryCompany().execute()

            }

        }


        SearchAdvanced.setOnClickListener{
            codePostal.visibility=View.VISIBLE
            Departement.visibility=View.VISIBLE
            SearchAdvancedUp.visibility=View.VISIBLE
            SearchAdvanced.visibility=View.GONE
        }

        SearchAdvancedUp.setOnClickListener{
            codePostal.visibility= View.GONE
            Departement.visibility=View.GONE
            SearchAdvancedUp.visibility=View.GONE
            SearchAdvanced.visibility=View.VISIBLE
        }

        codePostal.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Departement.isEnabled = s.isBlank()
            }
        })

        Departement.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                codePostal.isEnabled = s.isBlank()
            }
        })

        val db = CompanyDB.getDatabase(this@MainActivity)
        val searchDAO = db.searchDAO()
        val companyDAO = db.companyDAO()
        val linkDAO = db.linkDAO()

        History.setOnClickListener {

            SearchListView.adapter = null
            SearchListView.adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_dropdown_item,
                searchDAO.getSearch()
            )
            TV_Titre.text = "Historique"
        }

        SearchListView.setOnItemClickListener {
            _,_,position,_ ->  SearchListView.adapter.getItem(position)

            if(SearchListView.adapter.getItem(position) is Company){

                var intent = Intent(this,CompanyActivity::class.java)

                intent.putExtra("company",SearchListView.adapter.getItem(position) as Company)

                startActivity(intent)

            }else{
                val search = SearchListView.adapter.getItem(position) as Search

                SearchListView.adapter = null

                SearchListView.adapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    searchDAO.getSearchID(search.id!!)
                )

                if(search.cp != ""){
                    TV_Titre.text = "Recherche de l'entreprise '${search.text} (${search.cp})'"
                }
                if(search.dep != ""){
                    TV_Titre.text = "Recherche de l'entreprise '${search.text} (${search.dep})'"
                }
                if(search.dep == "" && search.cp == ""){
                    TV_Titre.text = "Recherche de l'entreprise '${search.text}'"
                }




            }

        }

}
}