package com.example.lineup

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.lineup.models.Code
import com.example.lineup.models.scanner
import com.gdsc.lineup2024.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.journeyapps.barcodescanner.ViewfinderView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class scanner : Fragment() {
    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var viewfinderView: ViewfinderView
    private var lastText: String? = null
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var sharedPreferences: SharedPreferences
    private var scannedQRSet = HashSet<String>()
    private lateinit var textViewTimer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("LineUpTokens", Context.MODE_PRIVATE)
        scannedQRSet = (sharedPreferences.getStringSet("scannedQRSet", HashSet<String>()) ?: HashSet()) as HashSet<String>

    }

    private val callback = object : BarcodeCallback {
        val sharedPreferences = context?.getSharedPreferences("LineUpTokens", Context.MODE_PRIVATE)
        val set = sharedPreferences?.getStringSet("scannedQRSet", HashSet<String>())

        override fun barcodeResult(result: BarcodeResult) {
            barcodeView.pause()
            
            val token = Code(result.text)
            scanQRCode(token)
        }
        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }


    fun scanQRCode(qrCode: Code) {


        val code = qrCode.code // Get the code from the Code object
        val qrCodeString = code
        val set = sharedPreferences.getStringSet("scannedQRSet", HashSet<String>())

        if (set!!.contains(qrCodeString)) {
            popup("Oops! Duplicate Member")
        } else {
            sendQRtoBackend(qrCode,qrCodeString,set)
        }
    }

    fun sendQRtoBackend(
        qrCode: Code,
        qrCodeString: String,
        set: MutableSet<String>
    ) {
        sharedPreferences =
            requireActivity().getSharedPreferences("LineUpTokens", Context.MODE_PRIVATE)
        val retrievedValue = sharedPreferences.getString("Token", "defaultValue") ?: "defaultValue"
        val header = "Bearer $retrievedValue"
        val call = RetrofitApi.apiInterface.scan(header, qrCode)
        call.enqueue(object : Callback<scanner> {
            override fun onResponse(call: Call<scanner>, response: Response<scanner>) {
                val responseBody = response.body()
                if (responseBody != null) {
                    if (responseBody.message == "QR Code scanned successfully") {
                        popup("Member Found!")
                        val updatedSet = HashSet(set) // Create a copy of the set
                        updatedSet.add(qrCodeString) // Modify the copy
                        sharedPreferences.edit().putStringSet("scannedQRSet", updatedSet).apply() // Save the modified set back to SharedPreferences
                    } else{
                        val msg = responseBody.message
                        popup(msg)
                    }
                }
                else{
                    popup("Something went wrong")
                }
            }
            override fun onFailure(call: Call<scanner>, t: Throwable) {
                popup("Something went wrong")
            }
        })
    }

    fun popup(msg: String){
        val popup = Dialog(requireContext())
        popup.requestWindowFeature(Window.FEATURE_NO_TITLE)
        popup.setCancelable(false)
        popup.setContentView(R.layout.member_found)
        val message = popup.findViewById<TextView>(R.id.message)
        val reset = popup.findViewById<Button>(R.id.reset_Button)
        popup.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        message.text = msg

        reset.setOnClickListener {
            barcodeView.resume()
            //  scanningEnabled = true
            popup.dismiss()
        }
        popup.show()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_scanner, container, false)

        barcodeView = view.findViewById(R.id.barcode_scanner)

        val formats = listOf(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)
        barcodeView.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcodeView.initializeFromIntent(requireActivity().intent)
        barcodeView.decodeContinuous(callback)
        textViewTimer = view.findViewById<TextView>(R.id.timeleft)
        val targetDate = "2024-05-20"
        val targetTime = "19:00:00"
        startCountdownToDateTime(targetDate, targetTime)

        return view
    }

    private fun startCountdownToDateTime(targetDate: String, targetTime: String) {

        // Parse the target date and time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val targetDateTime = dateFormat.parse("$targetDate $targetTime")

        // Calculate milliseconds until the target date and time
        val millisecondsUntilTarget = targetDateTime.time - System.currentTimeMillis()

        // Create and start the countdown timer
        countDownTimer = object : CountDownTimer(millisecondsUntilTarget, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished / (1000 * 60 * 60)
                val minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60)
                val seconds = ((millisUntilFinished % (1000 * 60 * 60)) % (1000 * 60) / 1000)
                if (hours > 0) {
                    textViewTimer?.text = String.format("%02d hours %02d minutes", hours, minutes)
                    Log.e("timer", "${textViewTimer?.text}")
                } else {
                    textViewTimer?.text = String.format("%02d minutes %02d seconds", minutes, seconds)
                }
            }

            override fun onFinish() {
                if (textViewTimer != null) {
                    textViewTimer.text = "00:00:00"
                }
                // You may perform any action when the countdown finishes
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    fun pause() {
        barcodeView.pause()
    }

    fun resume() {
        barcodeView.resume()
    }

    fun triggerScan() {
        barcodeView.decodeSingle(callback)
    }
}