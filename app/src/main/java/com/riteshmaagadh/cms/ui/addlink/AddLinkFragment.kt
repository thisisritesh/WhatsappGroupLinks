package com.riteshmaagadh.cms.ui.addlink

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.riteshmaagadh.cms.R
import com.riteshmaagadh.cms.data.models.Group
import com.riteshmaagadh.cms.data.models.Index
import com.riteshmaagadh.cms.databinding.FragmentAddLinkBinding
import com.riteshmaagadh.cms.ui.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.net.URI
import java.net.URISyntaxException


class AddLinkFragment : Fragment() {

    private lateinit var binding: FragmentAddLinkBinding
    private var imageUrl = ""
    private lateinit var dialog: AlertDialog
    private var currentIndex: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddLinkBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()


        lifecycleScope.launch(Dispatchers.IO) {
            FirebaseFirestore.getInstance()
                .collection("whatsapp_indexes")
                .document("ECiL6ApsUOmPIZsSy1dB")
                .get()
                .addOnSuccessListener {
                    val index = it.toObject(Index::class.java)
                    currentIndex = index?.current_index!!
                }
        }

        binding.addGroupBtn.setOnClickListener {
            if (Utils.isOnline(requireContext())){
                if (binding.groupNameEditText.text!!.isNotEmpty()){
                    if (isValidGroupLink(binding.groupLinkEditText.text!!.toString())){
                        if (imageUrl.isNotEmpty()) {
                            addGroupRequest(
                                binding.groupNameEditText.text.toString(),
                                binding.groupLinkEditText.text.toString(),
                                binding.categoriesSpinner.selectedItem.toString()
                            )
                        } else {
                            Toast.makeText(requireContext(),"Please upload an image!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        binding.groupLinkInputLayout.error = "Invalid group link!"
                    }
                } else {
                    binding.groupNameInputLayout.error = "Group name is required!"
                }
            } else {
                Snackbar.make(requireContext(),binding.root,"No Internet!", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.addGroupBtn.isEnabled = isChecked
        }

        binding.privacyPolicyTv.setOnClickListener {
            val url = "https://activegrouplinkswhatsapp.blogspot.com/2022/11/active-group-links-whatsapp.html"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        binding.imageView.setOnClickListener {
            chooseImage()
        }

        binding.uploadPicTv.setOnClickListener {
            chooseImage()
        }

        binding.downArrow.setOnClickListener {
            binding.categoriesSpinner.performClick()
        }

    }

    private fun isValidGroupLink(url: String) : Boolean {
        var isValid = false
        if (Patterns.WEB_URL.matcher(url).matches()){
            try {
                isValid = getUrlDomain(url) == "chat.whatsapp.com"
            } catch (e: Exception){
                e.printStackTrace()
            }
        } else {
            isValid = false
        }
        return isValid
    }

    @Throws(URISyntaxException::class)
    fun getUrlDomain(url: String?): String {
        val uri = URI(url)
        val domain: String = uri.host
        val domainArray = domain.split("\\.").toTypedArray()
        return if (domainArray.size == 1) {
            domainArray[0]
        } else domainArray[domainArray.size - 2] + "." + domainArray[domainArray.size - 1]
    }

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1001)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, dataIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, dataIntent)
        var path: Uri? = null
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            if (dataIntent == null || dataIntent.data == null) {
                return
            }
            path = dataIntent.data
            try {
                dialog.show()
                if (path != null) {
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(requireContext().contentResolver, path)
                    Glide.with(requireContext())
                        .load(bitmap)
                        .placeholder(R.drawable.picture)
                        .into(binding.imageView)
                    lifecycleScope.launch(Dispatchers.IO) {
                        val boas = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20,boas)
                        val ref = FirebaseStorage.getInstance()
                            .reference.child("WhatsAppGroup" + System.currentTimeMillis() + ".jpg")
                        ref.putBytes(boas.toByteArray())
                            .addOnSuccessListener {
                                ref.downloadUrl.addOnSuccessListener {
                                    imageUrl = it.toString()
                                    dialog.dismiss()
                                }
                            }
                            .addOnFailureListener {
                                dialog.dismiss()
                                Toast.makeText(requireContext(),"Failed to upload image! Try Again.",Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun addGroupRequest(title: String, groupLink: String, category: String) {
        currentIndex += 1
        val group = Group("", title, groupLink, category,imageUrl, false, currentIndex)
        lifecycleScope.launch(Dispatchers.IO){
            FirebaseFirestore.getInstance()
                .collection("whatsapp_groups")
                .add(group)
                .addOnSuccessListener {
                    resetImageUrl()
                    updateIndex()
                    showSuccessDialog()
                }
                .addOnFailureListener {
                    Snackbar.make(requireContext(),binding.root,"Failed. Try Again!", Snackbar.LENGTH_SHORT).show()
                }
        }
    }

    private fun resetImageUrl() {
        imageUrl = ""
        Glide.with(requireContext())
            .load(R.drawable.add_image)
            .into(binding.imageView)
    }

    private fun updateIndex() {
        lifecycleScope.launch(Dispatchers.IO) {
            FirebaseFirestore.getInstance()
                .collection("whatsapp_indexes")
                .document("ECiL6ApsUOmPIZsSy1dB")
                .update("current_index", currentIndex)
        }
    }

    private fun showSuccessDialog(){
        val layoutInflater = LayoutInflater.from(requireContext());
        val view = layoutInflater.inflate(R.layout.added_confirmation_dialog, null)

        val builder = AlertDialog.Builder(requireContext())
        val alertDialog = builder.create()
        alertDialog.setView(view)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        val yesAction: Button = view.findViewById(R.id.yesButton)

        yesAction.setOnClickListener {
            binding.groupNameEditText.setText("")
            binding.groupLinkEditText.setText("")
            binding.groupLinkInputLayout.error = null
            binding.groupNameInputLayout.error = null
            alertDialog.dismiss()
        }


    }


    companion object {
        @JvmStatic
        fun newInstance() =
            AddLinkFragment()
    }
}