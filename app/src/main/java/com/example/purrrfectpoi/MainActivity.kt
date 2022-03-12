package com.example.purrrfectpoi

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.purrrfectpoi.Models.UsuariosModel
import com.example.purrrfectpoi.fragments.ChatsFragment
import com.example.purrrfectpoi.fragments.GruposFragment
import com.example.purrrfectpoi.fragments.MuroFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.psm.hiring.Utils.DataManager
import java.io.File

class MainActivity : AppCompatActivity() {

    val TAG="MainActivity"
    var image_Foto : ImageView? = null
    var titulo_pantalla : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.image_Foto = findViewById<ImageView>(R.id.main_image_profile)
        this.titulo_pantalla = findViewById<TextView>(R.id.main_text_title)

        setUpImageUser()

        this.image_Foto!!.setOnClickListener{
            val vIntent = Intent(this, ProfileActivity::class.java)
            startActivity(vIntent)
        }

        val muroFragment=MuroFragment()
        val chatsFragment=ChatsFragment()
        val gruposFragment=GruposFragment()

        setCurrentFragment(muroFragment) //TODO: COMO SETTEAR EN EL NAVBAR AL INICIO LA OPCION DE MURO?, DE MOMENTO SE SOLUCIONO PONIENDO AL FRAGMENT COMO PRIMERA OPCION
        this.titulo_pantalla!!.setText("Muro")

        val bottom_navigation=findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottom_navigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_chats ->{
                    this.titulo_pantalla!!.setText("Chats")
                    setCurrentFragment(chatsFragment)
                    Log.i(TAG, "Chats Selected")
                }
                R.id.nav_muro ->{
                    this.titulo_pantalla!!.setText("Muro")
                    setCurrentFragment(muroFragment)
                    Log.i(TAG, "Muro Selected")
                }
                R.id.nav_grupos ->{
                    this.titulo_pantalla!!.setText("Grupos")
                    setCurrentFragment(gruposFragment)
                    Log.i(TAG, "Grupos Selected")
                }
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.content,fragment)
            commit()
        }



    private fun setUpImageUser() {
        var usuarioProfile : UsuariosModel = UsuariosModel()

        FirebaseFirestore.getInstance().collection("Usuarios").document(DataManager.emailUsuario!!).get()
            .addOnSuccessListener {
                usuarioProfile.Foto =       if(it.get("Foto") != null)      it.get("Foto") as String else ""

                if (usuarioProfile.Foto.isNotEmpty()) {
                    /*
                    val storageRef = FirebaseStorage.getInstance().reference.child("images/Usuarios/${usuarioProfile.Foto}")

                    val localfile = File.createTempFile("tempImage", "jpg")
                    storageRef.getFile(localfile)
                        .addOnSuccessListener {

                            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                            image_Foto!!.setImageBitmap(bitmap)

                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "No pudo cargar la imagen del Usuario", Toast.LENGTH_SHORT).show()
                        }

                     */

                    FirebaseStorage.getInstance().getReference("images/Usuarios/${usuarioProfile.Foto}").downloadUrl
                        .addOnSuccessListener {

                            Glide.with(this)
                                .load(it.toString())
                                .into(image_Foto!!)
                        }
                }
                else {
                    image_Foto!!.setImageResource(R.drawable.logo_app)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error al conseguir la información del Usuario", exception)
            }
    }

}


