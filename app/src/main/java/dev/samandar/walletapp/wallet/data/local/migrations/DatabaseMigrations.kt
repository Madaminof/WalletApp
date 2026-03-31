package dev.samandar.walletapp.wallet.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `currency_rates` (
                    `code` TEXT NOT NULL, 
                    `rate` REAL NOT NULL, 
                    `lastUpdated` INTEGER NOT NULL, 
                    PRIMARY KEY(`code`)
                )
                """.trimIndent()
            )
        }
    }
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // 1. originalAmount - REAL (Double) turi, default 0.0
            db.execSQL("ALTER TABLE transactions ADD COLUMN originalAmount REAL NOT NULL DEFAULT 0.0")

            // 2. originalCurrency - TEXT (String) turi, default 'UZS'
            db.execSQL("ALTER TABLE transactions ADD COLUMN originalCurrency TEXT NOT NULL DEFAULT 'UZS'")

            // 3. exchangeRate - REAL (Double) turi, default 1.0
            db.execSQL("ALTER TABLE transactions ADD COLUMN exchangeRate REAL NOT NULL DEFAULT 1.0")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // 1. Yangi ustunlar qo'shilgan eski jadvalni vaqtinchalik saqlaymiz
            // Avval yangi ustunlarni qo'shib olamiz (Crash bermasligi uchun)
            db.execSQL("ALTER TABLE accounts ADD COLUMN type TEXT NOT NULL DEFAULT 'CASH'")
            db.execSQL("ALTER TABLE accounts ADD COLUMN currencyCode TEXT NOT NULL DEFAULT 'UZS'")
            db.execSQL("ALTER TABLE accounts ADD COLUMN isDefault INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE accounts ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE accounts ADD COLUMN cardNumber TEXT")
            db.execSQL("ALTER TABLE accounts ADD COLUMN cardProvider TEXT")
            db.execSQL("ALTER TABLE accounts ADD COLUMN bankName TEXT")

            // 2. Yangi sxema bo'yicha vaqtinchalik jadval yaratamiz
            db.execSQL("""
            CREATE TABLE accounts_new (
                id TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                balance REAL NOT NULL,
                colorHex TEXT NOT NULL DEFAULT '#4CAF50',
                iconResId INTEGER,
                type TEXT NOT NULL DEFAULT 'CASH',
                currencyCode TEXT NOT NULL DEFAULT 'UZS',
                isDefault INTEGER NOT NULL DEFAULT 0,
                createdAt INTEGER NOT NULL DEFAULT 0,
                cardNumber TEXT,
                cardProvider TEXT,
                bankName TEXT
            )
        """.trimIndent())

            // 3. Ma'lumotlarni ko'chiramiz (NULL colorHex'larni default rangga almashtiramiz)
            db.execSQL("""
            INSERT INTO accounts_new (id, name, balance, colorHex, iconResId, type, currencyCode, isDefault, createdAt, cardNumber, cardProvider, bankName)
            SELECT id, name, balance, IFNULL(colorHex, '#4CAF50'), iconResId, type, currencyCode, isDefault, createdAt, cardNumber, cardProvider, bankName
            FROM accounts
        """.trimIndent())

            // 4. Eski jadvalni o'chirib, yangisini nomini o'zgartiramiz
            db.execSQL("DROP TABLE accounts")
            db.execSQL("ALTER TABLE accounts_new RENAME TO accounts")
        }
    }

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Transactions jadvaliga statistika uchun amountInBase ustunini qo'shamiz
            // REAL (Double) turi, null bo'lmasligi uchun DEFAULT 0.0 beramiz
            db.execSQL("ALTER TABLE transactions ADD COLUMN amountInBase REAL NOT NULL DEFAULT 0.0")
        }
    }

    val MIGRATION_5_6 = object : Migration(5, 6) { // 1 dan 2 ga o'tayotgan bo'lsang
        override fun migrate(db: SupportSQLiteDatabase) {
            // accounts jadvaliga yangi Double (REAL) ustun qo'shish
            db.execSQL(
                "ALTER TABLE accounts ADD COLUMN amountCurrencyKonverter REAL NOT NULL DEFAULT 0.0"
            )
        }
    }

    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // 1. bills jadvali (isScanned va fiscalSign bilan)
            database.execSQL("""
            CREATE TABLE IF NOT EXISTS `bills` (
                `id` TEXT NOT NULL, 
                `title` TEXT NOT NULL, 
                `date` INTEGER NOT NULL, 
                `serviceChargePercent` REAL NOT NULL, 
                `taxPercent` REAL NOT NULL, 
                `discountAmount` REAL NOT NULL, 
                `totalAmount` REAL NOT NULL, 
                `currency` TEXT NOT NULL, 
                `fiscalSign` TEXT, 
                `isScanned` INTEGER NOT NULL, 
                PRIMARY KEY(`id`)
            )
        """.trimIndent())

            // 2. bill_items jadvali (splitStrategy qo'shildi)
            database.execSQL("""
            CREATE TABLE IF NOT EXISTS `bill_items` (
                `id` TEXT NOT NULL, 
                `billId` TEXT NOT NULL, 
                `itemName` TEXT NOT NULL, 
                `price` REAL NOT NULL, 
                `quantity` REAL NOT NULL, 
                `splitStrategy` TEXT NOT NULL, 
                PRIMARY KEY(`id`),
                FOREIGN KEY(`billId`) REFERENCES `bills`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE 
            )
        """.trimIndent())

            // 3. participants jadvali
            database.execSQL("""
            CREATE TABLE IF NOT EXISTS `participants` (
                `id` TEXT NOT NULL, 
                `billId` TEXT NOT NULL, 
                `name` TEXT NOT NULL, 
                PRIMARY KEY(`id`),
                FOREIGN KEY(`billId`) REFERENCES `bills`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE 
            )
        """.trimIndent())

            // 4. item_assignments jadvali (id Long autoGenerate bo'lgani uchun INTEGER PRIMARY KEY AUTOINCREMENT)
            database.execSQL("""
            CREATE TABLE IF NOT EXISTS `item_assignments` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `itemId` TEXT NOT NULL, 
                `participantId` TEXT NOT NULL, 
                `share` REAL NOT NULL, 
                FOREIGN KEY(`itemId`) REFERENCES `bill_items`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, 
                FOREIGN KEY(`participantId`) REFERENCES `participants`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE 
            )
        """.trimIndent())

            // Indekslar (Entity klasslaringizda ko'rsatilganidek)
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_bill_items_billId` ON `bill_items` (`billId`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_participants_billId` ON `participants` (`billId`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_item_assignments_itemId` ON `item_assignments` (`itemId`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_item_assignments_participantId` ON `item_assignments` (`participantId`)")
        }
    }

    fun getAllMigrations(): Array<Migration> {
        return arrayOf(MIGRATION_1_2,MIGRATION_2_3,MIGRATION_3_4,MIGRATION_4_5,MIGRATION_5_6,MIGRATION_6_7)
    }
}