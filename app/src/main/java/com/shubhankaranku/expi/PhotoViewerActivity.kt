package com.shubhankaranku.expi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.shubhankaranku.expi.Base.BaseActivity
import com.shubhankaranku.expi.Base.Cons
import com.shubhankaranku.expi.Base.PublicMethods
import org.greenrobot.eventbus.EventBus

class PhotoViewerActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_viewer)

        EventBus.getDefault().post("Return")

        if (intent.hasExtra(Cons.IMG_EXTRA_KEY)) {
            val imageView = findViewById<ImageView>(R.id.image)
            val imagePath = intent.getStringExtra(Cons.IMG_EXTRA_KEY)
            imageView.setImageBitmap(PublicMethods.getBitmapByPath(imagePath, Cons.IMG_FILE))
        }
    }

}