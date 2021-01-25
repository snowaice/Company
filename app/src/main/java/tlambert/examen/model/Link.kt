package tlambert.examen.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*


@Entity(
    foreignKeys = [
        ForeignKey(
        entity = Company::class,
        parentColumns = ["id"],
        childColumns = ["company"],
        onDelete = ForeignKey.CASCADE
        ),

        ForeignKey(
        entity = Search::class,
        parentColumns = ["id"],
        childColumns = ["search"],
        onDelete = ForeignKey.CASCADE
        )],
    primaryKeys = ["company", "search"]
)
data class Link(var company:Long,
                var search:Long):Serializable{

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Link

                if (company != other.company) return false
                if (search != other.search) return false

                return true
        }

        override fun hashCode(): Int {
                var result = company?.hashCode() ?: 0
                result = 31 * result + (search?.hashCode() ?: 0)
                return result
        }

        override fun toString(): String {
                return "Link(company=$company, search=$search)"
        }

}