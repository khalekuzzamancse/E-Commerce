package database

import UserEntity
import database.api.UserListAPIs
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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
    fun testUpdateUser() = runBlocking {
        val user = UserEntity(
            email = "test@example.com",
            name = "Test User",
            hashedPassword = "hashedPassword123",
            cartItems = listOf()
        )
       userListAPIs.addUser(user)
        val retrievedUser = userListAPIs.addToCart("test@example.com")
        assertNotNull(retrievedUser)
        assertTrue(user.name!=retrievedUser.name)
        println("UpdatedName: $retrievedUser")
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


}
