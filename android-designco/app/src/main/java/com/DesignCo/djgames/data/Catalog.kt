package com.DesignCo.djgames.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Gradient
import androidx.compose.material.icons.outlined.HotTub
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.WaterDrop

/** Static studio catalog: retail shelf, service menus and team. */
object Catalog {
    const val MEMBER_DISCOUNT: Double = 0.10

    const val HERO_IMAGE: String =
        "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/7dab1128-40d8-4769-ac2d-405d87a24b52.png"
    const val CLUB_IMAGE: String =
        "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/1cfab92e-17fa-4b1d-ad91-2ea289ba9df1.png"

    val products: List<Product> = listOf(
        Product(
            id = "gold-hour-serum",
            name = "Gold Hour Serum",
            price = 48,
            category = ProductCategory.Hair,
            imageUrl = "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/7fae080f-1e3a-48c3-9de4-bd241b2021de.png",
            tagline = "Stronger, shinier, healthier hair.",
            description = "Our signature repair serum blends argan, camellia and bond-building peptides to seal split ends, tame frizz and leave a mirror-like shine without weight.",
            size = "30 mL · 1 fl oz",
            howToUse = "Warm 2–3 drops between palms and smooth through mid-lengths and ends on damp or dry hair.",
            isNew = true
        ),
        Product(
            id = "matte-clay",
            name = "Matte Clay",
            price = 22,
            category = ProductCategory.Hair,
            imageUrl = "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/6d554c0a-00d7-4677-82bb-9b7ef8e4acae.png",
            tagline = "Strong hold, natural finish.",
            description = "A kaolin and beeswax styling clay that builds texture and separation with all-day hold and zero shine. Reworkable, never crunchy.",
            size = "60 g · 2.1 oz",
            howToUse = "Scrape a fingertip amount, emulsify in palms and work through dry hair from root to tip."
        ),
        Product(
            id = "silk-oil",
            name = "Silk Oil",
            price = 36,
            category = ProductCategory.Hair,
            imageUrl = "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/2b43bc77-335e-4d81-8a19-913cc7fb6362.png",
            tagline = "Smooth, nourish, protect.",
            description = "A featherlight finishing oil with marula and silk amino acids. Protects against heat up to 450°F and adds a soft, silky glow.",
            size = "50 mL · 1.7 fl oz",
            howToUse = "Apply 1–2 pumps before blow-drying or as a finishing touch to flyaways."
        ),
        Product(
            id = "beard-balm",
            name = "Beard Balm",
            price = 18,
            category = ProductCategory.Beard,
            imageUrl = "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/10d4720a-cf99-4799-99a6-49f92a10dc4a.png",
            tagline = "Condition, tame, define.",
            description = "Shea butter, jojoba and a whisper of cedarwood soften coarse beard hair and give light, natural hold for a groomed shape.",
            size = "56 g · 2 oz",
            howToUse = "Rub a small amount between fingers, then work into the beard and shape with a comb."
        ),
        Product(
            id = "velvet-cream",
            name = "Velvet Cream",
            price = 42,
            category = ProductCategory.Skin,
            imageUrl = "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/40f4f598-5f2f-49ef-a34e-2a40ce5cd34e.png",
            tagline = "Deep moisture, soft-focus glow.",
            description = "A rich yet breathable face cream with squalane, niacinamide and ceramides that restores the skin barrier and leaves a velvety finish.",
            size = "50 mL · 1.7 fl oz",
            howToUse = "Massage a pearl-sized amount over clean skin morning and night.",
            isNew = true
        ),
        Product(
            id = "argan-mask",
            name = "Argan Mask",
            price = 30,
            category = ProductCategory.Hair,
            imageUrl = "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/3d7d61a5-e4e3-4598-907e-5612185e29bd.png",
            tagline = "Nourishes, repairs, softens.",
            description = "A weekly intensive treatment with cold-pressed argan oil and keratin to revive dry, color-treated or heat-stressed hair.",
            size = "200 mL · 6.8 fl oz",
            howToUse = "After shampoo, apply to lengths, leave for 5–10 minutes, then rinse thoroughly."
        ),
        Product(
            id = "beard-oil",
            name = "Beard Oil",
            price = 24,
            category = ProductCategory.Beard,
            imageUrl = "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/02bfbae4-5a5a-47b8-b51f-5a4b7fca6fa9.png",
            tagline = "Soften, soothe, scent.",
            description = "A blend of argan, hemp seed and sandalwood that softens the beard and soothes the skin beneath. Absorbs fast, no grease.",
            size = "30 mL · 1 fl oz",
            howToUse = "Massage 3–5 drops into beard and skin after showering."
        ),
        Product(
            id = "gold-comb",
            name = "Gold Detangler",
            price = 28,
            category = ProductCategory.Tools,
            imageUrl = "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/62991ff0-66e5-4f22-8688-fb119a050246.png",
            tagline = "Glide through, never snag.",
            description = "A brushed-gold, wide-tooth comb with polished, seamless teeth that detangle wet hair gently. Comes with a leather travel pouch.",
            size = "19 cm · 7.5 in",
            howToUse = "Work from ends to roots on damp hair after conditioning."
        )
    )

