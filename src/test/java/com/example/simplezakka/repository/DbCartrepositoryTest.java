package com.example.simplezakka.repository;

import com.example.simplezakka.entity.Cart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DbCartrepositoryTest {

    @Autowired
    private DbCartrepository cartRepository;

    @Test
    @DisplayName("saveしてfindByUserIdで取得")
    void saveAndFindByUserId_Success() {
        // given
        Cart cart = new Cart();
        cart.setUserId(1);
        Cart savedCart = cartRepository.save(cart);

        // when
        Optional<Cart> foundCart = cartRepository.findByUserId(1);

        // then
        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getUserId()).isEqualTo(savedCart.getUserId());
    }

    @Test
    @DisplayName("存在しないuserIdのfindByUserIdは空のOptionalを返す")
    void findByUserId_WhenNotExists_ShouldReturnEmpty() {
        Optional<Cart> foundCart = cartRepository.findByUserId(999);
        assertThat(foundCart).isEmpty();
    }

    @Test
    @DisplayName("存在するuserIdをdeleteByUserIdで削除")
    void deleteByUserId_WhenExists_ShouldDeleteCart() {
        // given
        Cart cart = new Cart();
        cart.setUserId(1);
        cartRepository.save(cart);

        // when
        cartRepository.deleteByUserId(2);

        // then
        Optional<Cart> foundCart = cartRepository.findByUserId(2);
        assertThat(foundCart).isEmpty();
    }

    @Test
    @DisplayName("存在しないuserIdのdeleteByUserIdでも例外が発生しない")
    void deleteByUserId_WhenNotExists_ShouldNotThrowException() {
        // when & then
        cartRepository.deleteByUserId(999);
    }
}
