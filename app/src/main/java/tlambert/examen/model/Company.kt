package tlambert.examen.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(
    indices = [Index(value = ["id"],unique = true)]
)

data class Company (@PrimaryKey(autoGenerate = true) var id:Long? = null,
                   var libelle:String): Serializable {


    override fun toString(): String {
        return "$libelle"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Company

        if (id != other.id) return false
        if (libelle != other.libelle) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + libelle.hashCode()
        return result
    }
}
