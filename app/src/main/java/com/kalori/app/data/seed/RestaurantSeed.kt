package com.kalori.app.data.seed

import com.kalori.app.domain.model.Food
import com.kalori.app.domain.model.FoodSource
import com.kalori.app.domain.model.Nutrition
import com.kalori.app.domain.model.NutritionBasis
import com.kalori.app.domain.model.Portion
import java.time.Instant

/**
 * Pre-seeded Norwegian restaurant-chain foods that publish nutrition: McDonald's, Burger King,
 * Subway, Max. PER_UNIT (one serving = the item as sold). Stable ids so re-seeding is
 * idempotent. Values are published per-item figures (kcal + macros); micros unknown (null).
 *
 * Not exhaustive menus — a representative starter set for the most-logged items.
 */
object RestaurantSeed {

    fun foods(now: Instant = Instant.now()): List<Food> = listOf(
        // --- McDonald's ---
        restaurant("rest-mcd-bigmac", "Big Mac", "McDonald's", now,
            Nutrition(kcal = 503.0, protein = 26.0, carbs = 41.0, sugars = 8.0, fat = 26.0, saturatedFat = 9.0, fiber = 3.0, salt = 2.3)),
        restaurant("rest-mcd-cheeseburger", "Cheeseburger", "McDonald's", now,
            Nutrition(kcal = 301.0, protein = 16.0, carbs = 31.0, sugars = 6.0, fat = 12.0, saturatedFat = 5.5, fiber = 2.0, salt = 1.7)),
        restaurant("rest-mcd-mcchicken", "McChicken", "McDonald's", now,
            Nutrition(kcal = 417.0, protein = 17.0, carbs = 39.0, sugars = 4.0, fat = 21.0, saturatedFat = 2.5, fiber = 3.0, salt = 1.6)),
        restaurant("rest-mcd-fries-medium", "Pommes frites medium", "McDonald's", now,
            Nutrition(kcal = 337.0, protein = 4.0, carbs = 42.0, sugars = 0.5, fat = 16.0, saturatedFat = 1.5, fiber = 4.0, salt = 0.6)),

        // --- Burger King ---
        restaurant("rest-bk-whopper", "Whopper", "Burger King", now,
            Nutrition(kcal = 657.0, protein = 28.0, carbs = 49.0, sugars = 11.0, fat = 37.0, saturatedFat = 11.0, fiber = 3.0, salt = 2.6)),
        restaurant("rest-bk-cheeseburger", "Cheeseburger", "Burger King", now,
            Nutrition(kcal = 300.0, protein = 16.0, carbs = 27.0, sugars = 6.0, fat = 14.0, saturatedFat = 6.5, fiber = 1.5, salt = 1.6)),
        restaurant("rest-bk-chicken-royale", "Chicken Royale", "Burger King", now,
            Nutrition(kcal = 590.0, protein = 23.0, carbs = 52.0, sugars = 5.0, fat = 31.0, saturatedFat = 3.5, fiber = 3.0, salt = 2.0)),

        // --- Subway (15 cm / 6-inch) ---
        restaurant("rest-sub-chicken-teriyaki", "Sweet Onion Chicken Teriyaki 15 cm", "Subway", now,
            Nutrition(kcal = 355.0, protein = 26.0, carbs = 52.0, sugars = 14.0, fat = 4.5, saturatedFat = 1.2, fiber = 4.0, salt = 1.8)),
        restaurant("rest-sub-turkey", "Turkey Breast 15 cm", "Subway", now,
            Nutrition(kcal = 254.0, protein = 18.0, carbs = 44.0, sugars = 6.0, fat = 3.5, saturatedFat = 1.0, fiber = 4.0, salt = 1.7)),
        restaurant("rest-sub-meatball", "Meatball Marinara 15 cm", "Subway", now,
            Nutrition(kcal = 433.0, protein = 19.0, carbs = 52.0, sugars = 9.0, fat = 17.0, saturatedFat = 7.0, fiber = 5.0, salt = 2.2)),

        // --- Max ---
        restaurant("rest-max-original-boost", "Original Boost", "Max", now,
            Nutrition(kcal = 540.0, protein = 30.0, carbs = 39.0, sugars = 9.0, fat = 28.0, saturatedFat = 10.0, fiber = 4.0, salt = 2.5)),
        restaurant("rest-max-framericano", "Framericano", "Max", now,
            Nutrition(kcal = 470.0, protein = 24.0, carbs = 38.0, sugars = 8.0, fat = 24.0, saturatedFat = 9.0, fiber = 3.0, salt = 2.2)),
        restaurant("rest-max-fries", "Pommes frites", "Max", now,
            Nutrition(kcal = 350.0, protein = 4.0, carbs = 44.0, sugars = 0.5, fat = 17.0, saturatedFat = 1.8, fiber = 4.0, salt = 0.7)),
    )

    private fun restaurant(
        id: String,
        name: String,
        brand: String,
        now: Instant,
        nutrition: Nutrition,
    ): Food = Food(
        id = id,
        name = name,
        brand = brand,
        barcode = null,
        source = FoodSource.RESTAURANT,
        sourceRef = brand,
        basis = NutritionBasis.PER_UNIT,
        nutrition = nutrition,
        defaultPortion = Portion(label = "Stk", units = 1.0),
        createdAt = now,
        lastUsedAt = null,
    )
}
