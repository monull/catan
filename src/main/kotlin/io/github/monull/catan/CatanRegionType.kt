package io.github.monull.catan

import com.google.common.collect.ImmutableSortedMap
import java.util.*

enum class CatanRegionType(val key: String, val displayName: String, val structure: () -> CatanStructure) {
    IRON("iron", "철", CatanStructure::IronStructure),
    BRICKS("bricks", "벽돌", CatanStructure::BricksStructure),
    DESSERT("dessert", "사막", CatanStructure::DessertStructure),
    FOREST("forest", "숲", CatanStructure::ForestStructure),
    SHEEPS("sheeps", "양", CatanStructure::SheepStructure),
    HAY("hay", "밀", CatanStructure::HayStructure);

    override fun toString(): String {
        return displayName
    }

    companion object {
        private val BY_KEY: Map<String, CatanRegionType>

        init {
            BY_KEY =
                values().associateByTo(TreeMap<String, CatanRegionType>(String.CASE_INSENSITIVE_ORDER)) { type -> type.key }
                    .let { map ->
                        ImmutableSortedMap.copyOf(map)
                    }
        }

        fun byKey(key: String): CatanRegionType? {
            return BY_KEY[key]
        }
    }
}