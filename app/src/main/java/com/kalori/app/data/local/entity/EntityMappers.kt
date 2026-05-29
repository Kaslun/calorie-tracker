package com.kalori.app.data.local.entity

import com.kalori.app.domain.model.Food
import com.kalori.app.domain.model.FoodPortion
import com.kalori.app.domain.model.LogEntry
import com.kalori.app.domain.model.Meal
import com.kalori.app.domain.model.MealComponent
import com.kalori.app.domain.model.TdeeCalibration
import com.kalori.app.domain.model.UserGoal
import com.kalori.app.domain.model.WeightPoint

/** Room entity <-> domain model mappers. The only place persistence types meet domain types. */

fun FoodEntity.toDomain(): Food = Food(
    id = id,
    name = name,
    brand = brand,
    barcode = barcode,
    source = source,
    sourceRef = sourceRef,
    basis = basis,
    nutrition = nutrition.toDomain(),
    defaultPortion = defaultPortion.toDomainOrNull(),
    isFavorite = isFavorite,
    createdAt = createdAt,
    lastUsedAt = lastUsedAt,
    useCount = useCount,
    isArchived = isArchived,
)

fun Food.toEntity(): FoodEntity = FoodEntity(
    id = id,
    name = name,
    brand = brand,
    barcode = barcode,
    source = source,
    sourceRef = sourceRef,
    basis = basis,
    nutrition = nutrition.toEmbedded(),
    defaultPortion = defaultPortion?.toEmbedded(),
    isFavorite = isFavorite,
    createdAt = createdAt,
    lastUsedAt = lastUsedAt,
    useCount = useCount,
    isArchived = isArchived,
)

fun FoodPortionEntity.toDomain(): FoodPortion = FoodPortion(
    id = id,
    foodId = foodId,
    portion = portion.toDomain(),
    isPackageDefault = isPackageDefault,
)

fun FoodPortion.toEntity(): FoodPortionEntity = FoodPortionEntity(
    id = id,
    foodId = foodId,
    portion = portion.toEmbedded(),
    isPackageDefault = isPackageDefault,
)

fun MealEntity.toDomain(): Meal = Meal(
    id = id,
    name = name,
    isFavorite = isFavorite,
    lastUsedAt = lastUsedAt,
    useCount = useCount,
    createdAt = createdAt,
)

fun Meal.toEntity(): MealEntity = MealEntity(
    id = id,
    name = name,
    isFavorite = isFavorite,
    lastUsedAt = lastUsedAt,
    useCount = useCount,
    createdAt = createdAt,
)

fun MealComponentEntity.toDomain(): MealComponent = MealComponent(
    id = id,
    mealId = mealId,
    foodId = foodId,
    portion = portion.toDomain(),
)

fun MealComponent.toEntity(): MealComponentEntity = MealComponentEntity(
    id = id,
    mealId = mealId,
    foodId = foodId,
    portion = portion.toEmbedded(),
)

fun LogEntryEntity.toDomain(): LogEntry = LogEntry(
    id = id,
    date = date,
    timestamp = timestamp,
    mealSlot = mealSlot,
    foodId = foodId,
    sourceMealId = sourceMealId,
    portion = portion.toDomainOrNull(),
    nutrition = nutrition.toDomain(),
    confidence = confidence,
    photoPath = photoPath,
    note = note,
    healthConnectId = healthConnectId,
)

fun LogEntry.toEntity(): LogEntryEntity = LogEntryEntity(
    id = id,
    date = date,
    timestamp = timestamp,
    mealSlot = mealSlot,
    foodId = foodId,
    sourceMealId = sourceMealId,
    portion = portion?.toEmbedded(),
    nutrition = nutrition.toEmbedded(),
    confidence = confidence,
    photoPath = photoPath,
    note = note,
    healthConnectId = healthConnectId,
)

fun WeightPointEntity.toDomain(): WeightPoint = WeightPoint(date = date, kg = kg, source = source)

fun WeightPoint.toEntity(): WeightPointEntity = WeightPointEntity(date = date, kg = kg, source = source)

fun UserGoalEntity.toDomain(): UserGoal = UserGoal(
    id = id,
    sex = sex,
    heightCm = heightCm,
    birthDate = birthDate,
    goalWeightKg = goalWeightKg,
    paceKgPerWeek = paceKgPerWeek,
    proteinTargetG = proteinTargetG,
    proteinTargetMode = proteinTargetMode,
    acceptableRangeKcal = acceptableRangeKcal,
    createdAt = createdAt,
)

fun UserGoal.toEntity(): UserGoalEntity = UserGoalEntity(
    id = id,
    sex = sex,
    heightCm = heightCm,
    birthDate = birthDate,
    goalWeightKg = goalWeightKg,
    paceKgPerWeek = paceKgPerWeek,
    proteinTargetG = proteinTargetG,
    proteinTargetMode = proteinTargetMode,
    acceptableRangeKcal = acceptableRangeKcal,
    createdAt = createdAt,
)

fun TdeeCalibrationEntity.toDomain(): TdeeCalibration = TdeeCalibration(
    id = id,
    effectiveDate = effectiveDate,
    tdee = tdee,
    bmr = bmr,
    basis = basis,
    expectedDeltaKg = expectedDeltaKg,
    actualDeltaKg = actualDeltaKg,
    confirmedByUser = confirmedByUser,
)

fun TdeeCalibration.toEntity(): TdeeCalibrationEntity = TdeeCalibrationEntity(
    id = id,
    effectiveDate = effectiveDate,
    tdee = tdee,
    bmr = bmr,
    basis = basis,
    expectedDeltaKg = expectedDeltaKg,
    actualDeltaKg = actualDeltaKg,
    confirmedByUser = confirmedByUser,
)
