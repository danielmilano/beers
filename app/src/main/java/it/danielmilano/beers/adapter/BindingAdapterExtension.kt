package it.danielmilano.beers.adapter

import android.animation.ObjectAnimator
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.request.transition.ViewPropertyTransition

@BindingAdapter("image")
fun loadImage(view: ImageView, url: String?) {
    url?.let {
        val fadeAnimation =
            ViewPropertyTransition.Animator {
                val fadeAnim = ObjectAnimator.ofFloat(it, "alpha", 0f, 1f)
                fadeAnim.duration = 500
                fadeAnim.start()
            }

        Glide.with(view)
            .load(url)
            .transition(GenericTransitionOptions.with(fadeAnimation))
            .into(view)
    }
}
