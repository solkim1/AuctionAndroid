package com.example.auctionproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class RegActivity : AppCompatActivity() {

    val CAMERA = arrayOf(android.Manifest.permission.CAMERA)
    val STORAGE = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val CAMERA_CODE = 98
    val STORAGE_CODE = 99

    lateinit var mContext: Context
    lateinit var mActivity: Activity

    lateinit var ivImg: ImageView
    lateinit var queue: RequestQueue
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)

        mContext = this
        mActivity = this

        val toolbar: Toolbar = findViewById(R.id.tbUpdProf)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        ivImg = findViewById(R.id.prod_img_path)
        val btnProduct = findViewById<Button>(R.id.btnProduct)
        val tvCam = findViewById<ImageView>(R.id.tvCam)
        val etProdName = findViewById<EditText>(R.id.prod_name)
        val etProdInfo = findViewById<EditText>(R.id.prod_info)
        val etBidPrice = findViewById<EditText>(R.id.bid_price)
        val etImmediatePrice = findViewById<EditText>(R.id.immediate_price)

        queue = Volley.newRequestQueue(this)

        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data: Intent? = result.data
                    val imageBitmap = data?.extras?.get("data") as Bitmap?
                    if (imageBitmap != null) {
                        ivImg.setImageBitmap(imageBitmap)
                        Log.d("RegActivity", "Image captured successfully.")
                    } else {
                        Log.e("RegActivity", "Failed to capture image.")
                    }
                } else {
                    Log.e("RegActivity", "Image capture failed.")
                }
            }

        tvCam.setOnClickListener { callCamera() }

        btnProduct.setOnClickListener {
            val prodName = etProdName.text.toString()
            val prodInfo = etProdInfo.text.toString()
            val bidPrice = etBidPrice.text.toString()
            val immediatePrice = etImmediatePrice.text.toString()
            val imageDrawable = ivImg.drawable

            if (imageDrawable != null && imageDrawable is BitmapDrawable) {
                val imageBitmap = imageDrawable.bitmap
                if (prodName.isNotEmpty() && prodInfo.isNotEmpty() && bidPrice.isNotEmpty() && immediatePrice.isNotEmpty()) {
                    uploadProductToServer(prodName, prodInfo, bidPrice, immediatePrice, imageBitmap)
                } else {
                    Toast.makeText(mContext, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(mContext, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun callCamera() {
        if (checkPermission(CAMERA, CAMERA_CODE)) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            Log.d("RegActivity", "Launching camera intent.")
            cameraLauncher.launch(intent)
        } else {
            Log.e("RegActivity", "Camera permission is not granted.")
        }
    }

    fun checkPermission(permissions: Array<String>, type: Int): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    mContext,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("RegActivity", "Requesting permission: $permission")
                ActivityCompat.requestPermissions(mActivity, permissions, type)
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("RegActivity", "Camera permission granted.")
                    callCamera()
                } else {
                    Log.e("RegActivity", "Camera permission denied.")
                    Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
                }
            }

            STORAGE_CODE -> {
                // 다른 권한 처리
            }
        }
    }

    fun encodeImageToBase64(image: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT).replace("\n", "")
    }

    fun uploadProductToServer(prodName: String, prodInfo: String, bidPrice: String, immediatePrice: String, imageBitmap: Bitmap) {
        val base64Image = encodeImageToBase64(imageBitmap)
        Log.d("RegActivity", "Base64 Image: $base64Image")


        val url = "http://192.168.0.23:8089/auction/products/prodRegister"


        val params = HashMap<String, String>()
        params["prodName"] = prodName
        params["prodInfo"] = prodInfo
        params["bidPrice"] = bidPrice
        params["immediatePrice"] = immediatePrice
        params["prodImgPath"] = base64Image

        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", "")

        params["userId"] = userId ?: ""

        val jsonObject = JSONObject(params as Map<*, *>)

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("RegActivity", "Server response: $response")
                Toast.makeText(mContext, "Product registered successfully", Toast.LENGTH_SHORT).show()
                finish()
            },
            Response.ErrorListener { error ->
                Log.e("RegActivity", "Product registration failed: ${error.message}")
                Toast.makeText(mContext, "Product registration failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }

            override fun getBody(): ByteArray {
                return jsonObject.toString().toByteArray()
            }
        }

        queue.add(stringRequest)
    }
}
