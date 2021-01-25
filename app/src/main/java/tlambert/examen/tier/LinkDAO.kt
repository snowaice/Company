package tlambert.examen.tier

import androidx.room.*
import tlambert.examen.model.Link

@Dao
interface LinkDAO {

    @Query("SELECT * FROM Link")
    fun getLink(): List<Link>

    @Query("SELECT COUNT(*) FROM Link")
    fun count(): Int

    @Insert
    fun insert(link: Link): Long

    @Update
    fun update(link: Link)

    @Delete
    fun delete(link: Link)
}