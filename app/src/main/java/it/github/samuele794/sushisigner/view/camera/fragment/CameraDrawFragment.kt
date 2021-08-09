package it.github.samuele794.sushisigner.view.camera.fragment

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import it.github.samuele794.sushisigner.R
import it.github.samuele794.sushisigner.databinding.FragmentCameraDrawBinding
import it.github.samuele794.sushisigner.utils.viewBinding
import it.github.samuele794.sushisigner.viewmodel.camera.CameraDrawViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class CameraDrawFragment : Fragment() {
    private val viewBinding by viewBinding(FragmentCameraDrawBinding::inflate)
    private val cameraArgs by navArgs<CameraDrawFragmentArgs>()
    private val viewModel: CameraDrawViewModel by viewModel()

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

        viewModel.getDrawDataState().observe(viewLifecycleOwner) { state ->
            renderViewState(state)
        }

        viewBinding.saveButton.setOnClickListener {
            viewModel.saveSigner(
                viewBinding.headerIL.editText?.text.toString(),
                viewBinding.bodyIL.editText?.text.toString()
            )
            viewBinding.root.transitionToState(R.id.cardDismissEnd)
            viewBinding.root.addTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) = Unit

                override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) =
                    Unit

                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                    viewBinding.root.removeTransitionListener(this)
                    renderViewState(
                        CameraDrawViewModel.AcquisitionState.StartAcquisitionState(
                            CameraDrawViewModel.AcquisitionState.TextStateHeader()
                        )
                    )
                }

                override fun onTransitionTrigger(
                    p0: MotionLayout?,
                    p1: Int,
                    p2: Boolean,
                    p3: Float
                ) = Unit

            })
        }

    }

    private fun renderViewState(state: CameraDrawViewModel.AcquisitionState) {
        when (state) {
            is CameraDrawViewModel.AcquisitionState.StartAcquisitionState -> {
                viewBinding.root.transitionToState(R.id.cardTransitionShowStart)
                viewBinding.drawView.clearDraw()
                viewBinding.drawView.enableDraw()
                renderViewState(state.nextState)
            }

            is CameraDrawViewModel.AcquisitionState.TextStateBody -> {
                viewBinding.drawView.setOnDrawEndlistener { pointList ->
                    viewModel.processState(
                        state.apply {
                            imageData = viewBinding.imageCameraAcquired.drawable as BitmapDrawable
                            screenPointList = pointList
                        }
                    )
                }
            }

            is CameraDrawViewModel.AcquisitionState.TextStateHeader -> {
                viewBinding.drawView.setOnDrawEndlistener { pointList ->
                    viewModel.processState(
                        state.apply {
                            imageData = viewBinding.imageCameraAcquired.drawable as BitmapDrawable
                            screenPointList = pointList
                        }
                    )
                }
            }

            is CameraDrawViewModel.AcquisitionState.CardDataEditState -> {
                viewBinding.root.transitionToState(R.id.cardTransitionShowEnd)
                viewBinding.cardTextAcquired.visibility = VISIBLE
                viewBinding.headerIL.editText?.setText(state.textHeader)
                viewBinding.bodyIL.editText?.setText(state.textBody)
            }
        }
    }
}