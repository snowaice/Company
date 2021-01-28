package tlambert.examen.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*


@Entity(
    indices = [Index(value = ["id"],unique = true)]
)

data class Search(@PrimaryKey(autoGenerate = true) var id:Long? = null,
                  var text:String,
                  var date: String? = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                  var cp:String,
                  var dep:String,
                  var codeNAF:String):Serializable
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Search

        if (id != other.id) return false
        if (text != other.text) return false
        if (date != other.date) return false
        if (cp != other.cp) return false
        if (dep != other.dep) return false
        if (codeNAF != other.codeNAF) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + text.hashCode()
        result = 31 * result + (date?.hashCode() ?: 0)
        result = 31 * result + cp.hashCode()
        result = 31 * result + dep.hashCode()
        result = 31 * result + codeNAF.hashCode()
        return result
    }

    override fun toString(): String {
        return "Recherche :'$text' le $date $cp $dep $codeNAF"
    }

}
