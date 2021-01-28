package tlambert.examen.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
        indices = [Index(value = ["id"],unique = true)]
)
data class code_NAF_APE(@PrimaryKey(autoGenerate = false) var id:Int,
                        var CodeNAFAPE:String?,
                        val Description:String?,
                        val Section:String?,
                        val Descriptionsection:String?):Serializable
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as code_NAF_APE

        if (CodeNAFAPE != other.CodeNAFAPE) return false
        if (Description != other.Description) return false
        if (Section != other.Section) return false
        if (Descriptionsection != other.Descriptionsection) return false

        return true
    }

    override fun hashCode(): Int {
        var result = CodeNAFAPE?.hashCode() ?: 0
        result = 31 * result + Description.hashCode()
        result = 31 * result + Section.hashCode()
        result = 31 * result + Descriptionsection.hashCode()
        return result
    }

    override fun toString(): String {
        return "CodeNAFAPE='$CodeNAFAPE' Description='$Description'"
    }
}


