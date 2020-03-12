package com.vinay.skrawl

import android.app.ProgressDialog
import android.content.SharedPreferences
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.Menu
import android.widget.CursorAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashSet


class MainActivity : AppCompatActivity() {

    private var SUGGESTIONS: MutableSet<String> = HashSet<String>()
    private var mAdapter: SimpleCursorAdapter? = null

    public lateinit var pref: SharedPreferences // 0 - for private mode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pref = applicationContext.getSharedPreferences("MyPref", 0);
        SUGGESTIONS = pref.getStringSet("res", HashSet<String>())!!

        rvItems.layoutManager = LinearLayoutManager(applicationContext)

        getResults("Sachin")

        val from = arrayOf("cityName")
        val to = intArrayOf(android.R.id.text1)

        mAdapter = SimpleCursorAdapter(
            applicationContext,
            android.R.layout.simple_list_item_1,
            null,
            from,
            to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
    }

    private fun getResults(key: String) {

        val tag_json_arry = "json_array_req"
        val url =
            "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=pageimages%7Cpageterms&generator=prefixsearch&redirects=1&formatversion=2&piprop=thumbnail&pithumbsize=50&pilimit=100&wbptterms=description&gpssearch=${key}&gpslimit=100"

        val pDialog = ProgressDialog(this)
        pDialog.setMessage("Loading...")
        pDialog.show()

        val req = StringRequest(url,
            Response.Listener<String?> { response ->
                Log.i("vvk", response.toString())
                val r: Result = Gson().fromJson(response, Result::class.java)
                rvItems.adapter = MoviesAdapter(r.query.pages, applicationContext)

                val editor: SharedPreferences.Editor = pref.edit()
                SUGGESTIONS?.addAll(r.query.pages.stream().map { t -> t.title }.collect(Collectors.toSet()));
                editor.putStringSet("res", SUGGESTIONS);
                editor.apply()

                pDialog.hide()
            }, Response.ErrorListener { error ->
                Log.e("vvk", "Error: " + error.message)
                pDialog.hide()
            })
        AppController.getInstance().addToRequestQueue(req, tag_json_arry)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        val searchView: SearchView = MenuItemCompat
            .getActionView(menu.findItem(R.id.action_search)) as SearchView
        searchView.suggestionsAdapter = mAdapter
        searchView.setIconifiedByDefault(false)
        // Getting selected (clicked) item suggestion
        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionClick(position: Int): Boolean {
                val cursor: Cursor = mAdapter!!.getItem(position) as Cursor
                val txt: String = cursor.getString(cursor.getColumnIndex("cityName"))

                Toast.makeText(applicationContext, txt, Toast.LENGTH_SHORT)
                    .show()
                getResults(searchView.query.toString().replace(" ", "%20"))
                searchView.setQuery(txt, true)
                return true
            }

            override fun onSuggestionSelect(position: Int): Boolean {
                val cursor: Cursor = mAdapter!!.getItem(position) as Cursor
                val txt: String = cursor.getString(cursor.getColumnIndex("cityName"))

                return true
            }
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                s?.let { getResults(s) };
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                populateAdapter(s)
                return false
            }
        })

        return true
    }

    private fun populateAdapter(query: String) {
        val c = MatrixCursor(arrayOf(BaseColumns._ID, "cityName"))
        var i = 0;

        SUGGESTIONS.stream().forEach {
            if (it.toLowerCase(Locale.getDefault()).startsWith(query.toLowerCase(Locale.getDefault()))) c.addRow(
                arrayOf(
                    i,
                    it
                )
            )
            i++;
        }

        mAdapter!!.changeCursor(c)
    }
}
