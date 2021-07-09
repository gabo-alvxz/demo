package com.gabo.finder.item.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gabo.architecture.utils.launch
import com.gabo.finder.common.utils.toNetworkState
import com.gabo.finder.item.repository.ItemRepository
import com.gabo.finder.user.repository.UserRepository
import com.gabo.models.models.item.Item
import com.gabo.models.models.seller.User
import com.gabo.network.transport.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _itemLiveEvent = MutableLiveData<NetworkState<Item>>()

    val itemLiveEvent: LiveData<NetworkState<Item>>
        get() = _itemLiveEvent

    val item: Item?
        get() = (_itemLiveEvent.value as? NetworkState.Success)?.value



    private val _sellerLiveEvent = MutableLiveData<NetworkState<User>>()
    val sellerLiveEvent: LiveData<NetworkState<User>>
        get() = _sellerLiveEvent

    private val _descriptionLiveEvent = MutableLiveData<NetworkState<String>>()
    val descriptionLiveEvent: LiveData<NetworkState<String>>
        get() = _descriptionLiveEvent

    /**
     *  Even though this has the side effect of loading both, the seller and the description,
     *  it's safe to assume we'll need those loaded for the same use case that we need to fetch the item
     */
    fun fetchItem(itemId: String) = launch {
        fetchItemDesctiption(itemId)
        _itemLiveEvent.postValue(NetworkState.Loading)
        _itemLiveEvent.postValue(
            itemRepository.getItem(itemId)
                .alsoDo { result ->
                     result.sellerId?.let {
                         fetchSeller(it)    //Also load the seller information for that item.
                     }
                }
                .toNetworkState()
        )
    }

    fun fetchItemDesctiption(itemId: String) = launch {
        _descriptionLiveEvent.postValue(NetworkState.Loading)
        _descriptionLiveEvent.postValue(
            itemRepository.getItemDescription(itemId)
                .toNetworkState()
        )
    }

    fun fetchSeller(sellerId: String) = launch {
        _sellerLiveEvent.postValue(NetworkState.Loading)
        _sellerLiveEvent.postValue(
            userRepository.getUser(sellerId)
                .toNetworkState()
        )
    }
}