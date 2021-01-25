package tlambert.examen.tier

import androidx.room.*
import tlambert.examen.model.Company

@Dao
interface CompanyDAO {
    @Query("SELECT company.* FROM company,search,link WHERE link.company=company.id AND link.search = search.id AND search.date=:date AND search.text=:mot")
    fun getEntrepriseByRecherche(date: String, mot: String): List<Company>

    @Query("SELECT company.* FROM company,search,link WHERE link.company=company.id AND link.search = search.id AND search.date=:date AND search.text=:mot and search.cp=:cp")
    fun getCP(date: String, mot: String,cp:String): List<Company>


    @Query("SELECT company.* FROM company,search,link WHERE link.company=company.id AND link.search = search.id AND search.date=:date AND search.text=:mot and search.dep=:dep")
    fun getDEP(date: String, mot: String,dep:String): List<Company>

    @Query("SELECT * FROM Company ORDER BY id")
    fun getAllCompany(): List<Company>

    @Query("SELECT * FROM Company WHERE siret =:siret ORDER BY id")
    fun getBySiret(siret:String): Company?


    @Query("SELECT * FROM Company Where id=:id")
    fun getCompany(id:Long): Company

    @Query("SELECT COUNT(*) FROM Company")
    fun count(): Int

    @Insert
    fun insert(company: Company): Long

    @Update
    fun update(company: Company)

    @Delete
    fun delete(company: Company)

}