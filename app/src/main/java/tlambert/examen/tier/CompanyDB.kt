package tlambert.examen.tier

import androidx.room.*
import android.content.Context
import tlambert.examen.model.*

@Database(version = 1, entities = [Search::class, Company::class,Link::class])
@TypeConverters(DateConverter::class)

abstract class CompanyDB : RoomDatabase() {
    abstract fun searchDAO(): SearchDAO
    abstract fun companyDAO(): CompanyDAO
    abstract fun linkDAO():LinkDAO

    companion object {
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