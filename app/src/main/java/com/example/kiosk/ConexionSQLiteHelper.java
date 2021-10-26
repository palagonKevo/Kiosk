package com.example.kiosk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class ConexionSQLiteHelper extends SQLiteOpenHelper {

    private static final String CREAR_TABLA_PRODUCTOS = "CREATE TABLE productos (id INTEGER PRIMARY KEY AUTOINCREMENT, descr TEXT NOT NULL, color TEXT NOT NULL, type TEXT NOT NULL,codi TEXT NOT NULL,tag TEXT NOT NULL,tagComment TEXT NOT NULL, hasData INTEGER NOT NULL)";
    private static final String DB_NAME="product_db";
    private static final int VERSION_DB=1;

    public ConexionSQLiteHelper(Context context) {
        super(context, DB_NAME, null, VERSION_DB);
    }

    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete("productos", null, null);
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAR_TABLA_PRODUCTOS);
        Log.println(Log.INFO,"DB","Data Base succesfully created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS productos");
        onCreate(db);
    }

    public void agregarProductos(Product product){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("descr", product.getDescr());
        values.put("color", product.getColor());
        values.put("type", product.getType());
        values.put("codi", product.getCodi());
        values.put("tag", product.getTag());
        if(product.isHasData()) values.put("hasData", 1);
        else values.put("hasData", 0);
        values.put("tagComment", product.getTagComment());
        if(db!=null){
            db.insert("productos", null, values);
            db.close();
        }
    }

    public Cursor getAllProducts(){
        return getReadableDatabase()
                .query(
                        "productos",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getProductById(String id){
        return getReadableDatabase()
                .query(
                        "productos",
                        null,
                        "id LIKE ?",
                        new String[]{id},
                        null,
                        null,
                        null);
    }
}
