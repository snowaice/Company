package tlambert.examen.tier

import androidx.room.*
import tlambert.examen.model.Company

@Dao
interface CompanyDAO {
    @Query("SELECT * FROM Company ORDER BY id")
    fun getAllCompany(): List<Company>

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