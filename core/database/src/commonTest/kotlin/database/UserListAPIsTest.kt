package database

import CartItemEntity
import UserEntity
import database.api.UserListAPIs
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UserListAPIsTest {

    private val userListAPIs = UserListAPIs()

    @Test
    fun testAddUser() = runBlocking {
        val user = UserEntity(
            email = "test@example.com",
            name = "Test User",
            hashedPassword = "hashedPassword123",
            cartItems = listOf()
        )
        userListAPIs.addUser(user)
        val retrievedUser = userListAPIs.getUserByEmail(user.email)
        assertNotNull(retrievedUser)
        assertEquals(user.name, retrievedUser.name)
        println("Added and retrieved user: $retrievedUser")
    }

    @Test
    fun testGetUserByEmail() = runBlocking {
        val email = "test@example.com"
        val user = UserEntity(email, "Unique Test User", "hashedPassword123", listOf())
        userListAPIs.addUser(user)
        val retrievedUser = userListAPIs.getUserByEmail(email)
        assertNotNull(retrievedUser)
        assertEquals(email, retrievedUser.email)
        println("Retrieved user by email: $retrievedUser")
    }

    @Test
    fun testGetAllUsers() = runBlocking {
        val user1 = UserEntity("user1@example.com", "User One", "hashedPassword1", listOf())
        val user2 = UserEntity("user2@example.com", "User Two", "hashedPassword2", listOf())
        userListAPIs.addUser(user1)
        userListAPIs.addUser(user2)
        val retrievedUsers = userListAPIs.getAllUsers()
        assertTrue(retrievedUsers.isNotEmpty())
        println("All users retrieved: $retrievedUsers")
    }

    @Test
    fun testUpdateUserCart() = runBlocking {
        val email = "updatecart@example.com"
        val user = UserEntity(email, "Cart User", "hashedPasswordUpdate", listOf())
        userListAPIs.addUser(user)  // This should complete and close its transaction

        // Call update in a clean context
        userListAPIs.updateUserCart(email, listOf(
            CartItemEntity(UUID.randomUUID().toString(), "product1", 3),
            CartItemEntity(UUID.randomUUID().toString(), "product2", 1)
        ))

        val updatedUser = userListAPIs.getUserByEmail(email)
        assertNotNull(updatedUser)
        assertEquals(2, updatedUser?.cartItems?.size)
        println("Updated user cart: ${updatedUser?.cartItems}")
    }

    @Test
    fun testRemoveItemFromUserCart() = runBlocking {
        val email = "removeitem@example.com"
        val user = UserEntity(email, "Remove Cart User", "hashedPasswordRemove", listOf())
        val cartItem = CartItemEntity(UUID.randomUUID().toString(), "product1", 2)
        userListAPIs.addUser(user)
        userListAPIs.addItemToUserCart(email, cartItem)
        userListAPIs.removeItemFromUserCart(email, cartItem.id)
        val updatedUser = userListAPIs.getUserByEmail(email)
        assertNotNull(updatedUser)
        assertTrue(updatedUser?.cartItems?.isEmpty() ?: false)
        println("User cart after removing item: ${updatedUser?.cartItems}")
    }
}
