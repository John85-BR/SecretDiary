package org.hyperskill.secretdiary
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.hyperskill.secretdiary.databinding.ActivityMainBinding
import java.time.format.DateTimeFormatter
import java.util.ArrayDeque

const val PREF_DIARY = "PREF_DIARY"
const val KEY_DIARY_TEXT = "KEY_DIARY_TEXT"

class MainActivity : AppCompatActivity() {

    private lateinit var stack : java.util.ArrayDeque<String>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    private fun alertDialog(){

        AlertDialog.Builder(this)
            .setTitle("Remove last note")
            .setMessage("Do you really want to remove the last writing? This operation cannot be undone!")
            .setPositiveButton(android.R.string.ok) { _, _ ->
                if(stack.isNotEmpty()) stack.pop()
                var temp = ""
                temp += stack.joinToString("\n\n").trim()
                binding.tvDiary.text = temp
                sharedPreferences.edit().putString(KEY_DIARY_TEXT,temp).apply()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        stack = java.util.ArrayDeque<String>()
        sharedPreferences = getSharedPreferences(PREF_DIARY, MODE_PRIVATE)

        val listTemp = sharedPreferences.getString(KEY_DIARY_TEXT,"")?.split("\n\n")
        stack = ArrayDeque(listTemp!!)
        var temp = ""
        temp += stack.joinToString("\n\n")
        binding.tvDiary.text = temp

        binding.btnSave.setOnClickListener {
            if (binding.etNewWriting.text.isBlank() || binding.etNewWriting.text.isEmpty()){
                Toast.makeText(this, "Empty or blank input cannot be saved",Toast.LENGTH_SHORT).show()
            }else{
                val dateTime = Instant.parse(Clock.System.now().toString())
                val tz: TimeZone = TimeZone.currentSystemDefault()
                val local: LocalDateTime = dateTime.toLocalDateTime(tz)
                val formatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss")
                stack.push("${formatter.format(local.toJavaLocalDateTime())}\n${binding.etNewWriting.text}")
                var temp = ""
                temp += stack.joinToString("\n\n").trim()
                binding.tvDiary.text = temp
                sharedPreferences.edit().putString(KEY_DIARY_TEXT,temp).apply()
                binding.etNewWriting.text.clear()
            }

        }
        binding.btnUndo.setOnClickListener {
            alertDialog()
        }

    }
}