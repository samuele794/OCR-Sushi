package it.github.samuele794.sushisigner.view.camera.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import it.github.samuele794.sushisigner.databinding.FragmentCameraDrawBinding
import it.github.samuele794.sushisigner.utils.viewBinding

class CameraDrawFragment: Fragment() {
    private val viewBinding by viewBinding(FragmentCameraDrawBinding::inflate)
    private val cameraArgs by navArgs<CameraDrawFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding.imageCameraAcquired.load(cameraArgs.imageUri)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}