package tlambert.examen.tier

import androidx.room.*
import android.content.Context
import tlambert.examen.model.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Database(version = 1, entities = [Search::class, Company::class])
@TypeConverters(DateConverter::class)

abstract class CompanyDB : RoomDatabase() {
    abstract fun searchDAO(): SearchDAO
    abstract fun companyDAO(): CompanyDAO

    companion object {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH)
        var INSTANCE: CompanyDB? = null

        fun getDatabase(context:Context): CompanyDB {
            if (INSTANCE == null) {
                INSTANCE = Room
                    .databaseBuilder(context, CompanyDB::class.java, "Company.db")
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE!!
        }
    }
}