    val heroProduct: Product get() = products.first()

    val featured: List<Product>
        get() = listOfNotNull(product("gold-hour-serum"), product("matte-clay"), product("silk-oil"), product("velvet-cream"))

    fun product(id: String): Product? = products.firstOrNull { it.id == id }

    val services: List<BeautyService> = listOf(
        BeautyService(
            "cut-style", "Signature Cut & Style", 85, 60, ServiceKind.Salon,
            Icons.Outlined.ContentCut, "Consultation, wash, precision cut and finish.", 6
        ),
        BeautyService(
            "blowout", "Blowout", 55, 45, ServiceKind.Salon,
            Icons.Outlined.Air, "Wash and a smooth, voluminous round-brush finish.", 2
        ),
        BeautyService(
            "full-color", "Full Color", 140, 120, ServiceKind.Salon,
            Icons.Outlined.Palette, "Root-to-end single process color with gloss.", 4
        ),
        BeautyService(
            "balayage", "Balayage", 220, 180, ServiceKind.Salon,
            Icons.Outlined.Brush, "Hand-painted, sun-kissed dimension and toner.", 12
        ),
        BeautyService(
            "gloss", "Gloss Treatment", 65, 45, ServiceKind.Salon,
            Icons.Outlined.WaterDrop, "Shine-boosting gloss to refresh tone.", 4
        ),
        BeautyService(
            "keratin", "Keratin Smoothing", 250, 150, ServiceKind.Salon,
            Icons.Outlined.AutoAwesome, "Frizz-free smoothing that lasts for months.", 16
        ),
        BeautyService(
            "classic-cut", "Classic Cut", 40, 30, ServiceKind.Barber,
            Icons.Outlined.ContentCut, "Scissor cut, neckline cleanup and style.", 3
        ),
        BeautyService(
            "skin-fade", "Skin Fade", 48, 45, ServiceKind.Barber,
            Icons.Outlined.Gradient, "Razor-sharp fade blended to the skin.", 2
        ),
        BeautyService(
            "beard-sculpt", "Beard Sculpt", 30, 30, ServiceKind.Barber,
            Icons.Outlined.Face, "Shape, line and condition the beard.", 2
        ),
        BeautyService(
            "hot-towel", "Hot Towel Shave", 45, 40, ServiceKind.Barber,
            Icons.Outlined.HotTub, "Traditional straight-razor shave with hot towels.", 3
        ),
        BeautyService(
            "cut-beard", "Cut & Beard", 70, 60, ServiceKind.Barber,
            Icons.Outlined.AutoAwesome, "The full works: cut, fade and beard sculpt.", 3
        ),
        BeautyService(
            "kids-cut", "Kids Cut", 28, 30, ServiceKind.Barber,
            Icons.Outlined.ChildCare, "Patient, careful cuts for young clients.", 6
        )
    )

    /** Inspiration looks a guest can save and share with their stylist. */
    val inspiration: List<InspirationItem> = listOf(
        InspirationItem(
            "insp-1", "The Gold Hour Glow",
            "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/7fae080f-1e3a-48c3-9de4-bd241b2021de.png"
        ),
        InspirationItem(
            "insp-2", "Soft Matte Texture",
            "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/6d554c0a-00d7-4677-82bb-9b7ef8e4acae.png"
        ),
        InspirationItem(
            "insp-3", "Silk Finish",
            "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/2b43bc77-335e-4d81-8a19-913cc7fb6362.png"
        ),
        InspirationItem(
            "insp-4", "Velvet Skin",
            "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/40f4f598-5f2f-49ef-a34e-2a40ce5cd34e.png"
        ),
        InspirationItem(
            "insp-5", "Deep Repair Ritual",
            "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/3d7d61a5-e4e3-4598-907e-5612185e29bd.png"
        ),
        InspirationItem(
            "insp-6", "The Stylist's Studio",
            "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/7dab1128-40d8-4769-ac2d-405d87a24b52.png"
        )
    )

    fun inspirationItem(id: String): InspirationItem? = inspiration.firstOrNull { it.id == id }

    fun services(kind: ServiceKind): List<BeautyService> = services.filter { it.kind == kind }

    val stylists: List<Stylist> = listOf(
        Stylist("ava", "Ava", "Senior Stylist", "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/6a141583-decc-4bb9-b860-1fdc87db830c.png", ServiceKind.Salon),
        Stylist("sofia", "Sofia", "Color Specialist", "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/28ad2d45-1cf6-42a9-a78c-da1f32e1ee9f.png", ServiceKind.Salon),
        Stylist("nia", "Nia", "Texture Expert", "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/0e1d0c94-4284-4995-b1f5-593be736920b.png", ServiceKind.Salon),
        Stylist("marcus", "Marcus", "Master Barber", "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/d8ed2e21-62fb-4c32-9327-72bbc9e62331.png", ServiceKind.Barber),
        Stylist("leo", "Leo", "Barber", "https://qpqw2c3lrglv1ionjkwxt.rork.app/~assets/img/cac9b665-bce8-4925-bfd1-ce6f9f214e72.png", ServiceKind.Barber)
    )

    fun stylists(kind: ServiceKind): List<Stylist> = stylists.filter { it.kind == kind }
}
