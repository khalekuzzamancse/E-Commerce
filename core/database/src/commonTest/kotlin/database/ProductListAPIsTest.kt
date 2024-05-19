package database

import database.api.ProductListAPIs
import database.schema.ProductEntity
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ProductListAPIsTest {

    private val productListAPIs = ProductListAPIs()

    @Test
    fun testAddProduct() = runBlocking {
        val product = ProductEntity(
            id = "1",
            name = "Test Product",
            images = listOf("https://example.com/image1.png"),
            price = 100,
            description = "A test product",
            type = "TestType",
            amountAvailable = 10
        )
        productListAPIs.addProduct(product)
        val retrievedProduct = productListAPIs.getProductById(product.id)
        assertNotNull(retrievedProduct)
        assertEquals(product.name, retrievedProduct.name)
    }

    @Test
    fun testGetProductById() = runBlocking {
        val product = ProductEntity(
            id = "1",
            name = "Unique Test Product",
            images = listOf("https://example.com/image2.png"),
            price = 150,
            description = "Another test product",
            type = "Electronics",
            amountAvailable = 5
        )
        productListAPIs.addProduct(product)
        val retrievedProduct = productListAPIs.getProductById(product.id)
        assertNotNull(retrievedProduct)
        assertEquals(product.id, retrievedProduct.id)
    }

    @Test
    fun testGetAllProducts() = runBlocking {
        // First clean up the existing products for consistency in test result
        val existingProducts = productListAPIs.getAllProducts()
        existingProducts.forEach {
            productListAPIs.deleteProduct(it.id)
        }
        // Add new products
        val product1 = ProductEntity(
            id = "2",
            name = "Multi Product 1",
            images = listOf("https://example.com/multi1.png"),
            price = 200,
            description = "Multi product test 1",
            type = "Home Goods",
            amountAvailable = 20
        )
        val product2 = ProductEntity(
            id = "3",
            name = "Multi Product 2",
            images = listOf("https://example.com/multi2.png"),
            price = 300,
            description = "Multi product test 2",
            type = "Garden",
            amountAvailable = 15
        )
        productListAPIs.addProduct(product1)
        productListAPIs.addProduct(product2)
        val retrievedProducts = productListAPIs.getAllProducts()
        assertEquals(2, retrievedProducts.size)
    }

    @Test
    fun testUpdateProduct() = runBlocking {
        val product = ProductEntity(
            id = "1",
            name = "Update Product",
            images = listOf("https://example.com/update.png"),
            price = 250,
            description = "Product before update",
            type = "Update Type",
            amountAvailable = 10
        )
        productListAPIs.addProduct(product)
        // Update the product
        val updatedProduct = product.copy(name = "Updated Product Name", price = 260)
        productListAPIs.updateProduct(updatedProduct)
        val retrievedProduct = productListAPIs.getProductById(product.id)
        assertNotNull(retrievedProduct)
        assertEquals("Updated Product Name", retrievedProduct?.name)
        assertEquals(260, retrievedProduct?.price)
    }

    @Test
    fun testDeleteProduct() = runBlocking {
        val product = ProductEntity(
            id = "1",
            name = "Delete Product",
            images = listOf("https://example.com/delete.png"),
            price = 400,
            description = "Product to delete",
            type = "Disposable",
            amountAvailable = 5
        )
        productListAPIs.addProduct(product)
        // Now delete the product
        productListAPIs.deleteProduct(product.id)
        val retrievedProduct = productListAPIs.getProductById(product.id)
        assertNull(retrievedProduct)
    }
}
