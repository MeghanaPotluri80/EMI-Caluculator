package com.example.emicalculator


import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.content.ContentUris

class MyContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.dataproviderapp.provider"
        const val PATH = "items"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH")
    }

    private lateinit var database: SQLiteDatabase

    override fun onCreate(): Boolean {
        val helper = MyDatabaseHelper(context)
        database = helper.writableDatabase
        return database != null
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        return database.query("items", projection, selection, selectionArgs, null, null, sortOrder)
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val id = database.insert("items", null, values)
        return ContentUris.withAppendedId(CONTENT_URI, id)
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return database.update("items", values, selection, selectionArgs)
    }



    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return database.delete("items", selection, selectionArgs)
    }

    override fun getType(uri: Uri): String? {
        return "vnd.android.cursor.dir/vnd.com.example.dataproviderapp.items"
    }

    class MyDatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val DATABASE_NAME = "mydatabase.db"
            private const val DATABASE_VERSION = 1
        }

        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL(
                """
                CREATE TABLE items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT
                )
                """.trimIndent()
            )
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL("DROP TABLE IF EXISTS items")
            onCreate(db)
        }
    }
}
