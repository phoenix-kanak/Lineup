// login up
package com.example.lineup
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.example.lineup.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


public class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    private var database = FirebaseDatabase.getInstance()
    val userid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val userInfo=database.getReference("users/$userid")
    private lateinit var zeal_id:String
    private lateinit var auth_password:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root);

val loginbtn = binding.loginBtn
     //   Log.e("zealID","$zeal")
        userInfo.get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val snapshot=task.result
                zeal_id=snapshot.child("zealid").value.toString()
                auth_password=snapshot.child("Password").value.toString()

                loginbtn.setOnClickListener {
                    val zeal = binding.zeal.text.toString()
                    val password = binding.password.text.toString()

                    if(zeal.isEmpty() || password.isEmpty())
                    {
                        //Log.e("zealID","$zeal")
                        Toast.makeText(this, "Please enter your ZealId and Password", Toast.LENGTH_SHORT).show()
                    }
                    else{

                        if(zeal == zeal_id && password == auth_password){
                            Toast.makeText(this,"User verified",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this,bottom_activity::class.java))
                            finish()
                        }else{
                            Toast.makeText(this,"Oops! User not registered ",Toast.LENGTH_SHORT).show()
                        }
                    }
                }

             //   Log.e("id123", zeal_id)

            }else{
              //  Log.e("TAG","Error getting data")
            }

        }




    }
}