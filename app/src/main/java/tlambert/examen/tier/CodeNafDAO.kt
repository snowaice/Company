package tlambert.examen.tier

import androidx.room.*
import tlambert.examen.model.code_NAF_APE

@Dao
interface CodeNafDAO {

    @Query("SELECT * FROM code_NAF_APE where CodeNAFAPE LIKE :s or Description LIKE :s")
    fun getNaf(s:String): List<code_NAF_APE>


    @Query("SELECT COUNT(*) FROM code_NAF_APE")
    fun count(): Int

    @Insert
    fun insert(codeNaf: code_NAF_APE): Long

    @Update
    fun update(codeNaf: code_NAF_APE)

    @Delete
    fun delete(codeNaf: code_NAF_APE)
}