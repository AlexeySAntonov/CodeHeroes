package com.aleksejantonov.codeheroes

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

  private var resultTextView: TextView? = null
  private val subscriptions = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setupContentView()
    doChallenge()
  }

  private fun setupContentView() {
    resultTextView = TextView(this).apply {
      layoutParams = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      )
      gravity = Gravity.CENTER
      setBackgroundResource(android.R.color.white)
      textSize = 20f
      typeface = Typeface.create("sans-serif", Typeface.NORMAL)
    }
    resultTextView?.let { setContentView(it) }
  }

  private fun doChallenge() {
    val a = listOf("Vj", "Xj", "Z1", "NV")
    val b = listOf("Q=", "A=", "M=", "g=")

    Observables
      .zip(
        a.toObservable(),
        b.toObservable()
      ) { first, second ->  first + second }
      .map { base64 -> Base64.decode(base64, Base64.DEFAULT) }
      .map { bytes -> String(bytes, Charset.forName("UTF-8")) }
      .flatMap { asciiString -> asciiString.toCharArray().toObservable() }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribeBy(
        onNext = { resultTextView?.text = "${resultTextView?.text} $it".also { Log.d("Result", it) } },
        onError = { Log.e("Oops", it.toString()) }
      )
      .addTo(subscriptions)
  }

  override fun onDestroy() {
    subscriptions.clear()
    super.onDestroy()
  }
}