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
                  var dep:String):Serializable
{

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Search

        if (id != other.id) return false
        if (text != other.text) return false
        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + text.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }

    override fun toString(): String {
        return "Search(id=$id, text='$text', date=$date)"
    }
}
