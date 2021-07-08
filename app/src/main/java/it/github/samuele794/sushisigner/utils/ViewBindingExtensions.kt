package it.github.samuele794.sushisigner.utils

import android.os.Looper
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified T : ViewBinding> AppCompatActivity.viewBinding(noinline initializer: (LayoutInflater) -> T) =
    ViewBindingActivityPropertyDelegate(this, initializer)

inline fun <reified T : ViewBinding> Fragment.viewBinding(noinline initializer: (LayoutInflater) -> T) =
    ViewBindingFragmentPropertyDelegate(this, initializer)

class ViewBindingActivityPropertyDelegate<T : ViewBinding>(
    private val activity: AppCompatActivity,
    private val initializer: (LayoutInflater) -> T
) : ReadOnlyProperty<AppCompatActivity, T>, LifecycleObserver {

    private var _value: T? = null

    init {
        activity.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    @Suppress("Unused")
    fun onCreate() {
        if (_value == null) {
            _value = initializer(activity.layoutInflater)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    @Suppress("Unused")
    fun onDestroy(){
        _value = null
    }

    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        if (_value == null) {

            // This must be on the main thread only
            if (Looper.myLooper() != Looper.getMainLooper()) {
                throw IllegalThreadStateException("This cannot be called from other threads. It should be on the main thread only.")
            }

            _value = initializer(thisRef.layoutInflater)
        }
        return _value!!
    }
}

class ViewBindingFragmentPropertyDelegate<T : ViewBinding>(
    private val fragment: Fragment,
    private val initializer: (LayoutInflater) -> T
) : ReadOnlyProperty<Fragment, T>, LifecycleObserver {

    private var _value: T? = null

    init {
        fragment.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    @Suppress("Unused")
    fun onCreate() {
        if (_value == null) {
            _value = initializer(fragment.layoutInflater)
        }
        fragment.lifecycle.removeObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    @Suppress("Unused")
    fun onDestroy(){
        _value = null
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        if (_value == null) {

            // This must be on the main thread only
            if (Looper.myLooper() != Looper.getMainLooper()) {
                throw IllegalThreadStateException("This cannot be called from other threads. It should be on the main thread only.")
            }

            _value = initializer(thisRef.layoutInflater)
        }
        return _value!!
    }
}