package tlambert.examen.tier

import android.util.JsonReader
import tlambert.examen.model.Company
import tlambert.examen.model.Link
import tlambert.examen.model.Search
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class CompanyService(companyDAO:CompanyDAO,searchDAO:SearchDAO,linkDAO:LinkDAO,text:String) {
    private val apiUrl = "https://entreprise.data.gouv.fr/api/sirene/v1/full_text/%s"
    private val apiUrlCP = "https://entreprise.data.gouv.fr/api/sirene/v1/full_text/%s?code_postal=%s"
    private val apiUrlDEP = "https://entreprise.data.gouv.fr/api/sirene/v1/full_text/%s?departement=%s"

    val companyDAO = companyDAO
    val searrchDAO = searchDAO
    val linkDAO = linkDAO

    //ADD SEARCH
    val searchText = text
    val search = Search(null,searchText,null,"","")



    fun getCompany(query:String,dep:String?="", cp:String?=""):List<Company>{

        var conn: HttpsURLConnection? = null

        try{
            var url: URL? = null
            var idSearch: Long? = null
            if(dep != ""){
                if (dep != null) {
                    search.dep = dep
                }
                idSearch = searrchDAO.insert(search)
                url = URL(String.format(apiUrlDEP,query,dep))
            }
            if(cp != ""){
                if (cp != null) {
                    search.cp = cp
                }
                idSearch = searrchDAO.insert(search)
                url = URL(String.format(apiUrlCP,query,cp))
            }
            if(cp == "" && dep ==""){
                 idSearch = searrchDAO.insert(search)
                 url = URL(String.format(apiUrl,query))
            }

            if (url != null) {
                conn = url.openConnection() as HttpsURLConnection
            }
            if (conn != null) {
                conn.connect()
            }

            println("""url $url""")
            val code = conn?.responseCode
            if(code != HttpsURLConnection.HTTP_OK)
                return emptyList()
            val list = mutableListOf<Company>()
            val inputStream = conn?.inputStream ?: return list
            val reader = JsonReader(inputStream.bufferedReader())

            var company = Company(null,"","","")

            reader.beginObject()
            while (reader.hasNext()){
                var next=reader.nextName()
                if(next=="etablissement"){
                    reader.beginArray()
                    while(reader.hasNext()){
                        reader.beginObject()
                        while (reader.hasNext()){
                            when(reader.nextName()){
                                "nom_raison_sociale"->company.libelle = reader.nextString().toString()
                                "code_postal" ->  company.cp = reader.nextString()
                                "siret"->company.siret = reader.nextString().toString()
                                else->reader.skipValue()
                            }
                        }

                        var old = companyDAO.getBySiret(company.siret)

                        var idEntreprise:Long?=null

                        if(old!=null){
                            idEntreprise = old.id

                            old.libelle= company.libelle

                            //println("la")
                            companyDAO.update(old)
                        }
                        else{
                            //println("ici")
                            idEntreprise = companyDAO.insert(company)
                        }

                        //rajout de tout les liens
                        var link = Link(idEntreprise!!,idSearch!!)
                        linkDAO.insert(link)

                        list.add(company)

                        company = Company(null,"","","")


                        reader.endObject()
                    }
                    return list


                }
                else{
                    reader.skipValue()
                }
            }

        reader.endObject()

        }
        catch (e:IOException){
            return emptyList()
        }
        finally {
            conn?.disconnect()
        }
        return emptyList()
    }

}