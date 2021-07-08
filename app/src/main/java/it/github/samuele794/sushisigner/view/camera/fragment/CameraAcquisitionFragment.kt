package it.github.samuele794.sushisigner.view.camera.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import it.github.samuele794.sushisigner.databinding.FragmentCameraAcquisitionBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import it.github.samuele794.sushisigner.utils.viewBinding
import kotlinx.coroutines.runBlocking
import java.io.File

class CameraAcquisitionFragment : Fragment() {

    private val viewBinding by viewBinding(FragmentCameraAcquisitionBinding::inflate)
    private lateinit var cameraExecutor: ExecutorService
    private var requestPermissionLauncher: ActivityResultLauncher<Array<String>>? = null

    private var imageCapture: ImageCapture? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.fabPhotoTaker.setOnClickListener {
            takePhoto()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        checkPermission()
    }


    override fun onDestroyView() {
        cameraExecutor.shutdown()
        super.onDestroyView()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            getOutputDirectory(),
            "$CACHE_PHOTO_FILE_NAME.jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(
                        CameraAcquisitionFragment::class.simpleName,
                        "Photo capture failed: ${exc.message}",
                        exc
                    )
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d(CameraAcquisitionFragment::class.simpleName, msg)

                    findNavController()
                        .navigate(
                            CameraAcquisitionFragmentDirections
                                .actionCameraAcquisitionFragmentToCameraDrawFragment(savedUri)
                        )
                }
            })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.cameraPreviewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()


            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            runCatching {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun checkPermission() {
        if (allPermissionsGranted()) {
            startCamera()
            return
        }

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                requestPermissionLauncher?.unregister()
                if (it.all { it.value }) {
                    startCamera()
                } else {
                    //TODO Dialog Spiegazione perchè me serve la camera
                }
            }



        when {
            allPermissionsGranted() -> {
                startCamera()
            }

            allShouldShowRequestPermissionRationale() -> {
                //TODO Dialog Spiegazione perchè me serve la camera
            }

            else -> {
                requestPermissionLauncher?.launch(REQUIRED_PERMISSIONS)
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun allShouldShowRequestPermissionRationale() = REQUIRED_PERMISSIONS.all {
        shouldShowRequestPermissionRationale(it)
    }


    private fun getOutputDirectory(): File {
        return requireContext().cacheDir
    }

    companion object {
        private val CACHE_PHOTO_FILE_NAME = "SushiImageCache"
        private val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}