package com.example.purrrfectpoi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.adapters.Rewards
import com.example.purrrfectpoi.adapters.RewardsAdapter
import com.psm.hiring.Utils.DataManager

class ProfileActivity : AppCompatActivity() {

    val TAG="ProfileActivity"

    var btn_Editar : Button? = null;
    var btn_Editar_Foto : ImageView? = null;
    var header_back : View? = null;
    var navbar_back : ImageView? = null;

    var editTextNombre : EditText? = null;
    var editTextApPaterno : EditText? = null;
    var editTextApMaterno : EditText? = null;

    private lateinit var recyclerViewMedallas : RecyclerView
    private lateinit var rewardsAdapter: RewardsAdapter

    var NombreUsuario : String? = null;
    var ApPaternoUsuario : String? = null;
    var ApMaternoUsuario : String? = null;
    var medallasList = mutableListOf<Rewards>()

    var editable:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        this.btn_Editar = findViewById<Button>(R.id.profile_btn_editar)
        this.btn_Editar_Foto = findViewById<ImageView>(R.id.profile_image_btn_editar_foto)
        this.navbar_back = findViewById<ImageView>(R.id.profile_image_btn_back)
        this.header_back = findViewById<View>(R.id.profile_header_back)

        this.editTextNombre = findViewById<EditText>(R.id.profile_input_nombre)
        this.editTextApPaterno = findViewById<EditText>(R.id.profile_input_ap_paterno)
        this.editTextApMaterno = findViewById<EditText>(R.id.profile_input_ap_materno)

        editTextNombre!!.isEnabled = false
        editTextApPaterno!!.isEnabled = false
        editTextApMaterno!!.isEnabled = false

        this.btn_Editar?.setOnClickListener {
            editarUsuario(this)
        }

        this.btn_Editar_Foto?.setOnClickListener {
            editarFotoUsuario(this)
        }

        this.navbar_back?.setOnClickListener {
            val vIntent = Intent(this, MainActivity::class.java)
            startActivity(vIntent)
        }

        this.header_back?.setOnClickListener {
            val vIntent = Intent(this, MainActivity::class.java)
            startActivity(vIntent)
        }


        this.recyclerViewMedallas = findViewById<RecyclerView>(R.id.profile_rv_medallas)
        setupRecyclerView()
    }

    private fun editarUsuario(profileActivity: ProfileActivity) {
        if (!editable){
            editTextNombre!!.isEnabled = true
            editTextApPaterno!!.isEnabled = true
            editTextApMaterno!!.isEnabled = true

            editable = true
        }
        else{
            DataManager.progressDialog!!.setMessage("Ingresando")
            DataManager.progressDialog!!.setCancelable(false)
            DataManager.progressDialog!!.show()


            NombreUsuario = editTextNombre?.text.toString()
            ApPaternoUsuario = editTextApPaterno?.text.toString()
            ApMaternoUsuario = editTextApMaterno?.text.toString()

            if(     NombreUsuario!!.isNullOrEmpty()
                ||  ApPaternoUsuario!!.isNullOrEmpty()
                ||  ApMaternoUsuario!!.isNullOrEmpty()
            ){

                if(editTextNombre?.text.isNullOrEmpty()){
                    editTextNombre?.setError("Campo vacio")
                }
                if(editTextApPaterno?.text.isNullOrEmpty()){
                    editTextApPaterno?.setError("Campo vacio")
                }
                if(editTextApMaterno?.text.isNullOrEmpty()){
                    editTextApMaterno?.setError("Campo vacio")
                }

                if(DataManager.progressDialog!!.isShowing) DataManager.progressDialog!!.dismiss()
            }else{
                editTextNombre!!.isEnabled = false
                editTextApPaterno!!.isEnabled = false
                editTextApMaterno!!.isEnabled = false

                editable = false
                Toast.makeText(applicationContext, "Se ha editado con exito la información", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editarFotoUsuario(profileActivity: ProfileActivity) {
        //TODO: EDITAR FOTO
    }



    private fun setupRecyclerView(){
        rewardsAdapter = RewardsAdapter(medallasList)
        recyclerViewMedallas.apply{
            adapter = rewardsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
}


