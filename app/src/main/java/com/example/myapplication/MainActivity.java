package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.BaseColumns;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText txtid;

    private EditText txtnombre;

    private EditText txtapellido;

    FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtid = findViewById(R.id.txtid);
        txtnombre = findViewById(R.id.txtnombre);
        txtapellido = findViewById(R.id.txtapellido);
    }

    public void Listar(View vista) {
        Intent listar = new Intent(this, Listado.class);
        startActivity(listar);
    }

    public void Guardar(View vista) {
        // Gers the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.column1, txtnombre.getText().toString());
        values.put(FeedReaderContract.FeedEntry.column2, txtapellido.getText().toString());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedReaderContract.FeedEntry.nameTable, null, values);

        Toast.makeText(getApplicationContext(), "se guardo el registro con clave: " +
                newRowId, Toast.LENGTH_LONG).show();
        db.close();
    }

    public void Buscar(View vista) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Define a projection that specifies which columns form the database
        // you will actually use after this query
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.column1,
                FeedReaderContract.FeedEntry.column2};
        // Filter results WHERE "title" = 'My Title'
        String selection = FeedReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = {txtid.getText().toString()};
        // How you want the results sorted in the resulting Cursor
        String sortOrder = FeedReaderContract.FeedEntry.column2 + " ASC";
        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.nameTable,     // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        while (cursor.moveToNext()) {
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column1));
            txtnombre.setText(nombre+"");
            String apellido = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column2));
            txtapellido.setText(apellido+"");
        }
        db.close();

    }

    public void Eliminar(View vista) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = FeedReaderContract.FeedEntry._ID + " - ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {txtid.getText().toString()};
        // Issue SQL statement
        int deletedRows = db.delete(FeedReaderContract.FeedEntry.nameTable, selection, selectionArgs);
        db.close();
        Toast.makeText(getApplicationContext(), "se eliminó " +
                deletedRows + " registro(s)", Toast.LENGTH_LONG).show();

    }

    public void Actualizar(View vista) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // New value for one column
        String nombre = txtnombre.getText().toString();
        String apellido = txtapellido.getText().toString();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.column1, nombre);
        values.put(FeedReaderContract.FeedEntry.column2, apellido);
        // Which row to update, based on the title
        String selection = FeedReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = {txtid.getText().toString()};

        int count = db.update(FeedReaderContract.FeedEntry.nameTable,
                values,
                selection,
                selectionArgs);
        Toast.makeText(getApplicationContext(), "se actualizo " +
                count + " registro(s)", Toast.LENGTH_LONG).show();
        db.close();
    }
}