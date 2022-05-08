package com.example.purrrfectpoi

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.purrrfectpoi.Models.TareasModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.psm.hiring.Utils.DataManager
import java.text.SimpleDateFormat
import java.util.*

class AddTaskGroupActivity : AppCompatActivity() {

    var grupoId : String? = "";
    var tareaId : String? = "";

    var btn_Crear_Tarea_Grupo : Button? = null;
    var header_back : ImageView? = null;

    var editTextTaskName : EditText? = null;
    var editTextTaskDescription : EditText? = null;
    var editTextTaskDate : EditText? = null;

    var tareaCreada : TareasModel = TareasModel()

    private lateinit var calendar : Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_ac)


        this.header_back = findViewById<ImageView>(R.id.add_tasks_group_header_back)
        this.btn_Crear_Tarea_Grupo = findViewById<Button>(R.id.add_task_btn_confirm)

        this.editTextTaskName = findViewById<EditText>(R.id.add_task_input_nombre)
        this.editTextTaskDescription = findViewById<EditText>(R.id.add_task_input_descripcion)
        this.editTextTaskDate = findViewById<EditText>(R.id.add_task_date)
        calendar = Calendar.getInstance()

        this.header_back!!.setOnClickListener{
            onBackPressed()
        }

        this.editTextTaskDate!!.setOnClickListener{
            showDatePickerDialog(editTextTaskDate!!)
        }

        if (intent.hasExtra("grupoId")) {
            //if (true){
            val bundle = intent.extras
            this.grupoId = bundle!!.getString("grupoId")
        }

        if (intent.hasExtra("tareaId")) {
            val bundle = intent.extras
            this.tareaId = bundle!!.getString("tareaId")

            setUpInfoTarea(this.tareaId!!)
        }
        else{
            tareaCreada.FechaProgramada = DataManager.getTimeStamptToday()
            editTextTaskDate!!.setText(DataManager.TimeStampToDayHourYear(tareaCreada.FechaProgramada!!))
        }

        btn_Crear_Tarea_Grupo!!.setOnClickListener {
            ingresarCurso()
        }
    }

    private fun setUpInfoTarea(tareaId : String) {

        FirebaseFirestore.getInstance().collection("Tareas").document(tareaId).get()

            .addOnSuccessListener {

                //TODO: NO ES TAN NECESARIO TRAERNOS LA INFO DE "CONVERSACION" Y "TAREAS", ESTA BIEN DE TODOS MODOS TRAERLA?
                tareaCreada.id = tareaId
                tareaCreada.Nombre = if(it.get("Nombre") != null) it.get("Nombre") as String else ""
                tareaCreada.Descripcion = if(it.get("Descripcion") != null) it.get("Descripcion") as String else ""
                tareaCreada.FechaCreacion = if(it.get("FechaCreacion") != null) it.get("FechaCreacion") as Timestamp else null
                tareaCreada.FechaProgramada = if(it.get("FechaProgramada") != null) it.get("FechaProgramada") as Timestamp else null
                tareaCreada.TrabajosAlumnos = if(it.get("TrabajosAlumnos") != null) it.get("TrabajosAlumnos") as ArrayList<DocumentReference> else arrayListOf()


                editTextTaskName?.setText(tareaCreada.Nombre)
                editTextTaskDescription?.setText(tareaCreada.Descripcion)
                editTextTaskDate!!.setText(DataManager.TimeStampToDayHourYear(tareaCreada.FechaProgramada!!))

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error al conseguir la información del Usuario", exception)
            }
    }

    private fun ingresarCurso(){
        tareaCreada.Nombre = editTextTaskName?.text.toString()
        tareaCreada.Descripcion = editTextTaskDescription?.text.toString()

        if(     tareaCreada.Nombre.isEmpty()
            ||  tareaCreada.Descripcion.isEmpty()
            ||  tareaCreada.FechaProgramada == null
            ||  validateTimestampHasError(tareaCreada.FechaProgramada!!)
        ){

            if(tareaCreada.Nombre.isEmpty()){
                editTextTaskName?.setError("Campo vacio")
            }
            if(tareaCreada.Descripcion.isEmpty()){
                editTextTaskDescription?.setError("Campo vacio")
            }

            if(tareaCreada.FechaProgramada == null){
                DataManager.showToast(this, "Error, Campo vacio")
            }
            else if(validateTimestampHasError(tareaCreada.FechaProgramada!!)){
                DataManager.showToast(this, "Error, Fecha ya paso")
            }

        }else {
            if (this.tareaId == "")
                crearTarea()
            else
                editarTarea()
        }
    }

    private fun crearTarea(){

        FirebaseFirestore.getInstance().collection("Tareas")
            .add(
                hashMapOf(
                    "Nombre" to tareaCreada.Nombre,
                    "Descripcion" to tareaCreada.Descripcion,
                    "FechaCreacion" to FieldValue.serverTimestamp(),
                    "FechaProgramada" to tareaCreada.FechaProgramada,
                    "TrabajosAlumnos" to tareaCreada.TrabajosAlumnos
                )
            ).addOnCompleteListener { responseTaskCreation ->
                if (responseTaskCreation.isSuccessful) {
                    var documentReferenceTaskCreated = FirebaseFirestore.getInstance().collection("Tareas").document(responseTaskCreation.result.id)

                    FirebaseFirestore.getInstance().collection("Grupos").document(grupoId!!)
                        .update(
                            "Tareas", FieldValue.arrayUnion(documentReferenceTaskCreated)
                        )

                    DataManager.showToast(this,"Tarea creada correctamente")

                    salirAVentanaGrupo()

                } else {
                    DataManager.showToast(this,"Error: ${responseTaskCreation.exception!!.message}")
                }
            }
    }

    private fun editarTarea() {
        FirebaseFirestore.getInstance().collection("Tareas").document(tareaId!!)
            .update(
                mapOf(
                    "Nombre" to tareaCreada.Nombre,
                    "Descripcion" to tareaCreada.Descripcion,
                    "FechaProgramada" to tareaCreada.FechaProgramada,
                )
            ).addOnCompleteListener{ responseTaskCreation ->
                if (responseTaskCreation.isSuccessful) {
                    DataManager.showToast(this,"Tarea creada correctamente")

                    salirAVentanaGrupo()
                }
                else{
                    DataManager.showAlert(this, "Se ha producido un error al editar el usuario")
                }
            }

    }


    private fun salirAVentanaGrupo(){
        val vIntent = Intent(this, GroupActivity::class.java)
        vIntent.putExtra("grupoId", this.grupoId)
        startActivity(vIntent)
    }

    private fun showDatePickerDialog(textView: TextView) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog : DatePickerDialog = DatePickerDialog(this,
            {
                datePicker, year, month, day ->

                var stringDate = "$year-${month + 1}-$day 23:59:59"
                val format = SimpleDateFormat("yyy-MM-dd HH:mm:ss")
                val date = format.parse(stringDate)
                tareaCreada.FechaProgramada = Timestamp(date.time / 1000, 0)

                textView.text = DataManager.TimeStampToDayHourYear(tareaCreada.FechaProgramada)

            }, year, month, day)


        datePickerDialog.show()
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }

    fun validateTimestampHasError (dateTimestamp: Timestamp) : Boolean{

        val dateTimestampLimit = DataManager.getTimeStamptToday()

        return dateTimestamp.seconds < dateTimestampLimit.seconds
    }


}