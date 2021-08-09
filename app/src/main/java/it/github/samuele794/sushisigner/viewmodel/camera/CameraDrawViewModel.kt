package it.github.samuele794.sushisigner.viewmodel.camera

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizerOptions
import it.github.samuele794.sushisigner.data.model.Signer
import it.github.samuele794.sushisigner.data.repository.SignerRepository
import it.github.samuele794.sushisigner.utils.onFailure
import it.github.samuele794.sushisigner.utils.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CameraDrawViewModel(private val signerRepository: SignerRepository) : ViewModel() {
    //https://thoughtbot.com/blog/finite-state-machines-android-kotlin-good-times

    private val mDrawStateData: MutableLiveData<AcquisitionState> by lazy {
        MutableLiveData<AcquisitionState>()
    }.also {
        processState(AcquisitionState.StartAcquisitionState(AcquisitionState.TextStateHeader()))
    }

    private var textHeader = ""
    private var textBody = ""

    fun getDrawDataState(): LiveData<AcquisitionState> = mDrawStateData


    fun processState(state: AcquisitionState) {
        viewModelScope.launch(Dispatchers.IO) {
            when (state) {
                is AcquisitionState.TextStateBody -> {
                    val imageData = state.imageData
                    val screenPointList = state.screenPointList

                    if (imageData != null && screenPointList != null) {
                        val result = calculateTextByBitmap(imageData, screenPointList)
                        result.onSuccess {
                            textBody = it.text
                            mDrawStateData.postValue(
                                AcquisitionState.CardDataEditState(textHeader, textBody)
                            )
                        }.onFailure {
                            it
                        }
                    } else {
                        //TODO FAIULRE
                    }
                }
                is AcquisitionState.TextStateHeader -> {
                    val imageData = state.imageData
                    val screenPointList = state.screenPointList

                    if (imageData != null && screenPointList != null) {
                        val result = calculateTextByBitmap(imageData, screenPointList)
                        result.onSuccess {
                            textHeader = it.text
                            mDrawStateData.postValue(
                                AcquisitionState.StartAcquisitionState(
                                    AcquisitionState.TextStateBody()
                                )
                            )
                        }.onFailure {
                            it
                        }
                    } else {
                        //TODO FAIULRE
                    }

                }
                is AcquisitionState.StartAcquisitionState -> {
                    mDrawStateData.postValue(state)
                }
            }
        }
    }


    private suspend inline fun calculateTextByBitmap(
        baseBitmapDrawable: BitmapDrawable,
        pointAreaList: List<Point>,
    ) = SuspendableResult.of<Text, Exception> {
        val minX = pointAreaList.minByOrNull { it.x }
        val maxX = pointAreaList.maxByOrNull { it.x }
        val minY = pointAreaList.minByOrNull { it.y }
        val maxY = pointAreaList.maxByOrNull { it.y }

        val rect = Rect(
            minX?.x ?: 0,
            minY?.y?.minus(75) ?: 0,
            maxX?.x ?: 0,
            maxY?.y ?: 0
        )

        val bitRes = Bitmap.createBitmap(
            baseBitmapDrawable.bitmap,
            minX?.x?.minus(100) ?: 0,
            minY?.y?.minus(50) ?: 0,
            (rect.right - rect.left) + 75,
            (rect.bottom - rect.top) + 25,
            null,
            false
        )

        val image = InputImage.fromBitmap(bitRes, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image).await()
    }

    fun saveSigner(headerText: String, bodyText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            signerRepository.saveSigner(
                Signer(title = headerText, body = bodyText)
            )
        }
    }

    sealed class AcquisitionState {
        class StartAcquisitionState(val nextState: AcquisitionState) : AcquisitionState()
        open class TextStateHeader(
            var imageData: BitmapDrawable? = null,
            var screenPointList: List<Point>? = null
        ) : AcquisitionState()

        class TextStateBody(
            var imageData: BitmapDrawable? = null,
            var screenPointList: List<Point>? = null
        ) : AcquisitionState()

        class CardDataEditState(
            val textHeader: String,
            val textBody: String
        ) : AcquisitionState()
    }
}