import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdfcreator.databinding.FragmentFromDeviceBinding
import java.io.File


class FromDevice : Fragment() {

    private val PICK_PDF_REQUEST_CODE = 123
    private val pdfFilesList = ArrayList<File>()
    private val pickPdfLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onActivityResult(PICK_PDF_REQUEST_CODE, result.resultCode, result.data)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFromDeviceBinding.inflate(inflater, container, false)
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        startActivityForResult(intent, PICK_PDF_REQUEST_CODE)

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { pdfUri ->
                val pdfFile = getFileFromUri(pdfUri)
                pdfFilesList.add(pdfFile)

                // Open the selected PDF file
                openPdfFile(pdfFile)
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File {
        val contentResolver: ContentResolver = requireActivity().contentResolver
        var displayName: String = ""
        val fileType: String
        val projection =
            arrayOf(MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.MIME_TYPE)

        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
            fileType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))
        }
        val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File(requireActivity().cacheDir, displayName)
        } else {
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            File(downloadsDir, displayName)
        }
        contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun openPdfFile(pdfFile: File) {
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireActivity().packageName}.fileprovider",
            pdfFile
        )
        pdfIntent.setDataAndType(uri, "application/pdf")
        pdfIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        try {
            startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Error opening PDF!", Toast.LENGTH_SHORT).show()
        }
    }
}
