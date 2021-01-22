package tlambert.examen.tier

import android.util.JsonReader
import android.util.JsonToken
import tlambert.examen.model.Company
import java.io.IOException
import java.lang.IllegalStateException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class CompanyService {
    private val apiUrl = "https://entreprise.data.gouv.fr/api/sirene/v1/full_text/%s"

    fun getCompany(query:String):List<Company>{
        val url = URL(String.format(apiUrl,query))
        var conn: HttpsURLConnection? = null

        try{
            conn = url.openConnection() as HttpsURLConnection
            conn.connect()
            val code = conn.responseCode
            if(code != HttpsURLConnection.HTTP_OK)
                return emptyList()
            val list = mutableListOf<Company>()
            val inputStream = conn.inputStream ?: return list
            val reader = JsonReader(inputStream.bufferedReader())
            reader.beginObject()
            while (reader.hasNext()){
                try {
                    val name= reader.nextName()
                    when(name){
                        "etablissement" -> {reader.beginArray()
                                            while (reader.hasNext()){
                                                list.add(readCompany(reader))
                                            }
                                            reader.endArray()}

                        else -> reader.skipValue()
                    }
                }catch (e:IllegalStateException){
                    return list
                }
            }
            reader.endObject()
            return list

        }
        catch (e:IOException){
            return emptyList()
        }
        finally {
            conn?.disconnect()
        }

    }

    private fun readCompany(reader: JsonReader):Company{

        val company = Company(null,"")
        println("""test""")
        reader.beginObject()
        while (reader.hasNext()){
            val name= reader.nextName()
            if(reader.peek() == JsonToken.NULL){
                reader.skipValue()
                continue
            }
            when(name){
                "nom_raison_sociale" -> company.libelle = reader.nextString()
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return company
    }
}