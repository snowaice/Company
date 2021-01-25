package tlambert.examen.tier

import androidx.room.*
import tlambert.examen.model.Company
import tlambert.examen.model.Search

@Dao
interface SearchDAO {
    @Query("SELECT count(*) FROM search WHERE search.date=:date AND search.text=:mot")
    fun countee(date: String, mot: String): Int

    @Query("SELECT count(*) FROM search WHERE search.date=:date AND search.text=:mot and search.cp =:cp")
    fun counteCP(date: String, mot: String,cp:String): Int

    @Query("SELECT count(*) FROM search WHERE search.date=:date AND search.text=:mot and search.dep=:dep")
    fun counteDEP(date: String, mot: String,dep:String): Int


    @Query("SELECT * FROM Search")
    fun getSearch(): List<Search>

    @Query("SELECT COUNT(*) FROM Search")
    fun count(): Int

    @Insert
    fun insert(search: Search): Long

    @Update
    fun update(search: Search)

    @Delete
    fun delete(search: Search)


}