package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.act


//class DownloadActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityDownloadBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityDownloadBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        initData()
//        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                binding.btnHome.performClick()
//            }
//        })
//
//        initClickListeners()
//    }
//
//
//
//    private fun initData() {
//        // 🌟 Ensure this matches Constant.IMAGE_URI ("image_uri") exactly
//        val imageUriString = intent.getStringExtra(Constant.IMAGE_URI)
//            ?: intent.getStringExtra("image_uri") // Fallback check
//
//        if (imageUriString.isNullOrEmpty()) {
//            Log.e("DownloadActivity", "Image URI is null or empty from intent!")
//            return
//        }
//
//        val uri = Uri.parse(imageUriString)
//        Log.d("DownloadActivity", "Loading image URI successfully: $uri")
//
//        Glide.with(this)
//            .asBitmap()
//            .load(uri)
//            .diskCacheStrategy(DiskCacheStrategy.NONE)
//            .skipMemoryCache(true)
//            .placeholder(R.drawable.img_place_holder)
//            .error(R.drawable.img_place_holder)
//            .into(object : CustomTarget<Bitmap>() {
//                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                    binding.imgResult.setImageBitmap(resource)
//                }
//                override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {}
//            })
//    }
//
//
//
//    private fun initClickListeners() {
//        binding.btnBack.setOnClickListener { finish() }
//
//        binding.btnHome.setOnClickListener {
//            val intent = Intent(this, ColoringGalleryActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//            startActivity(intent)
//            finish()
//        }
//
//
//    }
//}