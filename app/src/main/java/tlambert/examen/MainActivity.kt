package tlambert.examen


import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import tlambert.examen.model.Company
import tlambert.examen.model.Search
import tlambert.examen.model.code_NAF_APE
import tlambert.examen.tier.CodeNafDatabase
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
            var naf: code_NAF_APE? = null

            if(spNAF.adapter != null){
                 naf = spNAF.selectedItem as code_NAF_APE
            }

            //Va chercher en BDD si on a déja le résultat ou fait appel à L'API

            if(naf != null){
                if(naf.CodeNAFAPE.toString().isNotBlank()){
                    ListCompany = if(searchDAO.countNAF(dateNow, text, codeNAFtext.text.toString()) != 0){
                        companyDAO.getNAF(dateNow, text, naf.CodeNAFAPE.toString())
                    }else {
                        val companyService = CompanyService(companyDAO, searchDAO, linkDAO, SearchBar.text.toString())
                        companyService.getCompany(text, "", "", naf.CodeNAFAPE.toString())
                    }

                }
            }

            if(Departement.text.toString().isNotBlank()) {
                if (Departement.text.length == 2){
                    ListCompany = if (searchDAO.counteDEP(dateNow, text, Departement.text.toString()) != 0) {
                        companyDAO.getDEP(dateNow, text, Departement.text.toString())
                    } else {
                        val companyService = CompanyService(companyDAO, searchDAO, linkDAO, SearchBar.text.toString())
                        companyService.getCompany(text, Departement.text.toString(), "", "")
                    }
                }else{
                    return false
                }

            }

            if(codePostal.text.toString().isNotBlank()){
                if(Departement.text.length == 5){
                    ListCompany = if(searchDAO.counteCP(dateNow, text, codePostal.text.toString()) != 0){
                        companyDAO.getCP(dateNow, text, codePostal.text.toString())
                    }else {
                        val companyService = CompanyService(companyDAO, searchDAO, linkDAO, SearchBar.text.toString())
                        companyService.getCompany(text, "", codePostal.text.toString(), "")
                    }
                }
                else{
                    return false
                }


            }


            if(Departement.text.toString().isBlank() && codePostal.text.toString().isBlank() && codeNAFtext.text.toString().isBlank() ){

                ListCompany = if(searchDAO.countee(dateNow, text) != 0){
                        companyDAO.getCompanyDate(dateNow, text)
                    }else {
                        val companyService = CompanyService(companyDAO, searchDAO, linkDAO, SearchBar.text.toString())

                        companyService.getCompany(text, "", "", "")
                    }
            }

            println("""List ${ListCompany}""")


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

                //Sauvegarde des champs sur la recherche
                val prefs = getPreferences(MODE_PRIVATE)
                val editor = prefs.edit()

                editor.putString("Company", SearchBar.text.toString())
                editor.putString("CP", codePostal.text.toString())
                editor.putString("DEP", Departement.text.toString())
                editor.putString("NAF", codeNAFtext.text.toString())

                editor.commit()

                TV_Titre.visibility =  View.VISIBLE
            }
            else{
                Toast.makeText(this@MainActivity, "Une erreur est survenu", Toast.LENGTH_LONG).show()
            }
        }
    }

    //Sauvegarde Bundle
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putString("libelle", SearchBar.text.toString())
        savedInstanceState.putString("cp", codePostal.text.toString())
        savedInstanceState.putString("dep", Departement.text.toString())
        savedInstanceState.putString("naf", codeNAFtext.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val libelle = savedInstanceState.getString("libelle")
        val cp = savedInstanceState.getString("cp")
        val dep = savedInstanceState.getString("dep")
        val naf = savedInstanceState.getString("naf")

        SearchBar.setText(libelle)
        codePostal.setText(cp)
        Departement.setText(dep)
        codeNAFtext.setText(naf)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = CompanyDB.getDatabase(this@MainActivity)
        val searchDAO = db.searchDAO()

        val dbNAF = CodeNafDatabase.getDatabase(this@MainActivity)
        val codeNafDAO = dbNAF.CodeNafDAO()

        //Affichage des anciennes valeurs
        val prefs = getPreferences(MODE_PRIVATE)

        val textSearch = prefs.getString("Company", "")
        val CPSearch = prefs.getString("CP", "")
        val DEPSearch = prefs.getString("DEP", "")
        val NAFSearch = prefs.getString("NAF", "")

        SearchBar.setText(textSearch)
        codePostal.setText(CPSearch)
        Departement.setText(DEPSearch)
        codeNAFtext.setText(NAFSearch)


        //Clique bouton recherche
        SearchButton.setOnClickListener {
            if(SearchBar.text.toString().isNotBlank()){

                    QueryCompany().execute()
            }
        }


        //Disabled les champs en fonction de ce qui est rempli (on ne peut pas saisir le departement si il y a le code postal)
        if(codePostal.text.toString() != ""){
            Departement.isEnabled = false
            codeNAFtext.isEnabled = false
        }

        if(Departement.text.toString() != ""){
            codePostal.isEnabled = false
            codeNAFtext.isEnabled = false
        }

        if(codeNAFtext.text.toString() != ""){
            codePostal.isEnabled = false
            Departement.isEnabled = false

            val text = "%${codeNAFtext.text}%"
            val listNAF = codeNafDAO.getNaf(text)

            val adapter = ArrayAdapter(this@MainActivity,
                    android.R.layout.simple_expandable_list_item_1,
                    listNAF)
            spNAF.adapter = adapter

        }

        if(TV_Titre.text.isBlank()){
            TV_Titre.visibility =  View.GONE
        }

        //Recherche avancé
        SearchAdvanced.setOnClickListener{
            codePostal.visibility=View.VISIBLE
            Departement.visibility=View.VISIBLE
            codeNAFtext.visibility = View.VISIBLE
            SearchAdvancedUp.visibility=View.VISIBLE
            spNAF.visibility=View.VISIBLE
            SearchAdvanced.visibility=View.GONE
        }

        SearchAdvancedUp.setOnClickListener{
            codePostal.visibility= View.GONE
            Departement.visibility=View.GONE
            SearchAdvancedUp.visibility=View.GONE
            codeNAFtext.visibility = View.GONE
            spNAF.visibility=View.GONE
            SearchAdvanced.visibility=View.VISIBLE
        }


        codePostal.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Departement.isEnabled = s.isBlank()
                codeNAFtext.isEnabled = s.isBlank()
            }
        })

        Departement.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                codePostal.isEnabled = s.isBlank()
                codeNAFtext.isEnabled = s.isBlank()
            }
        })

        codeNAFtext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Departement.isEnabled = s.isBlank()
                codePostal.isEnabled = s.isBlank()
                if (s.isBlank()) {
                    spNAF.adapter = null
                } else {
                    val text = "%${s}%"
                    val listNAF = codeNafDAO.getNaf(text)

                    val adapter = ArrayAdapter(this@MainActivity,
                            android.R.layout.simple_expandable_list_item_1,
                            listNAF)
                    spNAF.adapter = adapter
                }
            }
        })


        //Suppression de 3 mois
        val list =  searchDAO.getSearch()
        for(search in list) {

            val sdf = SimpleDateFormat("yyyy/MM/dd")
            val c = Calendar.getInstance()
            c.add(Calendar.MONTH, -3)
            val datePasse = c.time
            val datePast = sdf.format(datePasse)
            val date3Month= sdf.parse(datePast)


            val date = SimpleDateFormat("yyyy-MM-dd").parse(search.date!!)
            val dateTime = date!!.time
            val datedSDF = sdf.format(dateTime)
            val dateFromSearch = sdf.parse(datedSDF)

            if( date3Month!! > dateFromSearch){
                searchDAO.delete(search)
            }
        }

        //Affichage de l'historique
        History.setOnClickListener {

            SearchListView.adapter = null
            SearchListView.adapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    searchDAO.getSearch()
            )
            TV_Titre.text = "Historique"
            TV_Titre.visibility = View.VISIBLE
        }


        //Affichage de la listView si c'est une entreprise ou un recherche
        SearchListView.setOnItemClickListener { _, _, position, _ ->  SearchListView.adapter.getItem(position)

            if(SearchListView.adapter.getItem(position) is Company){

                var intent = Intent(this, CompanyActivity::class.java)

                intent.putExtra("company", SearchListView.adapter.getItem(position) as Company)

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
                if(search.codeNAF != ""){
                    TV_Titre.text = "Recherche de l'entreprise '${search.text} (${search.codeNAF})'"
                }
                if(search.dep == "" && search.cp == "" && search.codeNAF == ""){
                    TV_Titre.text = "Recherche de l'entreprise '${search.text}'"
                }

            }

        }


}
}