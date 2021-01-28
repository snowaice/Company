package tlambert.examen.tier

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import tlambert.examen.model.code_NAF_APE


@Database(version = 1, entities = [code_NAF_APE::class])

abstract class CodeNafDatabase : RoomDatabase(){

    abstract fun CodeNafDAO():CodeNafDAO

    companion object {
        var INSTANCE: CodeNafDatabase? = null

        fun getDatabase(context: Context): CodeNafDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room
                        .databaseBuilder(context, CodeNafDatabase::class.java, "code_NAF_APE.db")
                        .createFromAsset("databases/code_NAF_APE")
                        .allowMainThreadQueries()
                        .build()
            }
            return INSTANCE!!
        }
    }
}