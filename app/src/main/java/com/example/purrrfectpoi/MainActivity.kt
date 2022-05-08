package com.example.purrrfectpoi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.purrrfectpoi.fragments.ChatsFragment
import com.example.purrrfectpoi.fragments.GruposFragment
import com.example.purrrfectpoi.fragments.MuroFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    val TAG="MainActivity"
    var image_Foto : ImageView? = null
    var titulo_pantalla : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.image_Foto = findViewById<ImageView>(R.id.main_image_profile)
        this.titulo_pantalla = findViewById<TextView>(R.id.main_text_title)

        //setUpImageUser()

        this.image_Foto!!.setOnClickListener{
            val vIntent = Intent(this, ProfileActivity::class.java)
            startActivity(vIntent)
        }


        val muroFragment=MuroFragment()
        val chatsFragment=ChatsFragment()
        val gruposFragment=GruposFragment()

        setCurrentFragment(muroFragment)

        val bottom_navigation=findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottom_navigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_chats ->{
                    setCurrentFragment(chatsFragment)
                    Log.i(TAG, "Chats Selected")
                }
                R.id.nav_muro ->{
                    setCurrentFragment(muroFragment)
                    Log.i(TAG, "Muro Selected")
                }
                R.id.nav_grupos ->{
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


/*
    private fun setUpImageUser() {
        var usuarioProfile : UsuariosModel = UsuariosModel()

        FirebaseFirestore.getInstance().collection("Usuarios").document(DataManager.emailUsuario!!).get()
            .addOnSuccessListener {
                usuarioProfile.Foto =       if(it.get("Foto") != null)      it.get("Foto") as String else ""

                if (usuarioProfile.Foto.isNotEmpty()) {
                    FirebaseStorage.getInstance().getReference("images/Usuarios/${usuarioProfile.Foto}").downloadUrl
                        .addOnSuccessListener {

                            Glide.with(this)
                                .load(it.toString())
                                .into(image_Foto!!)
                        }
                }
                else {
                    image_Foto!!.setImageResource(R.drawable.foto_default_perfil)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error al conseguir la información del Usuario", exception)
            }
    }

    */
}


