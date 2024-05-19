package database.schema

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.UUID

internal class ProductSchema : RealmObject {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var name: String = ""
    var images: RealmList<String> = realmListOf()
    var price: Int = 0
    var description: String = ""
    var type: String = ""
    var amountAvailable: Int = 0

    fun toEntity() = ProductEntity(
        id = id,
        name = name,
        images = images.toList(),
        price = price,
        description = description,
        type = type,
        amountAvailable = amountAvailable
    )

    override fun toString(): String {
        return "ProductEntity(id='$id', name='$name', images=$images, price=$price, description='$description', type='$type', amountAvailable=$amountAvailable)"
    }
}
data class ProductEntity(
    val id: String,
    val name: String,
    val images: List<String>,
    val price: Int,
    val description: String,
    val type: String,
    val amountAvailable: Int
)
