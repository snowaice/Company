package tlambert.examen.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable
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
        )]
)
data class Link(@PrimaryKey(autoGenerate = false) var company:Long?= null,
                @PrimaryKey(autoGenerate = false) var search:Long?= null,
                var date: Date?= null):Serializable{






}