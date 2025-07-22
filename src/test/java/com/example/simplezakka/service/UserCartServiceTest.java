
package com.example.simplezakka.service;

import com.example.simplezakka.dto.cart.CartDto;
import com.example.simplezakka.dto.cart.CartGuest;
import com.example.simplezakka.entity.Cart;
import com.example.simplezakka.entity.CartItemEntity;
import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.DbCartrepository;
import com.example.simplezakka.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserCartServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DbCartrepository dbCartRepository;

    @InjectMocks
    private UserCartService userCartService;

    private CartDto cartDto;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartDto = new CartDto();
        cartDto.setUserId(1);

        product = new Product();
        product.setProductId(100);
        product.setName("商品A");
        product.setPrice(1000);
        product.setImageUrl("image.jpg");
    }

    @Test
    @DisplayName("DBにカートが存在しない場合、空のカートを返す")
    void getCartFromDb_WhenCartNotExist_ReturnsEmptyCart() {
        when(dbCartRepository.findByUserId(1)).thenReturn(Optional.empty());

        CartGuest result = userCartService.getCartFromDb(cartDto);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1);
        assertThat(result.getItems()).isEmpty();
        assertThat(result.getTotalPrice()).isZero();
        assertThat(result.getTotalQuantity()).isZero();
    }

    @Test
    @DisplayName("商品をカートに追加できる")
    void addItemToUserCart_Success() {
        when(productRepository.findById(100)).thenReturn(Optional.of(product));
        when(dbCartRepository.findByUserId(1)).thenReturn(Optional.empty());

        CartGuest result = userCartService.addItemToUserCart(100, 2, cartDto);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get("100").getQuantity()).isEqualTo(2);
        verify(dbCartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("存在しない商品IDでカート追加するとnullを返す")
    void addItemToUserCart_WhenProductNotFound_ReturnsNull() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        CartGuest result = userCartService.addItemToUserCart(999, 2, cartDto);

        assertThat(result).isNull();
        verify(dbCartRepository, never()).save(any());
    }

    @Test
    @DisplayName("カート内アイテムの数量を更新できる")
    void updateUserItemQuantity_UpdatesQuantitySuccessfully() {
        // 初期状態で商品を1つ追加しておく
        Cart existingCart = new Cart();
        existingCart.setCartId(1);
        existingCart.setUserId(1);
        CartItemEntity item = new CartItemEntity();
        item.setProductId(100);
        item.setCartQuantity(1);
        item.setCart(existingCart);
        existingCart.setItems(List.of(item));

        when(dbCartRepository.findByUserId(1)).thenReturn(Optional.of(existingCart));
        when(productRepository.findById(100)).thenReturn(Optional.of(product));

        CartGuest result = userCartService.updateUserItemQuantity("100", 5, cartDto);

        assertThat(result.getItems().get("100").getQuantity()).isEqualTo(5);
        verify(dbCartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("指定された商品をカートから削除できる")
    void removeUserItemFromCart_RemovesItemSuccessfully() {
        Cart existingCart = new Cart();
        existingCart.setCartId(1);
        existingCart.setUserId(1);
        CartItemEntity item = new CartItemEntity();
        item.setProductId(100);
        item.setCartQuantity(1);
        item.setCart(existingCart);
        existingCart.setItems(List.of(item));

        when(dbCartRepository.findByUserId(1)).thenReturn(Optional.of(existingCart));
        when(productRepository.findById(100)).thenReturn(Optional.of(product));

        CartGuest result = userCartService.removeUserItemFromCart("100", cartDto);

        assertThat(result.getItems()).doesNotContainKey("100");
        verify(dbCartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("カートを空にできる")
    void clearUserCart_RemovesAllItems() {
        Cart existingCart = new Cart();
        existingCart.setCartId(1);
        existingCart.setUserId(1);
        CartItemEntity item = new CartItemEntity();
        item.setProductId(100);
        item.setCartQuantity(2);
        item.setCart(existingCart);
        existingCart.setItems(List.of(item));

        when(dbCartRepository.findByUserId(1)).thenReturn(Optional.of(existingCart));
        when(productRepository.findById(100)).thenReturn(Optional.of(product));

        CartGuest result = userCartService.clearUserCart(cartDto);

        assertThat(result.getItems()).isEmpty();
        assertThat(result.getTotalQuantity()).isZero();
        assertThat(result.getTotalPrice()).isZero();
        verify(dbCartRepository).save(any(Cart.class));
    }
}