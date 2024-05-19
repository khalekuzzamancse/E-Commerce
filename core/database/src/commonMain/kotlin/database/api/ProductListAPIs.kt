package database.api

import database.DB
import database.data_source.ProductSource
import database.schema.ProductEntity
import database.schema.ProductSchema
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Flow

class ProductListAPIs {

    private val realm = DB.db
    init {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                ProductSource.productsList.forEach {
                    addProduct(it)
                }
            } catch (e: Exception) {

            }
        }

    }

    suspend fun addProduct(product: ProductEntity) {
        withContext(Dispatchers.IO) {
            realm.write {
                copyToRealm(ProductSchema().apply {
                    id = product.id
                    name = product.name
                    images.addAll(product.images)
                    price = product.price
                    description = product.description
                    type = product.type
                    amountAvailable = product.amountAvailable
                })
            }
        }
    }

    suspend fun getProductById(id: String): ProductEntity? {
        return withContext(Dispatchers.IO) {
            realm.query<ProductSchema>("id == $0", id).first().find()?.toEntity()
        }
    }

    suspend fun getAllProducts(): List<ProductEntity> {
        return withContext(Dispatchers.IO) {
            realm.query<ProductSchema>().find().map { it.toEntity() }
        }
    }

    suspend fun getAllProductsFlow() = realm.query<ProductSchema>().asFlow().map { result ->
        result.list.map {
            it.toEntity()
        }
    }


    suspend fun updateProduct(product: ProductEntity) {
        withContext(Dispatchers.IO) {
            realm.write {
                val productSchema = query<ProductSchema>("id == $0", product.id).first().find()
                productSchema?.apply {
                    name = product.name
                    images.clear()
                    images.addAll(product.images)
                    price = product.price
                    description = product.description
                    type = product.type
                    amountAvailable = product.amountAvailable
                }
            }
        }
    }

    suspend fun deleteProduct(id: String) {
        withContext(Dispatchers.IO) {
            realm.write {
                query<ProductSchema>("id == $0", id).first().find()?.let {
                    delete(it)
                }
            }
        }
    }
}
