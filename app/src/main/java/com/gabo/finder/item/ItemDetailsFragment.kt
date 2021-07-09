package com.gabo.finder.item

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.gabo.architecture.ui.BaseFragment
import com.gabo.architecture.utils.IntentUtils
import com.gabo.architecture.utils.formatCurrency
import com.gabo.architecture.utils.formatQuantity
import com.gabo.finder.R
import com.gabo.finder.common.adapter.BaseAdapter
import com.gabo.finder.common.adapter.PictureRow
import com.gabo.finder.databinding.ItemDetailFragmentBinding
import com.gabo.finder.item.ui.ItemAttributeRow
import com.gabo.finder.item.ui.ItemAttributeSeeMoreRow
import com.gabo.finder.item.viewModel.ItemViewModel
import com.gabo.models.models.item.Item
import com.gabo.models.models.seller.User
import com.gabo.network.transport.NetworkException
import com.gabo.network.transport.NetworkState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

private const val MAX_ATTRIBUTES_TO_SHOW = 4

@AndroidEntryPoint
class ItemDetailsFragment : BaseFragment<ItemDetailFragmentBinding>() {

    private val viewModel: ItemViewModel by viewModels()

    private val args: ItemDetailsFragmentArgs by navArgs()

    private val item: Item?
        get() = args.item

    private val itemId: String
        get() = args.itemId

    @Inject
    lateinit var intentUtils: IntentUtils

    private val attributesAdapter = BaseAdapter()

    private val imagesAdapter = BaseAdapter()

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.share_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.share) {
            val activity = activity ?:return false
            val shared = (viewModel.item ?: this.item) ?: return false
            intentUtils.share(
                activity,
                R.string.share,
                getString(R.string.share_item_message, shared.url)
            )
            return true
        }
        return false
    }

    override fun createBinding(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?
    ): ItemDetailFragmentBinding {
        return ItemDetailFragmentBinding.inflate(
            layoutInflater,
            viewGroup,
            false
        ).apply {
            setToolbar(toolbar)
            setTitle(item?.title.orEmpty())
            attributesRecyclerview.adapter = attributesAdapter
            attributesRecyclerview.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            attributesRecyclerview.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )

            imageGalleryRecyclerview.adapter = imagesAdapter
            imageGalleryRecyclerview.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            PagerSnapHelper().attachToRecyclerView(imageGalleryRecyclerview)
            val item = item ?: return@apply
            itemTitle.text = item.title
            price.text = item.price.formatCurrency(item.currencyId)
            availableQty.text = getString(
                R.string.search_result_row_availability,
                item.availableQuantity.formatQuantity(
                    resources.getInteger(R.integer.max_item_availability_limit)
                )
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        viewModel.itemLiveEvent.observe(viewLifecycleOwner, {
            when (it) {
                is NetworkState.Success -> onItemLoaded(it.value)
                is NetworkState.Loading -> showLoading()
                is NetworkState.Error -> showNetwokrError(it.error)
            }
        })
        viewModel.descriptionLiveEvent.observe(viewLifecycleOwner, {
            onDescriptionLoaded(it)
        })
        viewModel.sellerLiveEvent.observe(viewLifecycleOwner, {
            onSellerLoaded(it)
        })
        loadItem()
    }

    /**
     *  Make sure to fetch the item just the first time the user opens the details screen. We need
     *  to make sure we get the correct item.
     *  An alternative to this could have been to pass the itemId as a parameter to the ViewModel
     *  constructor, however, that would require Assisted Inject  which is not fully integrated with Hilt yet.
     */
    private fun loadItem(){
        if(viewModel.item?.id != itemId){
            viewModel.fetchItem(itemId)
        }
    }

    private fun showLoading() {
        binding?.stateView?.showLoading()
    }

    private fun showNetwokrError(e: NetworkException) {
        Timber.e(e, "Error loading item")
        binding?.stateView?.showError {
            viewModel.fetchItem(itemId)
        }
    }

    private fun onDescriptionLoaded(description: NetworkState<String>) {
        if (description is NetworkState.Success) {
            showDescription(description.value)
        }
    }

    private fun onSellerLoaded(sellerState: NetworkState<User>) {
        if (sellerState is NetworkState.Success) {
            binding?.sellerView?.seller = sellerState.value
        }
    }

    private fun displayImagesCarousel(item : Item) {
        item.pictures?:return
        imagesAdapter.clear()
        imagesAdapter.addAll(
            item.pictures!!.map {
                PictureRow(it)
            }
        )
    }


    private fun onItemLoaded(item: Item) {
        val binding = binding ?: return
        binding.stateView.showContent()
        setTitle(item.title)
        binding.itemTitle.text = item.title
        binding.availableQty.text = getString(
            R.string.search_result_row_availability,
            item.availableQuantity.formatQuantity()
        )
        binding.price.text = item.price.formatCurrency(item.currencyId)
        binding.buyButton.setOnClickListener {
            val activity = activity?:return@setOnClickListener
            intentUtils.openInBrowser(item.url, activity)
        }
        displayAttributes(item)
        displayImagesCarousel(item)
    }

    private fun showDescription(description : String?){
        val binding = binding ?: return
        binding.descriptionTitle.isVisible = !description.isNullOrEmpty()
        binding.itemDescription.isVisible = !description.isNullOrEmpty()
        binding.itemDescription.text = description
    }

    private fun displayAttributes(item: Item) {
        attributesAdapter.clear()
        val attrs = if (item.attributes.size > MAX_ATTRIBUTES_TO_SHOW) {
            item.attributes.slice(0 until MAX_ATTRIBUTES_TO_SHOW )
        } else {
            item.attributes
        }.map {
            ItemAttributeRow(it)
        }
        attributesAdapter.addAll(attrs)
        if (item.attributes.size > MAX_ATTRIBUTES_TO_SHOW) {
            attributesAdapter.add(
                ItemAttributeSeeMoreRow(
                    count = item.attributes.size,
                    onClick = {
                        attributesAdapter.clear()
                        attributesAdapter.addAll(item.attributes.map { ItemAttributeRow(it) })
                    }
                )
            )
        }

    }


}