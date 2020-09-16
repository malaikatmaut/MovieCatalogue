package com.ryanrvldo.moviecatalogue.ui.detailimport android.content.Intentimport android.net.Uriimport android.os.Bundleimport android.view.Menuimport android.view.MenuItemimport android.view.Viewimport android.widget.ImageViewimport android.widget.TextViewimport android.widget.Toastimport androidx.activity.viewModelsimport androidx.core.content.ContextCompatimport com.bumptech.glide.Glideimport com.bumptech.glide.load.resource.drawable.DrawableTransitionOptionsimport com.bumptech.glide.request.RequestOptionsimport com.google.android.material.appbar.AppBarLayoutimport com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListenerimport com.google.android.material.snackbar.Snackbarimport com.ryanrvldo.moviecatalogue.BuildConfigimport com.ryanrvldo.moviecatalogue.Rimport com.ryanrvldo.moviecatalogue.adapter.BackdropSlideAdapterimport com.ryanrvldo.moviecatalogue.adapter.CastAdapterimport com.ryanrvldo.moviecatalogue.adapter.SimilarAdapterimport com.ryanrvldo.moviecatalogue.data.model.Castimport com.ryanrvldo.moviecatalogue.data.model.Imageimport com.ryanrvldo.moviecatalogue.data.model.Movieimport com.ryanrvldo.moviecatalogue.data.model.Similarimport com.ryanrvldo.moviecatalogue.data.remote.response.VideosResponseimport com.ryanrvldo.moviecatalogue.data.vo.Statusimport com.ryanrvldo.moviecatalogue.databinding.ActivityMovieDetailBindingimport com.ryanrvldo.moviecatalogue.di.MovieQualifierimport com.ryanrvldo.moviecatalogue.ui.viewmodel.MovieDetailsViewModelimport com.ryanrvldo.moviecatalogue.utils.BaseAppCompatActivityimport com.ryanrvldo.moviecatalogue.utils.LayoutManagerUtil.getHorizontalLayoutManagerimport com.ryanrvldo.moviecatalogue.utils.StringUtils.getGenresimport com.ryanrvldo.moviecatalogue.utils.StringUtils.getReleaseDateimport com.ryanrvldo.moviecatalogue.utils.StringUtils.getRuntimeimport com.ryanrvldo.moviecatalogue.widget.FavoriteMovieWidget.Companion.updateWidgetimport com.smarteist.autoimageslider.SliderAnimationsimport dagger.hilt.android.AndroidEntryPointimport java.util.*import javax.inject.Inject@AndroidEntryPointclass MovieDetailsActivity : BaseAppCompatActivity() {    private lateinit var binding: ActivityMovieDetailBinding    private val viewModel: MovieDetailsViewModel by viewModels()    @Inject    lateinit var castAdapter: CastAdapter    @MovieQualifier    @Inject    lateinit var similarAdapter: SimilarAdapter    private lateinit var movie: Movie    private var favorite = false    private lateinit var msg: String    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        binding = ActivityMovieDetailBinding.inflate(layoutInflater)        setContentView(binding.root)        setupToolbar()        setupRecyclerViews()        observeData()    }    override fun onCreateOptionsMenu(menu: Menu): Boolean {        menuInflater.inflate(R.menu.details_menu, menu)        super.onCreateOptionsMenu(menu)        return true    }    override fun onPrepareOptionsMenu(menu: Menu): Boolean {        if (favorite) {            menu.findItem(R.id.favorite).setIcon(R.drawable.ic_favorite)        } else {            menu.findItem(R.id.favorite).setIcon(R.drawable.ic_favorite_border)        }        super.onPrepareOptionsMenu(menu)        return true    }    override fun onOptionsItemSelected(item: MenuItem): Boolean {        if (item.itemId == R.id.favorite) {            if (favorite) {                deleteFavorite(item)            } else {                addFavorite(item)            }        }        return super.onOptionsItemSelected(item)    }    override fun onSupportNavigateUp(): Boolean {        onBackPressed()        return true    }    private fun setupToolbar() {        setSupportActionBar(binding.toolbarDetail)        supportActionBar?.setDisplayHomeAsUpEnabled(true)        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_dark)        binding.appBar.addOnOffsetChangedListener(object : OnOffsetChangedListener {            var isShow = true            var scrollRange = -1            override fun onOffsetChanged(                appBarLayout: AppBarLayout,                verticalOffset: Int            ) {                if (scrollRange == -1) {                    scrollRange = appBarLayout.totalScrollRange                }                if (scrollRange + verticalOffset == 0) {                    binding.collapsingToolbar.title = movie.title                    binding.cardBanner.visibility = View.GONE                    isShow = true                } else if (isShow) {                    binding.collapsingToolbar.title = " "                    binding.cardBanner.visibility = View.VISIBLE                    isShow = false                }            }        })    }    private fun setupRecyclerViews() {        binding.castRv.layoutManager = getHorizontalLayoutManager(this)        binding.castRv.adapter = castAdapter        binding.similarRv.layoutManager = getHorizontalLayoutManager(this)        binding.similarRv.adapter = similarAdapter    }    private fun observeData() {        viewModel.movieDetails.observe(this) { resource ->            when (resource.status) {                Status.SUCCESS -> {                    resource.data?.let { movie ->                        setMovieDetails(movie)                        setBackdropsSlider(movie.images?.backdrops)                        setMovieVideos(movie.videos)                        setCastList(movie.credits?.cast)                        setSimilarList(movie.similar?.similar)                        showLoading(false)                    }                }                Status.ERROR -> {                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()                    showLoading(false)                }                Status.LOADING -> showLoading(true)            }        }        viewModel.isFavorites.observe(this) {            if (it) {                favorite = true                invalidateOptionsMenu()            }        }    }    private fun setMovieDetails(movie: Movie) {        this.movie = movie        binding.movieDetailsTitle.text = this.movie.title        binding.movieDetailsOverview.text = this.movie.overview        binding.ratingText.text = this.movie.rating.toString()        binding.ratingCount.text = String.format(            Locale.US,            "%,d",            this.movie.ratingVotes        )        binding.movieDetailsDuration.text = getRuntime(this.movie.duration)        binding.movieDetailsGenres.text = getGenres(this.movie.genres)        getReleaseDate(this.movie.releaseDate)?.let {            binding.movieDetailsReleaseDate.text = it        }        Glide.with(this)            .load(BuildConfig.TMDB_IMAGE_342 + this.movie.posterPath)            .error(R.drawable.ic_undraw_404)            .apply(RequestOptions.placeholderOf(R.drawable.ic_undraw_images).centerCrop())            .transition(DrawableTransitionOptions.withCrossFade())            .into(binding.movieDetailsPoster)    }    private fun setBackdropsSlider(imageItems: List<Image>?) {        imageItems?.let {            binding.imageSlider.sliderAdapter = BackdropSlideAdapter(imageItems)            binding.imageSlider.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION)            binding.imageSlider.setIndicatorVisibility(false)        }    }    private fun setMovieVideos(videosResponse: VideosResponse?) {        binding.movieTrailers.removeAllViews()        if (videosResponse != null) {            if (videosResponse.videos.isEmpty()) {                binding.trailersLabel.visibility = View.GONE                binding.movieTrailers.visibility = View.GONE            } else {                for ((key, name) in videosResponse.videos) {                    val parent = layoutInflater.inflate(                        R.layout.item_trailer,                        binding.movieTrailers,                        false                    )                    val thumbnailTrailer =                        parent.findViewById<ImageView>(R.id.thumbnail_trailer)                    val movieTrailerTitle =                        parent.findViewById<TextView>(R.id.trailerTitle)                    movieTrailerTitle.text = name                    Glide.with(this)                        .load(String.format(BuildConfig.YOUTUBE_THUMBNAIL_URL, key))                        .apply(RequestOptions.placeholderOf(R.color.colorPrimary).centerCrop())                        .into(thumbnailTrailer)                    thumbnailTrailer.requestLayout()                    thumbnailTrailer.setOnClickListener {                        startActivity(                            Intent(                                Intent.ACTION_VIEW,                                Uri.parse(String.format(BuildConfig.YOUTUBE_VIDEO_URL, key))                            )                        )                    }                    binding.movieTrailers.addView(parent)                }            }        } else {            showError()            binding.trailersLabel.visibility = View.GONE            binding.movieTrailers.visibility = View.GONE        }    }    private fun setCastList(castList: List<Cast>?) {        if (castList?.isNotEmpty()!!) {            castAdapter.differ.submitList(castList)        } else {            binding.castLabel.visibility = View.GONE            binding.castRv.visibility = View.GONE        }    }    private fun setSimilarList(similarList: List<Similar>?) {        similarList?.let {            if (similarList.isNotEmpty()) {                similarAdapter.setSimilarList(similarList)            } else {                binding.similarLabel.visibility = View.GONE                binding.similarRv.visibility = View.GONE            }        }    }    private fun showLoading(state: Boolean) {        if (state) {            binding.backgroundLoading.visibility = View.VISIBLE            binding.progressBar.visibility = View.VISIBLE            binding.appBar.visibility = View.GONE            binding.constraint.visibility = View.GONE        } else {            binding.backgroundLoading.visibility = View.GONE            binding.progressBar.visibility = View.GONE            binding.appBar.visibility = View.VISIBLE            binding.constraint.visibility = View.VISIBLE        }    }    private fun showError() {        Toast.makeText(this, "Check your internet connection.", Toast.LENGTH_SHORT).show()    }    private fun addFavorite(item: MenuItem) {        viewModel.addFavoriteMovie(movie)        favorite = true        item.setIcon(R.drawable.ic_favorite)        msg = movie.title + " " + getString(R.string.add_favorite_movie)        val snackbar = Snackbar            .make(binding.root, msg, Snackbar.LENGTH_LONG)            .setAction(R.string.undo) {                deleteFavorite(item)                msg = movie.title + " " + getString(R.string.delete_favorite_movie)                val snackbarUndo =                    Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)                        .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))                        .setBackgroundTint(ContextCompat.getColor(this, R.color.colorWhite))                        .setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimaryLight))                snackbarUndo.show()            }            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))            .setBackgroundTint(ContextCompat.getColor(this, R.color.colorWhite))            .setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent))        snackbar.show()        updateWidget(this)    }    private fun deleteFavorite(item: MenuItem) {        viewModel.deleteFavMovie(movie)        favorite = false        item.setIcon(R.drawable.ic_favorite_border)        msg = movie.title + " " + getString(R.string.delete_favorite_movie)        val snackbar = Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)            .setAction(R.string.undo) {                addFavorite(item)                msg = movie.title + " " + getString(R.string.add_favorite_movie)                val snackbarUndo =                    Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)                        .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))                        .setBackgroundTint(ContextCompat.getColor(this, R.color.colorWhite))                        .setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimaryLight))                snackbarUndo.show()            }            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))            .setBackgroundTint(ContextCompat.getColor(this, R.color.colorWhite))            .setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimaryLight))        snackbar.show()        updateWidget(this)    }}