package tlambert.examen.tier

import androidx.room.*
import tlambert.examen.model.Search

@Dao
interface SearchDAO {

    @Query("SELECT * FROM Search")
    fun getSearch(): Search

    @Query("SELECT COUNT(*) FROM Search")
    fun count(): Int

    @Insert
    fun insert(search: Search): Long

    @Update
    fun update(search: Search)

    @Delete
    fun delete(search: Search)
}