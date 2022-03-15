package com.leonardosantos.consumirwebapi

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream
import java.sql.Blob
import java.util.*

object  ImageUtilities{
    init{

    }

    fun getByteArrayFromResourse(idImage:Int, content: Context):ByteArray{
        var bitmap = BitmapFactory.decodeResource(content.resources, idImage)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,80, stream)
        return stream.toByteArray()
    }

    fun getByteArrayFromBitmap(bitmap: Bitmap):ByteArray{
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,80, stream)
        return stream.toByteArray()
    }

    fun getByteArrayFromDrawable(drawable: Drawable, content: Context):ByteArray{
        var bitMap =  drawable.toBitmap(drawable.intrinsicWidth,drawable.intrinsicHeight,null)
        val stream = ByteArrayOutputStream()
        bitMap.compress(Bitmap.CompressFormat.JPEG,80, stream)
        return stream.toByteArray()
    }

    fun getBitMapFromByteArray(data:ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(data,0,data.size)
    }

    fun getBitMapFromBase64(base64Image : String): Bitmap {
        val strImage:String =  base64Image.replace("data:image/png;base64,","").replace("data:image/jpeg;base64,","").replace("data:image/png;base64,","").replace("dataimage/jpegbase64","")
        var byteArray = android.util.Base64.decode(strImage, android.util.Base64.DEFAULT)
        var BitmapImage = ImageUtilities.getBitMapFromByteArray(byteArray)

        return BitmapImage
    }

    fun getByteArrayFromBase64(base64Image : String):ByteArray{
        var byteArray = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT)

        return byteArray
    }

}