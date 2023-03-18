package com.riteshmaagadh.cms.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.widget.Toast
import com.riteshmaagadh.cms.data.models.Category

object Utils {

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
            .isConnected
    }

    fun openWhatsapp(context: Context, groupLink: String) {
        try {
            val list = context.packageManager.getLaunchIntentForPackage("com.whatsapp")
            if (list != null) {
                val intentWhatsAppGroup = Intent(Intent.ACTION_VIEW)
                val uri: Uri = Uri.parse(groupLink)
                intentWhatsAppGroup.data = uri
                intentWhatsAppGroup.setPackage("com.whatsapp")
                context.startActivity(intentWhatsAppGroup)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(context,"WhatsApp not installed!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun getCategories() : List<Category> {
        val categories: MutableList<Category> = mutableListOf()
        categories.add(Category("Fitness","https://firebasestorage.googleapis.com/v0/b/small-projects-7b158.appspot.com/o/pexels-pixabay-40751.jpg?alt=media&token=ce6ea83f-f4c4-4a68-80ae-6631e21ad9ff"))
        categories.add(Category("Education","https://firebasestorage.googleapis.com/v0/b/small-projects-7b158.appspot.com/o/pexels-pixabay-159711.jpg?alt=media&token=11608ed2-0231-4d70-80c7-b2c35a50dd79"))
        categories.add(Category("News","https://firebasestorage.googleapis.com/v0/b/small-projects-7b158.appspot.com/o/pexels-cottonbro-3944454.jpg?alt=media&token=05039d44-e959-4e5f-973c-25bd631eff06"))
        categories.add(Category("Others","https://firebasestorage.googleapis.com/v0/b/small-projects-7b158.appspot.com/o/OTHERS.png?alt=media&token=32ce4422-b817-4c47-a831-f55bd3f96fab"))
        categories.add(Category("Funny","https://firebasestorage.googleapis.com/v0/b/small-projects-7b158.appspot.com/o/pexels-pixabay-207983.jpg?alt=media&token=40cdc92e-61b3-4fd7-92cc-4191fbdd7779"))
        categories.add(Category("Gaming","https://firebasestorage.googleapis.com/v0/b/small-projects-7b158.appspot.com/o/pexels-jeshootscom-442576.jpg?alt=media&token=c172ae3f-8dfa-41a9-8f8c-7a301cb9756f"))
        categories.add(Category("Photography","https://firebasestorage.googleapis.com/v0/b/small-projects-7b158.appspot.com/o/pexels-andre-furtado-1264210.jpg?alt=media&token=05445729-3c0b-4d69-aa3b-486fda5adbb9"))
        categories.add(Category("Sports","https://firebasestorage.googleapis.com/v0/b/small-projects-7b158.appspot.com/o/pexels-pixabay-209977.jpg?alt=media&token=8be941f7-ce85-4f60-9a17-0a1b701b38d6"))
        categories.add(Category("Dating","https://firebasestorage.googleapis.com/v0/b/small-projects-7b158.appspot.com/o/pexels-josh-hild-4606770.jpg?alt=media&token=7e862ecc-2844-47ef-a068-1bfeeb9d78d7"))
        categories.add(Category("Friendship","https://firebasestorage.googleapis.com/v0/b/small-projects-7b158.appspot.com/o/pexels-min-an-853168.jpg?alt=media&token=1edf78dd-82a3-44d5-a064-d1af0dd0f81c"))
        categories.add(Category("Technology","https://firebasestorage.googleapis.com/v0/b/small-projects-7b158.appspot.com/o/pexels-thisisengineering-3862632.jpg?alt=media&token=a6054320-16ca-4e17-9bd3-526f4a73a666"))
        categories.add(Category("Buy & Sell","https://firebasestorage.googleapis.com/v0/b/small-projects-7b158.appspot.com/o/pexels-karolina-grabowska-5632381.jpg?alt=media&token=4b77a81a-869b-4e2e-b051-70419422acbb"))
        return categories
    }

}