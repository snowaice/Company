package tlambert.examen.tier

import android.util.JsonReader
import android.util.JsonToken
import tlambert.examen.model.Company
import tlambert.examen.model.Link
import tlambert.examen.model.Search
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HttpsURLConnection

class CompanyService(companyDAO:CompanyDAO,searchDAO:SearchDAO,linkDAO:LinkDAO,text:String) {
    private val apiUrl = "https://entreprise.data.gouv.fr/api/sirene/v1/full_text/%s?per_page=100"
    private val apiUrlCP = "https://entreprise.data.gouv.fr/api/sirene/v1/full_text/%s?code_postal=%s&per_page=100"
    private val apiUrlDEP = "https://entreprise.data.gouv.fr/api/sirene/v1/full_text/%s?departement=%s&per_page=100"

    private val apiUrlCodeNAF = "https://entreprise.data.gouv.fr/api/sirene/v1/full_text/%s?activite_principale=%s&per_page=100"


    val companyDAO = companyDAO
    val searrchDAO = searchDAO
    val linkDAO = linkDAO

    //ADD SEARCH
    val searchText = text
    val search = Search(null,searchText, SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) ,"","","")


    fun getCompany(query:String,dep:String, cp:String,codeNAF:String):List<Company>{

        var conn: HttpsURLConnection? = null

        try{
            var url: URL? = null
            var idSearch: Long? = null

            if(dep != ""){
                search.dep = dep
                idSearch = searrchDAO.insert(search)
                url = URL(String.format(apiUrlDEP,query,dep))
            }

            if (cp != "") {
                search.cp = cp
                idSearch = searrchDAO.insert(search)
                url = URL(String.format(apiUrlCP,query,cp))
            }
            if(codeNAF != ""){
                search.codeNAF = codeNAF
                idSearch = searrchDAO.insert(search)
                url = URL(String.format(apiUrlCodeNAF,query,codeNAF))
            }

            if(cp == "" && dep =="" && codeNAF ==""){
                 idSearch = searrchDAO.insert(search)
                 url = URL(String.format(apiUrl,query))
            }


            if (url != null) {
                conn = url.openConnection() as HttpsURLConnection
            }

            conn?.connect()

            println("""url $url""")

            val code = conn?.responseCode

            if(code != HttpsURLConnection.HTTP_OK)
                return emptyList()

            val list = mutableListOf<Company>()
            val inputStream = conn?.inputStream ?: return list
            val reader = JsonReader(inputStream.bufferedReader())

            var company = Company(null,"","","","","","","","")

            reader.beginObject()
            while (reader.hasNext()){
                var next=reader.nextName()
                if(next=="etablissement"){
                    reader.beginArray()
                    while(reader.hasNext()){
                        reader.beginObject()
                        while (reader.hasNext()){
                            when(reader.nextName()){

                                "nom_raison_sociale" ->{ if (reader.peek() == JsonToken.NULL)  {
                                        company.libelle = reader.nextString()
                                    }else{
                                        company.libelle = reader.nextString()
                                    }
                                }

                                "code_postal" ->{ if (reader.peek() == JsonToken.NULL)  {
                                        company.cp = reader.nextNull().toString()
                                    }else{
                                        company.cp = reader.nextString()
                                    }
                                }

                                "siret" ->{ if (reader.peek() == JsonToken.NULL)  {
                                        company.siret = reader.nextNull().toString()
                                    }else{
                                        company.siret = reader.nextString()
                                    }
                                }
                                "region" -> {
                                    if (reader.peek() == JsonToken.NULL)  {
                                        company.region = reader.nextNull().toString()
                                    }else{
                                        company.region = reader.nextString()
                                    }
                                }
                                "libelle_activite_principale" -> {
                                    if (reader.peek() == JsonToken.NULL)  {
                                        company.activite = reader.nextNull().toString()
                                    }else{
                                        company.activite = reader.nextString()
                                    }
                                }
                                "latitude" -> {
                                    if (reader.peek() == JsonToken.NULL)  {
                                        company.latitude = reader.nextNull().toString()
                                    }else{
                                        company.latitude = reader.nextString()
                                    }
                                }
                                "longitude" -> {
                                    if (reader.peek() == JsonToken.NULL)  {
                                        company.longitude = reader.nextNull().toString()
                                    }else{
                                        company.longitude = reader.nextString()
                                    }
                                }
                                "activite_principale" -> {
                                    if (reader.peek() == JsonToken.NULL)  {
                                        company.activite_principale = reader.nextNull().toString()
                                    }else{
                                        company.activite_principale = reader.nextString()
                                    }
                                }


                                else->reader.skipValue()
                            }
                        }

                        var companyGet = companyDAO.getSiret(company.siret)

                        var idCompany :Long

                        if(companyGet!=null){
                            idCompany = companyGet.id!!

                            companyGet.libelle= company.libelle
                            companyGet.activite = company.activite
                            companyGet.cp = company.cp
                            companyGet.activite_principale = company.activite_principale
                            companyGet.latitude = company.latitude
                            companyGet.longitude = company.longitude
                            companyGet.region = company.region

                            companyDAO.update(companyGet)
                        }
                        else{
                            idCompany = companyDAO.insert(company)
                        }

                        var link = Link(idCompany!!,idSearch!!)
                        linkDAO.insert(link)

                        list.add(company)

                        company = Company(null,"","","","","","","","")


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