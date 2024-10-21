package com.example.gf5.models

/**
 * Data class representing an advertisement campaign.
 *
 * @property id Unique identifier for the ad.
 * @property campaignName Name of the campaign.
 * @property advertiserName Name of the advertiser.
 * @property status Current status of the ad campaign.
 */
data class Ad private constructor(
    val id: String,
    val campaignName: String,
    val advertiserName: String,
    val status: AdStatus
) {
    companion object {
        /**
         * Factory method to create an instance of [Ad] with validation.
         *
         * @throws IllegalArgumentException if any validation fails.
         */

        fun create(
            id: String,
            campaignName: String,
            advertiserName: String,
            status: AdStatus
        ): Ad {
            require(id.isNotBlank()) { "Ad ID cannot be blank." }
            require(campaignName.isNotBlank()) { "Campaign name cannot be blank." }
            require(advertiserName.isNotBlank()) { "Advertiser name cannot be blank." }
            return Ad(id, campaignName, advertiserName, status)
        }
    }
}
